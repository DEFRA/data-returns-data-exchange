package uk.gov.ea.datareturns.domain.jpa.loader;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;
import uk.gov.ea.datareturns.domain.jpa.repositories.masterdata.*;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static uk.gov.ea.datareturns.domain.jpa.loader.LoaderUtils.readCsvData;

/**
 * Simple database loader interface for master data
 *
 * @author Sam Gardner-Dell
 */
public interface DatabaseLoader {
    Logger LOGGER = LoggerFactory.getLogger(DatabaseLoader.class);

    /**
     * Load base data into the database
     */
    @Transactional
    void load();

    @Component class SiteAndPermitLoader implements DatabaseLoader {

        final SiteRepository siteRepository;
        final UniqueIdentifierRepository uniqueIdentifierRepository;
        final UniqueIdentifierAliasRepository uniqueIdentifierAliasRepository;

        @Inject public SiteAndPermitLoader(SiteRepository siteRepository,
                UniqueIdentifierRepository uniqueIdentifierRepository,
                UniqueIdentifierAliasRepository uniqueIdentifierAliasRepository) {
            this.siteRepository = siteRepository;
            this.uniqueIdentifierRepository = uniqueIdentifierRepository;
            this.uniqueIdentifierAliasRepository = uniqueIdentifierAliasRepository;
        }

        @Transactional
        @Override public void load() {
            String[] permitFiles = {
                    "/db/data/stage_data_initialization/EA-WML.csv",
                    "/db/data/stage_data_initialization/EPR.csv",
                    "/db/data/stage_data_initialization/IPPC.csv",
                    "/db/data/stage_data_initialization/Pre-EA.csv"
            };
            List<Map<String, String>> data = new ArrayList<>();
            Arrays.stream(permitFiles).forEach(pf -> data.addAll(readCsvData(pf)));

            Map<String, Site> sites = new HashMap<>();
            Map<String, UniqueIdentifier> primaryIdentifiers = new HashMap<>();

            for (Map<String, String> rowData : data) {
                String siteName = rowData.get("SITE");
                Site site = sites.computeIfAbsent(siteName, (sn) -> {
                    Site st = new Site();
                    st.setName(sn);
                    return st;
                });

                String primaryPermit = rowData.get("EA_ID");
                UniqueIdentifier primaryId = primaryIdentifiers.computeIfAbsent(primaryPermit, (ps) -> {
                    UniqueIdentifier id = new UniqueIdentifier();
                    id.setName(ps);
                    id.setSite(site);
                    return id;
                });

                Set<UniqueIdentifierAlias> historicalIdentifiers = new HashSet<>();
                String alternative = rowData.get("ALTERNATIVES");
                if (StringUtils.isNotEmpty(alternative)) {
                    // Alternatives column only holds a single value
                    UniqueIdentifierAlias alias = new UniqueIdentifierAlias();
                    alias.setName(alternative);
                    alias.setPreferred(primaryId);
                    historicalIdentifiers.add(alias);
                }
                primaryId.setAliases(historicalIdentifiers);
            }

            // Load all sites
            siteRepository.save(sites.values());
            siteRepository.flush();
            uniqueIdentifierRepository.save(primaryIdentifiers.values());
            uniqueIdentifierRepository.flush();

            // Sanity check
            List<String> aliasNames = uniqueIdentifierAliasRepository.findAll().stream()
                    .map(UniqueIdentifierAlias::getName)
                    .collect(Collectors.toList());
            List<String> primaryNames = uniqueIdentifierRepository.findAll().stream()
                    .map(UniqueIdentifier::getName)
                    .collect(Collectors.toList());
            List<String> duplicates = ListUtils.intersection(primaryNames, aliasNames);
            if (!duplicates.isEmpty()) {
                LOGGER.error("*** Duplicates were found in both the primary and alias permit lists: " + duplicates.toString() + " ***");
            }
        }
    }

    @Component class MethodsOrStandardsLoader implements DatabaseLoader {
        final MethodOrStandardRepository methodOrStandardRepository;

        @Inject public MethodsOrStandardsLoader(MethodOrStandardRepository methodOrStandardRepository) {
            this.methodOrStandardRepository = methodOrStandardRepository;
        }

        @Transactional
        @Override public void load() {
            List<Map<String, String>> data = readCsvData("/db/data/MethodsOrStandards.csv");
            for (Map<String, String> rowData : data) {
                MethodOrStandard entity = new MethodOrStandard();
                entity.setName(rowData.get("name"));
                entity.setNotes(rowData.get("notes"));
                methodOrStandardRepository.saveAndFlush(entity);
            }
        }
    }

    @Component class QualifiersLoader implements DatabaseLoader {
        final QualifierRepository qualifierRepository;

        @Inject public QualifiersLoader(QualifierRepository qualifierRepository) {
            this.qualifierRepository = qualifierRepository;
        }

        @Transactional
        @Override public void load() {
            List<Map<String, String>> data = readCsvData("/db/data/Qualifiers.csv");
            for (Map<String, String> rowData : data) {
                Qualifier entity = new Qualifier();
                entity.setName(rowData.get("name"));
                entity.setNotes(rowData.get("notes"));
                entity.setSingleOrMultiple(rowData.get("singleormultiple"));
                entity.setType(rowData.get("type"));
                qualifierRepository.saveAndFlush(entity);
            }
        }
    }

