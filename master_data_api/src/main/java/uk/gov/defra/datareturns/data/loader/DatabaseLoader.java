package uk.gov.defra.datareturns.data.loader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.defra.datareturns.data.model.Context;
import uk.gov.defra.datareturns.data.model.disposalsandrecoveries.DisposalCode;
import uk.gov.defra.datareturns.data.model.disposalsandrecoveries.DisposalCodeRepository;
import uk.gov.defra.datareturns.data.model.disposalsandrecoveries.RecoveryCode;
import uk.gov.defra.datareturns.data.model.disposalsandrecoveries.RecoveryCodeRepository;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifier;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifierAlias;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifierAliasRepository;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifierRepository;
import uk.gov.defra.datareturns.data.model.eprtr.EprtrActivity;
import uk.gov.defra.datareturns.data.model.eprtr.EprtrActivityRepository;
import uk.gov.defra.datareturns.data.model.eprtr.EprtrSector;
import uk.gov.defra.datareturns.data.model.eprtr.EprtrSectorRepository;
import uk.gov.defra.datareturns.data.model.ewc.EwcActivity;
import uk.gov.defra.datareturns.data.model.ewc.EwcActivityRepository;
import uk.gov.defra.datareturns.data.model.ewc.EwcChapter;
import uk.gov.defra.datareturns.data.model.ewc.EwcChapterRepository;
import uk.gov.defra.datareturns.data.model.ewc.EwcSubchapter;
import uk.gov.defra.datareturns.data.model.ewc.EwcSubchapterRepository;
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
import uk.gov.defra.datareturns.data.model.nosep.NoseActivity;
import uk.gov.defra.datareturns.data.model.nosep.NoseActivityClass;
import uk.gov.defra.datareturns.data.model.nosep.NoseActivityClassRepository;
import uk.gov.defra.datareturns.data.model.nosep.NoseActivityRepository;
import uk.gov.defra.datareturns.data.model.nosep.NoseProcess;
import uk.gov.defra.datareturns.data.model.nosep.NoseProcessRepository;
import uk.gov.defra.datareturns.data.model.parameter.Parameter;
import uk.gov.defra.datareturns.data.model.parameter.ParameterAlias;
import uk.gov.defra.datareturns.data.model.parameter.ParameterAliasRepository;
import uk.gov.defra.datareturns.data.model.parameter.ParameterGroup;
import uk.gov.defra.datareturns.data.model.parameter.ParameterGroupRepository;
import uk.gov.defra.datareturns.data.model.parameter.ParameterRepository;
import uk.gov.defra.datareturns.data.model.parameter.ParameterType;
import uk.gov.defra.datareturns.data.model.parameter.ParameterTypeRepository;
import uk.gov.defra.datareturns.data.model.qualifier.Qualifier;
import uk.gov.defra.datareturns.data.model.qualifier.QualifierRepository;
import uk.gov.defra.datareturns.data.model.referenceperiod.ReferencePeriod;
import uk.gov.defra.datareturns.data.model.referenceperiod.ReferencePeriodAlias;
import uk.gov.defra.datareturns.data.model.referenceperiod.ReferencePeriodAliasRepository;
import uk.gov.defra.datareturns.data.model.referenceperiod.ReferencePeriodRepository;
import uk.gov.defra.datareturns.data.model.regime.Regime;
import uk.gov.defra.datareturns.data.model.regime.RegimeRepository;
import uk.gov.defra.datareturns.data.model.returnperiod.ReturnPeriod;
import uk.gov.defra.datareturns.data.model.returnperiod.ReturnPeriodRepository;
import uk.gov.defra.datareturns.data.model.returntype.ReturnType;
import uk.gov.defra.datareturns.data.model.returntype.ReturnTypeRepository;
import uk.gov.defra.datareturns.data.model.route.Route;
import uk.gov.defra.datareturns.data.model.route.RouteRepository;
import uk.gov.defra.datareturns.data.model.route.Subroute;
import uk.gov.defra.datareturns.data.model.route.SubrouteRepository;
import uk.gov.defra.datareturns.data.model.site.Site;
import uk.gov.defra.datareturns.data.model.site.SiteRepository;
import uk.gov.defra.datareturns.data.model.regimeobligation.RegimeObligation;
import uk.gov.defra.datareturns.data.model.regimeobligation.RegimeObligationRepository;
import uk.gov.defra.datareturns.data.model.textvalue.TextValue;
import uk.gov.defra.datareturns.data.model.textvalue.TextValueAlias;
import uk.gov.defra.datareturns.data.model.textvalue.TextValueAliasRepository;
import uk.gov.defra.datareturns.data.model.textvalue.TextValueRepository;
import uk.gov.defra.datareturns.data.model.threshold.Threshold;
import uk.gov.defra.datareturns.data.model.threshold.ThresholdRepository;
import uk.gov.defra.datareturns.data.model.unit.Unit;
import uk.gov.defra.datareturns.data.model.unit.UnitAlias;
import uk.gov.defra.datareturns.data.model.unit.UnitAliasRepository;
import uk.gov.defra.datareturns.data.model.unit.UnitRepository;
import uk.gov.defra.datareturns.data.model.unit.UnitType;
import uk.gov.defra.datareturns.data.model.unit.UnitTypeRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static uk.gov.defra.datareturns.data.loader.LoaderUtils.basicFactory;
import static uk.gov.defra.datareturns.data.model.Context.ECM;

