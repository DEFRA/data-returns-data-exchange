package uk.gov.defra.datareturns.data.loader;

import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import uk.gov.defra.datareturns.data.model.AliasingEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Utility class to facilitate loading CSV data into the database.
 *
 * @author Sam Gardner-Dell
 */
@Slf4j
final class LoaderUtils {
    public static final int CSV_MAX_CHARS_PER_COL = 8000;

    private LoaderUtils() {
    }

    /**
     * Read a CSV file from the given resource path and return a List of Maps containing row data
     *
     * @param path the path to read
     * @return a {@link List} representing each row of data.  Each row contains a {@link Map} of field names to values
     */
    static List<Map<String, String>> readCsvData(final String path) {
        final CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setLineSeparatorDetectionEnabled(true);
        final RowListProcessor rowProcessor = new RowListProcessor();
        parserSettings.setProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);
        parserSettings.setMaxCharsPerColumn(CSV_MAX_CHARS_PER_COL);
        final CsvParser parser = new CsvParser(parserSettings);
        parser.parse(new BOMInputStream(LoaderUtils.class.getResourceAsStream(path)));
        final String[] headers = rowProcessor.getHeaders();
        final List<String[]> rows = rowProcessor.getRows();

        final List<Map<String, String>> data = new ArrayList<>();
        for (final String[] rowData : rows) {
            final Map<String, String> values = new HashMap<>();
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
     * @param persistenceHandler the underlying persistence handler (usually the add method on the repository)
     * @param data               the data read from the csv file
     * @param entityFactory      a factory for creating the persistable entity from a row of CSV data
     * @param <E>
     */
    static <E extends AliasingEntity<E>> void persistSelfReferencingEntityFile(
            final List<Map<String, String>> data,
            final Consumer<E> persistenceHandler,
            final Function<Map<String, String>, E> entityFactory) throws ValidationException {
        persistSelfReferencingEntityFile(
                data,
                persistenceHandler, entityFactory,
                persistenceHandler, entityFactory);
    }


    /**
     * Handle persistence of entities that may be aliased via multiple rows which reference each other
     *
     * @param data                      the data read from the csv file
     * @param primaryPersistenceHandler the persistence handler for the aliased entity
     * @param primaryEntityFactory      factory method for the primary entity
     * @param aliasPersistenceHandler   the persistence handler for the aliasing entity
     * @param aliasEntityFactory        factory method for the aliasing entity
     * @param <E>
     */
    static <E extends MasterDataEntity, A extends AliasingEntity<E>> void persistSelfReferencingEntityFile(
            final List<Map<String, String>> data,
            final Consumer<E> primaryPersistenceHandler,
            final Function<Map<String, String>, E> primaryEntityFactory,
            final Consumer<A> aliasPersistenceHandler,
            final Function<Map<String, String>, A> aliasEntityFactory) throws ValidationException {

        final Map<String, E> primaryEntityData = new LinkedHashMap<>();
        final Map<String, A> aliasesByName = new LinkedHashMap<>();
        final Map<A, String> preferredByAlias = new LinkedHashMap<>();

        for (final Map<String, String> rowData : data) {
            if (rowData.get("preferred") == null) {
                final E entity = primaryEntityFactory.apply(rowData);

                if (primaryEntityData.containsKey(entity.getNomenclature())) {
                    final String msg = "Primary entity " + entity.getNomenclature() + " is duplicated";
                    log.warn(msg);
//                    throw new ValidationException(msg);
                }
                primaryEntityData.put(entity.getNomenclature(), entity);
            } else {
                final A alias = aliasEntityFactory.apply(rowData);

                if (aliasesByName.containsKey(alias.getNomenclature())) {
                    final String msg = "Alias entity " + alias.getNomenclature() + " is duplicated";
                    log.warn(msg);
//                    throw new ValidationException(msg);
                }
                aliasesByName.put(alias.getNomenclature(), alias);
                preferredByAlias.put(alias, rowData.get("preferred"));
            }
        }

        preferredByAlias.forEach((alias, preferred) -> {
            // Check alias doesn't exist in primary list
            if (primaryEntityData.containsKey(alias.getNomenclature())) {
                final String msg = "Alias " + alias.getNomenclature() + " is also defined as a primary entity";
                log.warn(msg);
//                throw new ValidationException(msg);
            }
            final E primary = primaryEntityData.get(preferred);
            if (primary == null) {
                final String msg = "Unable to find preferred value " + preferred;
                log.warn(msg);
//                throw new ValidationException(msg);
            }
            alias.setPreferred(primary);
        });

        primaryEntityData.values().forEach(primaryPersistenceHandler);
        aliasesByName.values().forEach(aliasPersistenceHandler);
    }

    /**
     * Handle persistence of entities that may be aliased via multiple numbered columns
     *
     * @param persistenceHandler the underlying persistence handler (usually the add method on the dao)
     * @param data               the data read from the csv file
     * @param factory
     * @param <E>
     */
    static <E extends AliasingEntity<E>> void persistAliasColumnFileType(final Consumer<E> persistenceHandler,
                                                                         final List<Map<String, String>> data,
                                                                         final Function<Map<String, String>, E> factory) {
        final Set<E> primaryEntityData = new HashSet<>();
        final Set<E> aliasEntityData = new HashSet<>();
        for (final Map<String, String> rowData : data) {
            final E primary = factory.apply(rowData);
            primaryEntityData.add(primary);
            int aliasNumber = 1;
            String aliasName;
            // Read aliases from numbered columns starting from 1
            while ((aliasName = rowData.get("alias" + aliasNumber)) != null) {
                final E alias = factory.apply(rowData);
                alias.setNomenclature(aliasName);
                alias.setPreferred(primary);
                aliasEntityData.add(alias);
                aliasNumber++;
            }
        }
        primaryEntityData.forEach(persistenceHandler);
        aliasEntityData.forEach(persistenceHandler);
    }

    static <E extends MasterDataEntity> Function<Map<String, String>, E> basicFactory(final Supplier<E> entityClassSupplier) {
        return (rowData) -> {
            final E entity = entityClassSupplier.get();
            entity.setNomenclature(rowData.get("name"));
            return entity;
        };
    }


    static Set<String> extractGroupSet(final String cellValue) {
        if (cellValue != null) {
            return new LinkedHashSet<>(Arrays.asList(cellValue.split("\\W+")));
        }
        return Collections.emptySet();
    }
}