    @Component class ReleasesAndTransfersLoader implements DatabaseLoader {
        final ReleasesAndTransfersRepository releasesAndTransfersRepository;

        @Inject public ReleasesAndTransfersLoader(ReleasesAndTransfersRepository releasesAndTransfersRepository) {
            this.releasesAndTransfersRepository = releasesAndTransfersRepository;
        }

        @Transactional
        @Override public void load() {
            List<Map<String, String>> data = readCsvData("/db/data/ReleasesAndTransfers.csv");
            for (Map<String, String> rowData : data) {
                ReleasesAndTransfers entity = new ReleasesAndTransfers();
                entity.setName(rowData.get("name"));
                releasesAndTransfersRepository.saveAndFlush(entity);
            }
        }
    }

    @Component class ReturnPeriodsLoader implements DatabaseLoader {
        final ReturnPeriodRepository returnPeriodRepository;

        @Inject public ReturnPeriodsLoader(ReturnPeriodRepository returnPeriodRepository) {
            this.returnPeriodRepository = returnPeriodRepository;
        }

        @Transactional
        @Override public void load() {
            List<Map<String, String>> data = readCsvData("/db/data/ReturnPeriods.csv");
            for (Map<String, String> rowData : data) {
                ReturnPeriod entity = new ReturnPeriod();
                entity.setName(rowData.get("name"));
                entity.setDefinition(rowData.get("definition"));
                entity.setExample(rowData.get("example"));
                returnPeriodRepository.saveAndFlush(entity);
            }
        }
    }

    @Component class ReturnTypesLoader implements DatabaseLoader {
        final ReturnTypeRepository returnTypeRepository;

        @Inject public ReturnTypesLoader(ReturnTypeRepository returnTypeRepository) {
            this.returnTypeRepository = returnTypeRepository;
        }

        @Transactional
        @Override public void load() {
            List<Map<String, String>> data = readCsvData("/db/data/ReturnTypes.csv");
            for (Map<String, String> rowData : data) {
                ReturnType entity = new ReturnType();
                entity.setName(rowData.get("name"));
                entity.setSector(rowData.get("sector"));
                returnTypeRepository.saveAndFlush(entity);
            }
        }
    }

    @Component class TextValuesLoader implements DatabaseLoader {
        final TextValueRepository textValueRepository;

        @Inject public TextValuesLoader(TextValueRepository textValueRepository) {
            this.textValueRepository = textValueRepository;
        }

        @Transactional
        @Override public void load() {
            List<Map<String, String>> data = readCsvData("/db/data/TextValues.csv");
            LoaderUtils.persistSelfReferencingAliases(textValueRepository::save, data, rowData -> {
                TextValue entity = new TextValue();
                entity.setName(rowData.get("name"));
                return entity;
            });
        }
    }

    @Component class ReferencePeriodsLoader implements DatabaseLoader {
        final ReferencePeriodRepository referencePeriodRepository;

        @Inject public ReferencePeriodsLoader(ReferencePeriodRepository referencePeriodRepository) {
            this.referencePeriodRepository = referencePeriodRepository;
        }

        @Transactional
        @Override public void load() {
            List<Map<String, String>> data = readCsvData("/db/data/ReferencePeriods.csv");
            LoaderUtils.persistSelfReferencingAliases(referencePeriodRepository::save, data, rowData -> {
                ReferencePeriod entity = new ReferencePeriod();
                entity.setName(rowData.get("name"));
                entity.setNotes(rowData.get("notes"));
                return entity;
            });
        }
    }

    @Component class UnitsLoader implements DatabaseLoader {
        final UnitRepository unitRepository;

        @Inject public UnitsLoader(UnitRepository unitRepository) {
            this.unitRepository = unitRepository;
        }

        @Override public void load() {
            List<Map<String, String>> data = readCsvData("/db/data/Units.csv");
            LoaderUtils.persistSelfReferencingAliases(unitRepository::save, data, rowData -> {
                Unit entity = new Unit();
                entity.setName(rowData.get("name"));
                entity.setDescription(rowData.get("description"));
                entity.setLongName(rowData.get("long_name"));
                entity.setType(rowData.get("type"));
                entity.setUnicode(rowData.get("unicode"));
                return entity;
            });
        }
    }

    @Component class ParametersLoader implements DatabaseLoader {
        final ParameterRepository parameterRepository;

        @Inject public ParametersLoader(ParameterRepository parameterRepository) {
            this.parameterRepository = parameterRepository;
        }

        @Transactional
        @Override public void load() {
            List<Map<String, String>> data = readCsvData("/db/data/Parameters.csv");
            LoaderUtils.persistAliasColumnFileType(parameterRepository::save, data, rowData -> {
                Parameter entity = new Parameter();
                entity.setName(rowData.get("name"));
                entity.setCas(rowData.get("cas"));
                entity.setType(rowData.get("type"));
                return entity;
            });
        }
    }
}
