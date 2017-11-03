package uk.gov.defra.datareturns.data.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifier;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifierAlias;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifierAliasRepository;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifierRepository;
import uk.gov.defra.datareturns.data.model.methodorstandard.MethodOrStandard;
import uk.gov.defra.datareturns.data.model.methodorstandard.MethodOrStandardRepository;
import uk.gov.defra.datareturns.data.model.nace.NaceClass;
import uk.gov.defra.datareturns.data.model.nace.NaceClassRepository;
import uk.gov.defra.datareturns.data.model.nace.NaceDivision;
import uk.gov.defra.datareturns.data.model.nace.NaceDivisionRepository;
import uk.gov.defra.datareturns.data.model.nace.NaceGroup;
import uk.gov.defra.datareturns.data.model.nace.NaceGroupRepository;
import uk.gov.defra.datareturns.data.model.nace.NaceSection;
import uk.gov.defra.datareturns.data.model.nace.NaceSectionRepository;
import uk.gov.defra.datareturns.data.model.parameter.Parameter;
import uk.gov.defra.datareturns.data.model.parameter.ParameterAlias;
import uk.gov.defra.datareturns.data.model.parameter.ParameterAliasRepository;
import uk.gov.defra.datareturns.data.model.parameter.ParameterRepository;
import uk.gov.defra.datareturns.data.model.qualifier.Qualifier;
import uk.gov.defra.datareturns.data.model.qualifier.QualifierRepository;
import uk.gov.defra.datareturns.data.model.referenceperiod.ReferencePeriod;
import uk.gov.defra.datareturns.data.model.referenceperiod.ReferencePeriodAlias;
import uk.gov.defra.datareturns.data.model.referenceperiod.ReferencePeriodAliasRepository;
import uk.gov.defra.datareturns.data.model.referenceperiod.ReferencePeriodRepository;
import uk.gov.defra.datareturns.data.model.releaseortransfer.ReleaseOrTransfer;
import uk.gov.defra.datareturns.data.model.releaseortransfer.ReleaseOrTransferRepository;
import uk.gov.defra.datareturns.data.model.returnperiod.ReturnPeriod;
import uk.gov.defra.datareturns.data.model.returnperiod.ReturnPeriodRepository;
import uk.gov.defra.datareturns.data.model.returntype.ReturnType;
import uk.gov.defra.datareturns.data.model.returntype.ReturnTypeRepository;
import uk.gov.defra.datareturns.data.model.site.Site;
import uk.gov.defra.datareturns.data.model.site.SiteRepository;
import uk.gov.defra.datareturns.data.model.textvalue.TextValue;
import uk.gov.defra.datareturns.data.model.textvalue.TextValueAlias;
import uk.gov.defra.datareturns.data.model.textvalue.TextValueAliasRepository;
import uk.gov.defra.datareturns.data.model.textvalue.TextValueRepository;
import uk.gov.defra.datareturns.data.model.unit.Unit;
import uk.gov.defra.datareturns.data.model.unit.UnitAlias;
import uk.gov.defra.datareturns.data.model.unit.UnitAliasRepository;
import uk.gov.defra.datareturns.data.model.unit.UnitRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Simple database loader interface for master data
 *
 * @author Sam Gardner-Dell
 */
public interface DatabaseLoader {
    static <E extends MasterDataEntity> Function<Map<String, String>, E> basicFactory(final Supplier<E> entityClassSupplier) {
        return (rowData) -> {
            final E entity = entityClassSupplier.get();
            entity.setNomenclature(rowData.get("name"));
            return entity;
        };
    }

    /**
     * Load base data into the database
     */
    @Transactional
    void load();

    @Slf4j
    @RequiredArgsConstructor
    @Component
    class SiteAndPermitLoader implements DatabaseLoader {
        private final SiteRepository siteRepository;
        private final UniqueIdentifierRepository uniqueIdentifierRepository;
        private final UniqueIdentifierAliasRepository uniqueIdentifierAliasRepository;