/**
 * Simple database loader interface for master data
 *
 * @author Sam Gardner-Dell
 */
public interface DatabaseLoader {


    /**
     * Load base data into the database
     */
    @Transactional
    void load();

    /**
     * Other DatabaseLoader instances which must be run first.
     *
     * @return
     */
    default Set<Class<? extends DatabaseLoader>> dependsOn() {
        return Collections.emptySet();
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    class RegimeLoader implements DatabaseLoader {
        private final RegimeRepository regimeRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/Regimes.csv");
            for (final Map<String, String> rowData : data) {
                final Regime entity = new Regime();
                entity.setNomenclature(rowData.get("name"));
                regimeRepository.save(entity);
            }
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    class SiteAndPermitLoader implements DatabaseLoader {
        private final SiteRepository siteRepository;
        private final UniqueIdentifierRepository uniqueIdentifierRepository;
        private final UniqueIdentifierAliasRepository uniqueIdentifierAliasRepository;
        private final RegimeRepository regimeRepository;

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

            Regime ecm = regimeRepository.getOne(1L);

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
                    id.getRegime().put(ECM, ecm);
                    return id;
                });

                final String alternative = rowData.get("ALTERNATIVES");
                if (StringUtils.isNotEmpty(alternative)) {
                    // Alternatives column only holds a single value
                    final UniqueIdentifierAlias alias = new UniqueIdentifierAlias();
                    alias.setNomenclature(alternative);
                    alias.setPreferred(primaryId);
                    primaryId.getAliases().add(alias);
                }
            }

            // Load all sites
            siteRepository.save(sites.values());
            uniqueIdentifierRepository.save(primaryIdentifiers.values());

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

        @Override
        public Set<Class<? extends DatabaseLoader>> dependsOn() {
            return new HashSet<>(Collections.singletonList(RegimeLoader.class));
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
    @Slf4j
    class ReturnTypesLoader implements DatabaseLoader {
        private final ReturnTypeRepository returnTypeRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/ReturnTypes.csv");
            data.stream().map(basicFactory(ReturnType::new)).forEach(returnTypeRepository::save);
        }
    }

