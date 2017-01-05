package uk.gov.ea.datareturns.domain.jpa.service;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
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
import org.springframework.stereotype.Component;
import uk.gov.ea.datareturns.domain.jpa.dao.SiteDao;
import uk.gov.ea.datareturns.domain.jpa.entities.Site;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The service class to support inverted-index search functionality for site
 * using the apache lucene libraries
 * @author Graham Willis
 */
@Component
public class Search {
    protected static final Logger LOGGER = LoggerFactory.getLogger(Search.class);

    private static final String SITE = "site";
    private final Directory INDEX = new RAMDirectory();
    private final StandardAnalyzer STANDARD_ANALYZER = new StandardAnalyzer();
    private IndexReader reader = null;
    private IndexSearcher searcher = null;
    private QueryParser queryParser = null;

    /**
     * Initialize the site search inverted index. For now a minimal configuration
     * treating the site name as a document.
     * @param siteDao
     * @throws IOException
     */
    @Inject
    public Search(SiteDao siteDao) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(STANDARD_ANALYZER);
        IndexWriter writer = new IndexWriter(INDEX, config);

        // Collect the site, permit and permit alias into single documents and add them to the index
        for(Site site : siteDao.list()) {
            Document document = new Document();
            document.add(new TextField(SITE, site.getName(), Field.Store.YES));
            writer.addDocument(document);
        }
        writer.close();

        // For performance the searcher is shared across multiple searches -
        // because the index does not change. The index reader will be left open
        reader = DirectoryReader.open(INDEX);
        searcher = new IndexSearcher(reader);
        queryParser = new QueryParser(SITE, STANDARD_ANALYZER);
    }

    /**
     * Search for site
     * @param search
     * @return List of searched sites
     */
    public List<Pair<String, String[]>> searchSite(String search)  {
        List<Pair<String, String[]>> results = new ArrayList<>();
        try {
            Query query = queryParser.parse(search);
            int hitsPerPage = 101;

            TopDocs docs = searcher.search(query, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;
            for(int i=0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                List<Term> hitTerms = new ArrayList<>();
                getHitTerms(query, searcher,docId, hitTerms);
                results.add(new ImmutablePair<>(d.get(SITE), hitTerms.stream().map(t -> t.text()).toArray(String[]::new)));
            }
            return results;
        } catch (ParseException|IOException e) {
            LOGGER.error("Unexpected error in the search function");
            return null;
        }
    }

    private void getHitTerms(Query query, IndexSearcher searcher, int docId, List<Term> hitTerms) throws IOException {
        if (query instanceof TermQuery) {
            if (searcher.explain(query, docId).isMatch() == true) {
                hitTerms.add(((TermQuery) query).getTerm());
            }
        }
        if (query instanceof BooleanQuery) {
            List<BooleanClause> clauses = ((BooleanQuery) query).clauses();
            if (clauses == null) return;
            for (BooleanClause bc : clauses) {
                getHitTerms(bc.getQuery(), searcher, docId, hitTerms);
            }
            return;
        }
    }

}