        @Transactional
        @Override
        public void load() {
            final String[] permitFiles = {
                    "/db/data/stage_data_initialization/EA-WML.csv",
                    "/db/data/stage_data_initialization/EPR.csv",
                    "/db/data/stage_data_initialization/IPPC.csv",
                    "/db/data/stage_data_initialization/Pre-EA.csv"
            };
            final List<Map<String, String>> data = new ArrayList<>();
            Arrays.stream(permitFiles).forEach(pf -> data.addAll(LoaderUtils.readCsvData(pf)));

            final Map<String, Site> sites = new HashMap<>();
            final Map<String, UniqueIdentifier> primaryIdentifiers = new HashMap<>();

            for (final Map<String, String> rowData : data) {
                final String siteName = rowData.get("SITE");
                final Site site = sites.computeIfAbsent(siteName, (sn) -> {
                    final Site st = new Site();
                    st.setNomenclature(sn);
                    return st;
                });

                final String primaryPermit = rowData.get("EA_ID");
                final UniqueIdentifier primaryId = primaryIdentifiers.computeIfAbsent(primaryPermit, (ps) -> {
                    final UniqueIdentifier id = new UniqueIdentifier();
                    id.setNomenclature(ps);
                    id.setSite(site);
                    return id;
                });

                final Set<UniqueIdentifierAlias> historicalIdentifiers = new HashSet<>();
                final String alternative = rowData.get("ALTERNATIVES");
                if (StringUtils.isNotEmpty(alternative)) {
                    // Alternatives column only holds a single value
                    final UniqueIdentifierAlias alias = new UniqueIdentifierAlias();
                    alias.setNomenclature(alternative);
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
            final List<String> aliasNames = uniqueIdentifierAliasRepository.findAll().stream()
                    .map(UniqueIdentifierAlias::getNomenclature)
                    .collect(Collectors.toList());
            final List<String> primaryNames = uniqueIdentifierRepository.findAll().stream()
                    .map(UniqueIdentifier::getNomenclature)
                    .collect(Collectors.toList());
            final List<String> duplicates = ListUtils.intersection(primaryNames, aliasNames);
            if (!duplicates.isEmpty()) {
                log.error("*** Duplicates were found in both the primary and alias permit lists: " + duplicates.toString() + " ***");
            }
        }
    }

    @RequiredArgsConstructor
    @Component
    class MethodsOrStandardsLoader implements DatabaseLoader {
        private final MethodOrStandardRepository methodOrStandardRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/MethodsOrStandards.csv");
            for (final Map<String, String> rowData : data) {
                final MethodOrStandard entity = new MethodOrStandard();
                entity.setNomenclature(rowData.get("name"));
                entity.setNotes(rowData.get("notes"));
                methodOrStandardRepository.saveAndFlush(entity);
            }
        }
    }

    @RequiredArgsConstructor
    @Component
    class QualifiersLoader implements DatabaseLoader {
        private final QualifierRepository qualifierRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/Qualifiers.csv");
            for (final Map<String, String> rowData : data) {
                final Qualifier entity = new Qualifier();
                entity.setNomenclature(rowData.get("name"));
                entity.setNotes(rowData.get("notes"));
                entity.setSingleOrMultiple(rowData.get("singleormultiple"));
                entity.setType(rowData.get("type"));
                qualifierRepository.saveAndFlush(entity);
            }
        }
    }

