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

/**
 * Created by graham on 04/11/16.
 */
@Component
public class Search {

    private final Directory INDEX = new RAMDirectory();
    private final StandardAnalyzer STANDARD_ANALYZER = new StandardAnalyzer();

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
    }

    public void searchSite(String search)  {
        try {
            Query query = new QueryParser("site", STANDARD_ANALYZER).parse(search);
            int hitsPerPage = 10;

            IndexReader reader = DirectoryReader.open(INDEX);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(query, hitsPerPage);
            ScoreDoc[] hits = docs.scoreDocs;

            // 4. display results
            System.out.println("Found " + hits.length + " hits.");
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("site"));
            }

            // reader can only be closed when there
            // is no need to access the documents any more.
            reader.close();

        } catch (ParseException|IOException e) {
            e.printStackTrace();
        }
    }
}
