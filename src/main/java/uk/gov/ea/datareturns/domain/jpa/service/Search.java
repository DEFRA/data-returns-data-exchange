package uk.gov.ea.datareturns.domain.jpa.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
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

    private final Directory INDEX = new RAMDirectory();
    private final StandardAnalyzer STANDARD_ANALYZER = new StandardAnalyzer();
    private IndexReader reader = null;
    private IndexSearcher searcher = null;

    /**
     * Initialize the site search index. For now a minimal configuration
     * treating the site name as a document.
     * @param siteDao
     * @throws IOException
     */
    @Inject
    public Search(SiteDao siteDao) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(STANDARD_ANALYZER);
        IndexWriter writer = new IndexWriter(INDEX, config);

        for(Site site : siteDao.list()) {
            Document document = new Document();
            document.add(new TextField("site", site.getName(), Field.Store.YES));
            writer.addDocument(document);
        }
        writer.close();

        // For performance the searcher is shared across multiple searches -
        // because the index does not change. The index reader will be left open
        reader = DirectoryReader.open(INDEX);
        searcher = new IndexSearcher(reader);
    }

    /**
     * Search for site
     * @param search
     * @return List of searched sites
     */
    public List<String> searchSite(String search)  {
        try {
            Query query = new QueryParser("site", STANDARD_ANALYZER).parse(search);
            int hitsPerPage = 10;

            TopDocs docs = searcher.search(query, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            List<String> results = new ArrayList<>();
            for(int i=0; i<hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                results.add(d.get("site"));
            }
            return results;
        } catch (ParseException|IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