    @RequiredArgsConstructor
    @Component
    class ReleasesAndTransfersLoader implements DatabaseLoader {
        private final ReleaseOrTransferRepository releasesAndTransfersRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/ReleasesAndTransfers.csv");
            for (final Map<String, String> rowData : data) {
                final ReleaseOrTransfer entity = new ReleaseOrTransfer();
                entity.setNomenclature(rowData.get("name"));
                releasesAndTransfersRepository.saveAndFlush(entity);
            }
        }
    }

    @RequiredArgsConstructor
    @Component
    class ReturnPeriodsLoader implements DatabaseLoader {
        private final ReturnPeriodRepository returnPeriodRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/ReturnPeriods.csv");
            for (final Map<String, String> rowData : data) {
                final ReturnPeriod entity = new ReturnPeriod();
                entity.setNomenclature(rowData.get("name"));
                entity.setDefinition(rowData.get("definition"));
                entity.setExample(rowData.get("example"));
                returnPeriodRepository.saveAndFlush(entity);
            }
        }
    }

    @RequiredArgsConstructor
    @Component
    class ReturnTypesLoader implements DatabaseLoader {
        private final ReturnTypeRepository returnTypeRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/ReturnTypes.csv");
            for (final Map<String, String> rowData : data) {
                final ReturnType entity = new ReturnType();
                entity.setNomenclature(rowData.get("name"));
                entity.setApplicability(rowData.get("applicability"));
                returnTypeRepository.saveAndFlush(entity);
            }
        }
    }

    @RequiredArgsConstructor
    @Component
    class TextValuesLoader implements DatabaseLoader {
        private final TextValueRepository textValueRepository;
        private final TextValueAliasRepository textValueAliasRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/TextValues.csv");
            LoaderUtils.persistSelfReferencingEntityFile(data,
                    textValueRepository::save, basicFactory(TextValue::new),
                    textValueAliasRepository::save, basicFactory(TextValueAlias::new));
        }
    }

    @RequiredArgsConstructor
    @Component
    class ReferencePeriodsLoader implements DatabaseLoader {
        private final ReferencePeriodRepository referencePeriodRepository;
        private final ReferencePeriodAliasRepository referencePeriodAliasRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/ReferencePeriods.csv");

            final Function<Map<String, String>, ReferencePeriod> referencePeriodFactory = (rowData) -> {
                final ReferencePeriod entity = new ReferencePeriod();
                entity.setNomenclature(rowData.get("name"));
                entity.setNotes(rowData.get("notes"));
                return entity;
            };
            LoaderUtils.persistSelfReferencingEntityFile(data,
                    referencePeriodRepository::save, referencePeriodFactory,
                    referencePeriodAliasRepository::save, basicFactory(ReferencePeriodAlias::new));
        }
    }

    @RequiredArgsConstructor
    @Component
    class UnitsLoader implements DatabaseLoader {
        private final UnitRepository unitRepository;
        private final UnitAliasRepository unitAliasRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/Units.csv");
            final Function<Map<String, String>, Unit> unitFactory = (rowData) -> {
                final Unit entity = new Unit();
                entity.setNomenclature(rowData.get("name"));
                entity.setDescription(rowData.get("description"));
                entity.setLongName(rowData.get("long_name"));
                entity.setType(rowData.get("type"));
                entity.setUnicode(rowData.get("unicode"));
                return entity;
            };
            LoaderUtils.persistSelfReferencingEntityFile(data, unitRepository::save, unitFactory,
                    unitAliasRepository::save, basicFactory(UnitAlias::new));

        }
    }

    //
    @RequiredArgsConstructor
    @Component
    class ParametersLoader implements DatabaseLoader {
        private final ParameterRepository parameterRepository;
        private final ParameterAliasRepository parameterAliasRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/Parameters_New.csv");
            final Function<Map<String, String>, Parameter> parameterFactory = (rowData) -> {
                final Parameter entity = new Parameter();
                entity.setNomenclature(rowData.get("name"));
                entity.setCas(rowData.get("cas"));
                entity.setType(rowData.get("type"));
                return entity;
            };
            LoaderUtils.persistSelfReferencingEntityFile(data,
                    parameterRepository::save, parameterFactory,
                    parameterAliasRepository::save, basicFactory(ParameterAlias::new));
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    class NaceLoader implements DatabaseLoader {
        private final NaceSectionRepository naceSectionRepository;
        private final NaceDivisionRepository naceDivisionRepository;
        private final NaceGroupRepository naceGroupRepository;
        private final NaceClassRepository naceClassRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/NACE_REV2_20171103_133916.csv");

            final Map<String, NaceSection> naceSections = new LinkedHashMap<>();
            final Map<String, NaceDivision> naceDivisions = new LinkedHashMap<>();
            final Map<String, NaceGroup> naceGroups = new LinkedHashMap<>();
            final List<NaceClass> naceClasses = new ArrayList<>();

            data.forEach(entry -> {
                final int level = Integer.parseInt(entry.get("Level"));
                final String code = entry.get("Code");
                final String parentCode = entry.get("Parent");
                final String description = entry.get("Description");
                final String details = entry.get("This item includes");

                switch (level) {
                    case 1:
                        final NaceSection section = new NaceSection();
                        section.setNomenclature(code);
                        section.setDescription(description);
                        section.setDetails(details);

                        naceSections.put(code, section);
                        break;
                    case 2:
                        final NaceDivision division = new NaceDivision();
                        division.setNomenclature(code);
                        division.setDescription(description);
                        division.setDetails(details);

                        division.setSection(naceSections.get(parentCode));
                        naceDivisions.put(code, division);

                        break;
                    case 3:
                        final NaceGroup group = new NaceGroup();
                        group.setNomenclature(code);
                        group.setDescription(description);
                        group.setDetails(details);

                        group.setDivision(naceDivisions.get(parentCode));
                        naceGroups.put(code, group);
                        break;
                    case 4:
                        final NaceClass naceClass = new NaceClass();
                        naceClass.setNomenclature(code);
                        naceClass.setDescription(description);
                        naceClass.setDetails(details);

                        naceClass.setGroup(naceGroups.get(parentCode));
                        naceClasses.add(naceClass);
                        break;
                    default:
                        log.error("Unknown NACE code level");
                        throw new RuntimeException("Unknown NACE code level");
                }

                naceSectionRepository.save(naceSections.values());
                naceSectionRepository.flush();
                naceDivisionRepository.save(naceDivisions.values());
                naceDivisionRepository.flush();
                naceGroupRepository.save(naceGroups.values());
                naceGroupRepository.flush();
                naceClassRepository.save(naceClasses);
                naceClassRepository.flush();
            });
        }
    }

}
