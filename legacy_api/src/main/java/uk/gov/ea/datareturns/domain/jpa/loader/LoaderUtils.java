package uk.gov.ea.datareturns.domain.jpa.loader;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.input.BOMInputStream;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class to facilitate loading CSV data into the database.
 *
 * @author Sam Gardner-Dell
 */
abstract class LoaderUtils {

    /**
     * Read a CSV file from the given resource path and return a List of Maps containing row data
     *
     * @param path the path to read
     * @return a {@link List} representing each row of data.  Each row contains a {@link Map} of field names to values
     */
    static List<Map<String, String>> readCsvData(String path) {
        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        RowListProcessor rowProcessor = new RowListProcessor();
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(new BOMInputStream(LoaderUtils.class.getResourceAsStream(path)));
        String[] headers = rowProcessor.getHeaders();
        List<String[]> rows = rowProcessor.getRows();

        List<Map<String, String>> data = new ArrayList<>();
        for (String[] rowData : rows) {
            Map<String, String> values = new HashMap<>();
            for (int i = 0; i < rowData.length; i++) {
                values.put(headers[i], rowData[i]);
            }
            data.add(values);
        }
        return data;
    }

    /**
     * Handle persistence of entities that may be aliased via multiple rows which reference each other
     *
     * @param persistenceHandler the underlying persistence handler (usually the add method on the dao)
     * @param data the data read from the csv file
     * @param factory
     * @param <E>
     */
    static <E extends AliasingEntity<E>> void persistSelfReferencingAliases(Consumer<E> persistenceHandler,
            List<Map<String, String>> data,
            Function<Map<String, String>, E> factory) {

        Map<String, E> primaryEntityData = new HashMap<>();
        for (Map<String, String> rowData : data) {
            E entity = factory.apply(rowData);
            if (rowData.get("preferred") == null) {
                primaryEntityData.put(entity.getName(), entity);
            }
        }

        List<E> aliasData = new ArrayList<>();
        for (Map<String, String> rowData : data) {
            E entity = factory.apply(rowData);
            String preferredName = rowData.get("preferred");
            if (preferredName != null) {
                entity.setPreferred(primaryEntityData.get(preferredName));
                aliasData.add(entity);
            }
        }

        primaryEntityData.values().forEach(persistenceHandler);
        aliasData.forEach(persistenceHandler);
    }

    /**
     * Handle persistence of entities that may be aliased via multiple numberd columns
     *
     * @param persistenceHandler the underlying persistence handler (usually the add method on the dao)
     * @param data the data read from the csv file
     * @param factory
     * @param <E>
     */
    static <E extends AliasingEntity<E>> void persistAliasColumnFileType(Consumer<E> persistenceHandler,
            List<Map<String, String>> data,
            Function<Map<String, String>, E> factory) {
        Set<E> primaryEntityData = new HashSet<>();
        Set<E> aliasEntityData = new HashSet<>();
        for (Map<String, String> rowData : data) {
            E primary = factory.apply(rowData);
            primaryEntityData.add(primary);
            int aliasNumber = 1;
            String aliasName;
            // Read aliases from numbered columns starting from 1
            while ((aliasName = rowData.get("alias" + aliasNumber)) != null) {
                E alias = factory.apply(rowData);
                alias.setName(aliasName);
                alias.setPreferred(primary);
                aliasEntityData.add(alias);
                aliasNumber++;
            }
        }
        primaryEntityData.forEach(persistenceHandler);
        aliasEntityData.forEach(persistenceHandler);
    }
}
