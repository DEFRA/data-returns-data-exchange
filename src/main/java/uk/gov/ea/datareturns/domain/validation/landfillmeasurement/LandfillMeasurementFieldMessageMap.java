package uk.gov.ea.datareturns.domain.validation.landfillmeasurement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.validation.FieldMessageMap;
import uk.gov.ea.datareturns.domain.validation.landfillmeasurement.fields.*;
import uk.gov.ea.datareturns.domain.validation.model.fields.FieldValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

/**
 * The field mapping for landfill measurements
 */
public class LandfillMeasurementFieldMessageMap implements FieldMessageMap<LandfillMeasurementMvo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandfillMeasurementFieldMessageMap.class);
    private static Class<LandfillMeasurementMvo> measurementMvoClass = LandfillMeasurementMvo.class;
    private static Map<Type, Method> typeMethodMap = new HashMap<>();

    // Map of methods by there return types - they are fields and should be unique
    static {
        for (Method method : measurementMvoClass.getDeclaredMethods()) {
            Type returnType = method.getGenericReturnType();
            typeMethodMap.put(returnType, method);
        }
    }

    /**
     * The errors categorized as length errors
     */
    public class Length {
        public final static String Comments = "{DR9140-Length}";
        public final static String Mon_Point = "{DR9060-Length}";
    }

    /**
     * The errors categorized as Incorrect errors
     */
    public class Incorrect {
        public final static String Mon_Point = "{DR9060-Incorrect}";
        public final static String Value = "{DR9040-Incorrect}";
        public final static String Mon_Date = "{DR9020-Incorrect}";
    }

    /**
     * The errors categorized as missing errors
     */
    public class Missing {
        public final static String Unit = "{DR9050-Missing}";
        public final static String EA_ID = "{DR9000-Missing}";
        public final static String Mon_Point = "{DR9060-Missing}";
        public final static String Parameter = "{DR9030-Missing}";
        public final static String Rtn_Type = "{DR9010-Missing}";
        public final static String Site_Name = "{DR9110-Missing}";
        public final static String RequireCommentsForTxtValue = "{DR9140-Missing}";
        public final static String RequireValueOrTxtValue = "{DR9999-Missing}";
        public final static String Mon_Date = "{DR9020-Missing}";
        public final static String Rel_Trans = "{DR9170-Missing}";
    }

    /**
     * The errors categorized as missing errors
     */
    public class ControlledList {
        public final static String MethodOrStandard = "{DR9100-Incorrect}";
        public final static String EA_ID = "{DR9000-Incorrect}";
        public final static String Parameter = "{DR9030-Incorrect}";
        public final static String Qualifier = "{DR9180-Incorrect}";
        public final static String Ref_Period = "{DR9090-Incorrect}";
        public final static String Rtn_Period = "{DR9070-Incorrect}";
        public final static String Rtn_Type = "{DR9010-Incorrect}";
        public final static String Txt_Value = "{DR9080-Incorrect}";
        public final static String Unit = "{DR9050-Incorrect}";
    }

    /**
     * The errors categorized as conflict errors
     */
    public class Conflict {
        public final static String ProhibitUnitForTxtValue = "{DR9050-Conflict}";
        public final static String RequireValueOrTxtValue = "{DR9999-Conflict}";
        public final static String UniqueIdentifierSiteConflict = "{DR9110-Conflict}";
    }

    private final static Map<String, List<Class<? extends FieldValue>>> messageFieldsMap = new HashMap<>();

    private static void add(String key, Class<? extends FieldValue<LandfillMeasurementMvo, ?>> ... values) {
        if (messageFieldsMap.containsKey(key)) {
            LOGGER.error("Initialization error: " + key + " already exists in messageFieldsMap");
        } else {
            messageFieldsMap.put(key, Arrays.asList(values));
        }
    }

    static {
        /*
         * Add the (atomic) length errors to the map
         */
        add(Length.Comments, Comments.class);
        add(Length.Mon_Point, MonitoringPoint.class);
        /*
         * Add in the (atomic) Incorrect errors to the map
         */
        add(Incorrect.Mon_Point, MonitoringPoint.class);
        add(Incorrect.Value, Value.class);
        add(Incorrect.Mon_Date, MonitoringDate.class);
        /*
         * Add the (atomic) missing errors to map
         */
        add(Missing.Unit, Unit.class);
        add(Missing.EA_ID, EaId.class);
        add(Missing.Mon_Point, MonitoringPoint.class);
        add(Missing.Parameter, Parameter.class);
        add(Missing.Rtn_Type, ReturnType.class);
        add(Missing.Site_Name, SiteName.class);
        add(Missing.Mon_Date, MonitoringDate.class);

        /*
         * Add the (atomic) controlled list errors to the map
         */
        add(ControlledList.MethodOrStandard, MethodOrStandard.class);
        add(ControlledList.EA_ID, EaId.class);
        add(ControlledList.Parameter, Parameter.class);
        add(ControlledList.Qualifier, Qualifier.class);
        add(ControlledList.Ref_Period, ReferencePeriod.class);
        add(ControlledList.Rtn_Period, ReturnPeriod.class);
        add(ControlledList.Rtn_Type, ReturnType.class);
        add(ControlledList.Txt_Value, TxtValue.class);
        add(ControlledList.Unit, Unit.class);
        /*
         * Add in the conflicts, optionally missing and dependency validations etc. The convention being applied is to
         * add first the primary data item - the header the error is being reported on and then
         * items in descending order of relevance.
         */
        add(Conflict.ProhibitUnitForTxtValue, Unit.class);
        add(Missing.RequireCommentsForTxtValue, Comments.class);
        add(Conflict.RequireValueOrTxtValue, Value.class, TxtValue.class);
        add(Conflict.UniqueIdentifierSiteConflict, EaId.class, SiteName.class);
    }

    @Override
    public List<FieldValue<LandfillMeasurementMvo, ?>> getFieldDependencies(LandfillMeasurementMvo measurement, String message) {
        List<FieldValue<LandfillMeasurementMvo, ?>> result = new ArrayList<>();
        List<Class<? extends FieldValue>> fieldClasses = messageFieldsMap.get(message);
        for (Class<? extends FieldValue> fieldClass : fieldClasses) {
            Method getter = typeMethodMap.get(fieldClass);
            try {
                FieldValue<LandfillMeasurementMvo, ?> field = (FieldValue<LandfillMeasurementMvo, ?>) getter.invoke(measurement);
                result.add(field);
            } catch (IllegalAccessException|InvocationTargetException e) {
                LOGGER.error("Could not invoke a getter: " + fieldClass.getCanonicalName());
            }
        }
        return result;
    }
}
