package uk.gov.ea.datareturns.domain.jpa.service;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Site;
import uk.gov.ea.datareturns.domain.jpa.repositories.events.MasterDataUpdateEvent;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The service class to support inverted-index search functionality for site
 * using the apache lucene libraries
 * @author Graham Willis
 */
@Component
public class Search implements ApplicationListener<MasterDataUpdateEvent<?>> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Search.class);

    private static final String SITE = "site";
    private static final Directory INDEX = new RAMDirectory();

    final List<String> stopWords = new ArrayList<>(); // Empty stop words list
    final CharArraySet stopSet = new CharArraySet(stopWords, true);

    private final StandardAnalyzer STANDARD_ANALYZER = new StandardAnalyzer(stopSet);

    private volatile IndexReader reader = null;
    private IndexSearcher searcher = null;
    private QueryParser queryParser = null;
    private final MasterDataCacheService masterDataCacheService;

    /**
     * Initialize the site search inverted index. For now a minimal configuration
     * treating the site name as a document.
     * @param masterDataCacheService the cache of master data.
     * @throws IOException
     */
    @Inject
    public Search(MasterDataCacheService masterDataCacheService) {
        this.masterDataCacheService = masterDataCacheService;
    }

    private void initialize() {
        LOGGER.info("Initializing site/permit indexes");
        // Close the readers of the index - the reader stays open
        // until the index is refreshed

        // Create a new index writer
        IndexWriterConfig config = new IndexWriterConfig(STANDARD_ANALYZER);

        try (IndexWriter writer = new IndexWriter(INDEX, config)) {
            if (reader != null) {
                reader.close();
            }

            writer.deleteAll();

            // Collect the site, permit and permit alias into single documents and add them to the index
            Set<String> cachedSiteNames = masterDataCacheService.getStrictNaturalKeyToPkMap(Site.class).keySet();
            for (String siteName : cachedSiteNames) {
                Document document = new Document();
                document.add(new TextField(SITE, siteName, Field.Store.YES));
                writer.addDocument(document);
            }

            writer.commit();
            Assert.isTrue(writer.numDocs() == cachedSiteNames.size(), "Number of indexed sites should match the site list");
            writer.close();

            // Set up the reader which remains open until a refresh
            // For performance the searcher is shared across multiple searches -
            // because the index does not change. The index reader will be left open
            reader = DirectoryReader.open(INDEX);
            searcher = new IndexSearcher(reader);
            queryParser = new QueryParser(SITE, STANDARD_ANALYZER);
            Assert.isTrue(reader.numDocs() == cachedSiteNames.size(), "Number of indexed sites should match the site list");

        } catch (Exception e) {
            LOGGER.error("Error creating lucene search index.", e);
        }
    }

    private void getHitTerms(Query query, IndexSearcher searcher, int docId, List<Term> hitTerms) throws IOException {
        if (query instanceof TermQuery) {
            if (searcher.explain(query, docId).isMatch()) {
                hitTerms.add(((TermQuery) query).getTerm());
            }
        }
        if (query instanceof BooleanQuery) {
            List<BooleanClause> clauses = ((BooleanQuery) query).clauses();
            if (clauses == null)
                return;
            for (BooleanClause bc : clauses) {
                getHitTerms(bc.getQuery(), searcher, docId, hitTerms);
            }
        }
    }

    /**
     * Search for site
     * @param search
     * @return List of searched sites
     */
    public List<Pair<String, String[]>> searchSite(String search) {
        // Initialize on demand
        if (reader == null) {
            initialize();
        }
        // Do index search
        List<Pair<String, String[]>> results = new ArrayList<>();
        try {
            Query query = queryParser.parse(search);

            int hitsPerPage = 101;

            TopDocs docs = searcher.search(query, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            // Process hits
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                List<Term> hitTerms = new ArrayList<>();
                getHitTerms(query, searcher, docId, hitTerms);
                results.add(new ImmutablePair<>(d.get(SITE), hitTerms.stream().map(Term::text).toArray(String[]::new)));
            }
            return results;
        } catch (ParseException | IOException e) {
            LOGGER.error("Error in search function trying to search: " + search, e);
            return null;
        }
    }

    @Override public void onApplicationEvent(MasterDataUpdateEvent<?> event) {
        Class<?> entityClass = event.getEntityClass();
        if (Site.class.equals(entityClass)) {
            // FIXME: We need a better way to cause a reindex than nulling the reader!  This will cause errors if another thread is using the reader..
            // FIXME: Ideally we should substitute this for hibernate full-text search
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOGGER.error("Potentional resource leak. Failed to close reader.", e);
                }
                reader = null;
            }

        }
    }
}