    @RequiredArgsConstructor
    @Component
    @Slf4j
    class RoutesLoader implements DatabaseLoader {
        private final RouteRepository routeRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/Routes.csv");
            data.stream().map(basicFactory(Route::new)).forEach(routeRepository::save);
        }
    }


    @RequiredArgsConstructor
    @Component
    @Slf4j
    class SubroutesLoader implements DatabaseLoader {
        private final RouteRepository routeRepository;
        private final SubrouteRepository subrouteRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/Subroutes.csv");
            data.stream()
                    .map(rowData -> {
                        final Subroute subroute = new Subroute();
                        subroute.setNomenclature(rowData.get("name"));
                        subroute.setRoute(routeRepository.getByNomenclature(rowData.get("route_name")));
                        return subroute;
                    })
                    .forEach(subrouteRepository::save);
        }


        @Override
        public Set<Class<? extends DatabaseLoader>> dependsOn() {
            return new HashSet<>(Collections.singletonList(RoutesLoader.class));
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
    @Slf4j
    @Component
    class UnitsLoader implements DatabaseLoader {
        private final UnitRepository unitRepository;
        private final UnitAliasRepository unitAliasRepository;
        private final UnitTypeRepository unitTypeRepository;
        private final RegimeObligationRepository regimeObligationRepository;

        // regimeObligationRepository

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/Units.csv");

            // Map to store unit types as they are read from the file.
            final Map<String, UnitType> unitTypes = new HashMap<>();

            Map<String, RegimeObligation> regimeObligationMap = regimeObligationRepository.findAll()
                    .stream().collect(Collectors.toMap(RegimeObligation::getNomenclature, Function.identity()));

            // Unit factory
            final Function<Map<String, String>, Unit> unitFactory = (rowData) -> {
                final Unit entity = new Unit();
                entity.setNomenclature(rowData.get("name"));
                entity.setDescription(rowData.get("description"));
                entity.setLongName(rowData.get("long_name"));
                entity.setUnicode(rowData.get("unicode"));

                final String conversionFactorString = Objects.toString(rowData.get("conversion"), "0");
                final BigDecimal conversionFactor = new BigDecimal(conversionFactorString);
                if (conversionFactor.equals(BigDecimal.ZERO)) {
                    log.warn("Conversion factor for unit {} is not set, or is set to zero.  " +
                            "Data submitted using this unit will be excluded from reporting.", entity.getNomenclature());
                }
                entity.setConversion(conversionFactor);

                final String typeName = rowData.get("type");
                UnitType type = unitTypes.get(typeName);
                if (type == null) {
                    // Haven't encountered this type before
                    type = new UnitType();
                    type.setNomenclature(typeName);
                    unitTypes.put(typeName, type);
                    unitTypeRepository.save(type);
                }
                // Associate the unit and the type
                entity.setType(type);
                type.getUnits().add(entity);

                final Set<String> groupNames = LoaderUtils.extractGroupSet(rowData.get("unit_groups"));


                for (final String groupName : groupNames) {
                    switch (groupName) {
                        case "ECM":
                            regimeObligationMap.get("ECM").getUnits().add(entity);
                            break;
                        case "PI_RSR":
                            regimeObligationMap.get("PI_RSR_RTA").getUnits().add(entity);
                            regimeObligationMap.get("PI_RSR_OSTW").getUnits().add(entity);
                            regimeObligationMap.get("PI_RSR_RTCW").getUnits().add(entity);
                            break;
                        case "PI":
                            regimeObligationMap.get("PI_A1_RTA").getUnits().add(entity);
                            regimeObligationMap.get("PI_A1_RTL").getUnits().add(entity);
                            regimeObligationMap.get("PI_A1_RTCW").getUnits().add(entity);
                            regimeObligationMap.get("PI_A1_OSTW").getUnits().add(entity);
                            regimeObligationMap.get("PI_A1LFIA_RTA").getUnits().add(entity);
                            regimeObligationMap.get("PI_A1LFIA_RTL").getUnits().add(entity);
                            regimeObligationMap.get("PI_A1LFIA_RTCW").getUnits().add(entity);
                            regimeObligationMap.get("PI_A1LFIA_OSTW").getUnits().add(entity);
                            regimeObligationMap.get("PI_EPRTR_RTA").getUnits().add(entity);
                            regimeObligationMap.get("PI_EPRTR_RTL").getUnits().add(entity);
                            regimeObligationMap.get("PI_EPRTR_RTCW").getUnits().add(entity);
                            regimeObligationMap.get("PI_EPRTR_OSTW").getUnits().add(entity);
                            break;
                    }
                }
                return entity;
            };

            LoaderUtils.persistSelfReferencingEntityFile(data, unitRepository::save, unitFactory,
                    unitAliasRepository::save, basicFactory(UnitAlias::new));

            regimeObligationRepository.save(regimeObligationMap.values());
        }

        @Override
        public Set<Class<? extends DatabaseLoader>> dependsOn() {
            return new HashSet<>(Collections.singletonList(RegimeObligationsLoader.class));
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    class ParametersLoader implements DatabaseLoader {
        private final ParameterRepository parameterRepository;
        private final ParameterTypeRepository parameterTypeRepository;
        private final ParameterAliasRepository parameterAliasRepository;
        private final ParameterGroupRepository parameterGroupRepository;

        @Transactional
        @Override
        public void load() {
            final Map<String, ParameterGroup> parameterGroups = new HashMap<>();

            // Map to store parameter types as they are read from the file.
            final Map<String, ParameterType> parameterTypes = new HashMap<>();

            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/Parameters.csv");
            final Function<Map<String, String>, Parameter> parameterFactory = (rowData) -> {
                final Parameter entity = new Parameter();
                entity.setNomenclature(rowData.get("name"));
                entity.setCas(rowData.get("cas"));

                final String typeName = rowData.get("type");
                ParameterType type = parameterTypes.get(typeName);
                if (type == null) {
                    // Haven't encountered this type before
                    type = new ParameterType();
                    type.setNomenclature(typeName);
                    parameterTypes.put(typeName, type);
                    parameterTypeRepository.save(type);
                }
                // Associate the unit and the type
                entity.setType(type);
                type.getParameters().add(entity);

                final Set<String> groupNames = LoaderUtils.extractGroupSet(rowData.get("parameter_groups"));
                for (final String groupName : groupNames) {
                    ParameterGroup group = parameterGroups.get(groupName);
                    if (group == null) {
                        group = new ParameterGroup();
                        group.setNomenclature(groupName);
                        parameterGroups.put(groupName, group);
                    }
                    group.getParameters().add(entity);
                }
                return entity;
            };
            LoaderUtils.persistSelfReferencingEntityFile(data,
                    parameterRepository::save, parameterFactory,
                    parameterAliasRepository::save, basicFactory(ParameterAlias::new));

            // Now that all parameters have been persisted, also flush changes to the groups
            parameterGroupRepository.save(parameterGroups.values().stream()
                    .sorted(Comparator.comparing(ParameterGroup::getNomenclature))
                    .collect(Collectors.toList()));
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    class ThresholdLoader implements DatabaseLoader {
        private final RegimeObligationRepository regimeObligationRepository;
        private final ThresholdRepository thresholdRepository;
        private final ParameterRepository parameterRepository;
        private final UnitRepository unitRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/Thresholds.csv");
            for (final Map<String, String> rowData : data) {
                final Threshold entity = new Threshold();
                entity.setType(Threshold.ThresholdType.valueOf(rowData.get("type")));
                entity.setRegimeObligation(regimeObligationRepository.getByNomenclature(rowData.get("regime_obligation_name")));
                entity.setParameter(parameterRepository.getByNomenclature(rowData.get("parameter_name")));
                entity.setUnit(unitRepository.getByNomenclature(rowData.get("unit")));
                entity.setValue(new BigDecimal(rowData.get("threshold")));
                thresholdRepository.save(entity);
            }
        }

        @Override
        public Set<Class<? extends DatabaseLoader>> dependsOn() {
            return new HashSet<>(Arrays.asList(ParametersLoader.class, UnitsLoader.class, RegimeObligationsLoader.class));
        }
    }


    @Slf4j
    @RequiredArgsConstructor
    @Component
    class RegimeObligationsLoader implements DatabaseLoader {
        private final RegimeObligationRepository regimeObligationRepository;
        private final RegimeRepository regimeRepository;
        private final RouteRepository routeRepository;
        private final ParameterGroupRepository parameterGroupRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/RegimeObligations.csv");
            for (final Map<String, String> rowData : data) {
                final RegimeObligation entity = new RegimeObligation();
                entity.setNomenclature(rowData.get("name"));
                entity.setDescription(rowData.get("description"));
                entity.setRegime(regimeRepository.getByNomenclature(rowData.get("regime_name")));
                entity.setRoute(routeRepository.getByNomenclature(rowData.get("route_name")));

                final Set<String> parameterGroups = LoaderUtils.extractGroupSet(rowData.get("parameter_groups"));
                parameterGroups.stream().map(parameterGroupRepository::getByNomenclature).forEach(entity.getParameterGroups()::add);
                regimeObligationRepository.save(entity);
            }
        }

        @Override
        public Set<Class<? extends DatabaseLoader>> dependsOn() {
            return new HashSet<>(Arrays.asList(RegimeLoader.class, RoutesLoader.class, ParametersLoader.class));
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    class NoseLoader implements DatabaseLoader {
        private final NoseActivityClassRepository noseActivityClassRepository;
        private final NoseActivityRepository noseActivityRepository;
        private final NoseProcessRepository noseProcessRepository;

        @Transactional
        @Override
        public void load() {
            List<String[]> rows = LoaderUtils.readTabData("/db/data/nose_p_activity.tsv");

            final Set<NoseActivityClass> noseActivityClasses = new HashSet<>();
            final Set<NoseActivity> noseActivities = new HashSet<>();
            final Map<String, NoseProcess> noseProcesses = new HashMap<>();

            NoseActivityClass noseActivityClass = null;
            NoseActivity noseActivity = null;
            NoseProcess noseProcess;

            for (String[] entry : rows) {
                if (entry.length == 1) {
                    noseActivityClass = new NoseActivityClass();
                    noseActivityClass.setNomenclature(entry[0]);
                    noseActivityClasses.add(noseActivityClass);
                }

                if (entry.length == 2) {
                    noseActivity = new NoseActivity();
                    noseActivity.setNomenclature(entry[1]);
                    noseActivity.setNoseActivityClass(noseActivityClass);
                    noseActivities.add(noseActivity);
                }

                if (entry.length == 4) {
                    if (noseProcesses.containsKey(entry[2])) {
                        noseProcess = noseProcesses.get(entry[2]);
                        noseProcess.getNoseActivities().add(noseActivity);
                    } else {
                        noseProcess = new NoseProcess();
                        noseProcess.setNomenclature(entry[2]);
                        noseProcess.setDescription(entry[3]);
                        noseProcess.setNoseActivities(new HashSet<>(Arrays.asList(noseActivity)));
                        noseProcesses.put(entry[2], noseProcess);
                    }
                }
            }

            noseActivityClassRepository.save(noseActivityClasses);
            noseActivityRepository.save(noseActivities);
            noseProcessRepository.save(noseProcesses.values());
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    class EprtrLoader implements DatabaseLoader {
        private final EprtrActivityRepository eprtrActivityRepository;
        private final EprtrSectorRepository eprtrSectorRepository;

        @Override
        public void load() {
            final List<String[]> rows = LoaderUtils.readTabData("/db/data/EPRTR.tsv");

            final Set<EprtrActivity> eprtrActivities = new HashSet<>();
            final Set<EprtrSector> eprtrSectors = new HashSet<>();

            EprtrSector eprtrSector = null;

            for (final String[] entry : rows) {
                if (entry.length == 2) {
                    eprtrSector = new EprtrSector();
                    eprtrSector.setNomenclature(entry[0]);
                    eprtrSector.setDescription(entry[1]);
                    eprtrSectors.add(eprtrSector);
                } else {
                    final EprtrActivity eprtrActivity = new EprtrActivity();
                    eprtrActivity.setNomenclature(entry[1]);
                    eprtrActivity.setDescription(entry[2]);
                    if (entry.length == 4) {
                        eprtrActivity.setThreshold(entry[3]);
                    }
                    eprtrActivity.setEprtrSector(eprtrSector);
                    eprtrActivities.add(eprtrActivity);
                }
            }
            eprtrSectorRepository.save(eprtrSectors);
            eprtrActivityRepository.save(eprtrActivities);

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

            // Load Nace codes using the order encountered in the published NACE data
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
                        division.setNaceSection(naceSections.get(parentCode));
                        naceDivisions.put(code, division);

                        break;
                    case 3:
                        final NaceGroup group = new NaceGroup();
                        group.setNomenclature(code);
                        group.setDescription(description);
                        group.setDetails(details);
                        group.setNaceDivision(naceDivisions.get(parentCode));
                        naceGroups.put(code, group);
                        break;
                    case 4:
                        final NaceClass naceClass = new NaceClass();
                        naceClass.setNomenclature(code);
                        naceClass.setDescription(description);
                        naceClass.setDetails(details);
                        naceClass.setNaceGroup(naceGroups.get(parentCode));
                        naceClasses.add(naceClass);
                        break;
                    default:
                        log.error("Unknown NACE code level");
                        throw new RuntimeException("Unknown NACE code level");
                }

                naceSectionRepository.save(naceSections.values());
                naceDivisionRepository.save(naceDivisions.values());
                naceGroupRepository.save(naceGroups.values());
                naceClassRepository.save(naceClasses);
            });
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    class DisposalsAndRecoveryLoader implements DatabaseLoader {
        //private final MethodOrStandardRepository methodOrStandardRepository;
        private final DisposalCodeRepository disposalCodeRepository;
        private final RecoveryCodeRepository recoveryCodeRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> cdata = LoaderUtils.readCsvData("/db/data/DisposalCodes.csv");
            for (final Map<String, String> rowData : cdata) {
                final DisposalCode entity = new DisposalCode();
                entity.setNomenclature(rowData.get("code"));
                entity.setDescription(rowData.get("description"));
                disposalCodeRepository.saveAndFlush(entity);
            }

            final List<Map<String, String>> rdata = LoaderUtils.readCsvData("/db/data/RecoveryCodes.csv");
            for (final Map<String, String> rowData : rdata) {
                final RecoveryCode entity = new RecoveryCode();
                entity.setNomenclature(rowData.get("code"));
                entity.setDescription(rowData.get("description"));
                recoveryCodeRepository.saveAndFlush(entity);
            }
        }
    }

    @Slf4j
    @RequiredArgsConstructor
    @Component
    class EwcLoader implements DatabaseLoader {
        private static final Pattern EWC_NOMEN_PATTERN = Pattern.compile("\\s*(?<Chapter>\\d{2})(\\s+(?<Subchapter>\\d{2}))?" +
                "(\\s+(?<Activity>\\d{2}))?(\\s*(?<Hazardous>\\*))?\\s*");
        private final EwcChapterRepository ewcChapterRepository;
        private final EwcSubchapterRepository ewcSubchapterRepository;
        private final EwcActivityRepository ewcActivityRepository;

        @Transactional
        @Override
        public void load() {
            final List<Map<String, String>> data = LoaderUtils.readCsvData("/db/data/EWC.csv");

            final Map<String, EwcChapter> ewcChapterMap = new HashMap<>();
            final Map<String, EwcSubchapter> ewcSubchapterMap = new HashMap<>();
            data.forEach(entry -> {
                final String ewcCode = entry.get("ewc_code").trim();
                final String description = entry.get("description").trim();

                // Determine EWC entity type from description
                final Matcher matcher = EWC_NOMEN_PATTERN.matcher(ewcCode);
                if (matcher.matches()) {
                    final String chapterCode = matcher.group("Chapter");
                    final String subchapterCode = matcher.group("Subchapter");
                    final String activityCode = matcher.group("Activity");
                    final boolean hazardous = matcher.group("Hazardous") != null;

                    if (activityCode != null) {
                        final EwcActivity activity = new EwcActivity();
                        activity.setNomenclature(chapterCode + " " + subchapterCode + " " + activityCode);
                        activity.setCode(activityCode);
                        activity.setDescription(description);
                        activity.setHazardous(hazardous);
                        activity.setEwcSubchapter(ewcSubchapterMap.get(subchapterCode));
                        ewcActivityRepository.save(activity);
                    } else if (subchapterCode != null) {
                        final EwcSubchapter subchapter = new EwcSubchapter();
                        subchapter.setNomenclature(chapterCode + " " + subchapterCode);
                        subchapter.setCode(subchapterCode);
                        subchapter.setDescription(description);
                        subchapter.setEwcChapter(ewcChapterMap.get(chapterCode));
                        ewcSubchapterRepository.save(subchapter);
                        ewcSubchapterMap.put(subchapterCode, subchapter);
                    } else {
                        final EwcChapter chapter = new EwcChapter();
                        chapter.setNomenclature(chapterCode);
                        chapter.setCode(chapterCode);
                        chapter.setDescription(description);
                        ewcChapterRepository.save(chapter);
                        ewcChapterMap.put(chapterCode, chapter);
                    }
                } else {
                    log.error("Unable to parse EWC nomenclature: '{}'", ewcCode);
                }
            });
        }
    }
}
