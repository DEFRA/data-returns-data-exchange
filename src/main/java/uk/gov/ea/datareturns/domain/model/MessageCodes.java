package uk.gov.ea.datareturns.domain.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.model.rules.FieldDefinition;

import java.util.*;

/**
 * The intersection between the error codes DR...and associated fields
 *
 * This class allows the message codes to be associated with a group of fields so that each
 * unique error message can be contextualized by other data on the row.
 *
 * This gets rid of the problem whereby the hibernate validator error cannot
 * carry a complex payload.
 *
 * The String constant is stored in the hibernate violation messageTemplate - the
 * DataSampleValidator reads this from the violation and determines the set of FieldDefinitions
 * for to retrieve from the record and to serialize on returned error message.
 */
public class MessageCodes {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MessageCodes.class);

    /**
     * The errors categorized as length errors
     */
    public class Length {
        public final static String CiC = "{DR9150-Length}";
        public final static String Comments = "{DR9140-Length}";
        public final static String Mon_Point = "{DR9060-Length}";
        public final static String Site_Name = "{DR9110-Length}";
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
        public final static String Rel_Trans = "{DR9170-Incorrect}";
        public final static String Site_Name = "{DR9110-Incorrect}";
    }

    /**
     * The errors categorized as conflict errors
     */
    public class Conflict {
        public final static String ProhibitUnitForTxtValue = "{DR9050-Conflict}";
        public final static String RequireValueOrTxtValue = "{DR9999-Conflict}";
        public final static String UniqueIdentifierSiteConflict = "{DR9000-Conflict}";
    }

    /**
     * The errors categorized as dependent validation errors - these are treated as conflict by the
     * front end but usefully distinguished in the API
     */
    public class DependencyConflict {
        public final static String Unit = "{DR9051-Conflict}";
        public final static String Rtn_Type = "{DR9011-Conflict}";
        public final static String Parameter = "{DR9031-Conflict}";
        public final static String Rel_Trans = "{DR9170-Conflict}";
    }

    private final static Map<String, List<FieldDefinition>> messageFieldsMap = new HashMap<>();

    /**
     * Helper to check that the map keys are unique
     * @param key
     * @param value
     */
    private static void add(String key, List<FieldDefinition> value) {
        if (messageFieldsMap.containsKey(key)) {
            LOGGER.error("Initialization error: " + key + " already exists in messageFieldsMap");
        } else {
            messageFieldsMap.put(key, value);
        }
    }
    
    static {
        /*
         * Add the (atomic) length errors to the map
         */
        add(Length.CiC, new ArrayList<>(Collections.singletonList(FieldDefinition.CiC)));
        add(Length.Comments, new ArrayList<>(Collections.singletonList(FieldDefinition.Comments)));
        add(Length.Mon_Point, new ArrayList<>(Collections.singletonList(FieldDefinition.Mon_Point)));
        add(Length.Site_Name, new ArrayList<>(Collections.singletonList(FieldDefinition.Site_Name)));
        /*
         * Add in the (atomic) Incorrect errors to the map
         */
        add(Incorrect.Mon_Point, new ArrayList<>(Collections.singletonList(FieldDefinition.Mon_Point)));
        add(Incorrect.Value, new ArrayList<>(Collections.singletonList(FieldDefinition.Value)));
        add(Incorrect.Mon_Date, Collections.singletonList(FieldDefinition.Mon_Date));
        /*
         * Add the (atomic) missing errors to map
         */
        add(Missing.Unit, new ArrayList<>(Collections.singletonList(FieldDefinition.Unit)));
        add(Missing.EA_ID, new ArrayList<>(Collections.singletonList(FieldDefinition.EA_ID)));
        add(Missing.Mon_Point, new ArrayList<>(Collections.singletonList(FieldDefinition.Mon_Point)));
        add(Missing.Parameter, new ArrayList<>(Collections.singletonList(FieldDefinition.Parameter)));
        add(Missing.Rtn_Type, new ArrayList<>(Collections.singletonList(FieldDefinition.Rtn_Type)));
        add(Missing.Site_Name, Collections.singletonList(FieldDefinition.Site_Name));
        add(Missing.Mon_Date, Collections.singletonList(FieldDefinition.Mon_Date));
        add(Missing.Rel_Trans, Collections.singletonList(FieldDefinition.Rel_Trans));
        /*
         * Add the (atomic) controlled list errors to the map
         */
        add(ControlledList.MethodOrStandard, Collections.singletonList(FieldDefinition.Meth_Stand));
        add(ControlledList.EA_ID, Collections.singletonList(FieldDefinition.EA_ID));
        add(ControlledList.Parameter, Collections.singletonList(FieldDefinition.Parameter));
        add(ControlledList.Qualifier, Collections.singletonList(FieldDefinition.Qualifier));
        add(ControlledList.Ref_Period, Collections.singletonList(FieldDefinition.Ref_Period));
        add(ControlledList.Rtn_Period, Collections.singletonList(FieldDefinition.Rtn_Period));
        add(ControlledList.Rtn_Type, Collections.singletonList(FieldDefinition.Rtn_Type));
        add(ControlledList.Txt_Value, Collections.singletonList(FieldDefinition.Txt_Value));
        add(ControlledList.Unit, Collections.singletonList(FieldDefinition.Unit));
        add(ControlledList.Site_Name, new ArrayList<>(Collections.singletonList(FieldDefinition.Site_Name)));
        /*
         * Add in the conflicts, optionally missing and dependency validations etc. The convention being applied is to
         * add first the primary data item - the header the error is being reported on and then
         * items in descending order of relevance.
         */
        add(Conflict.ProhibitUnitForTxtValue, Collections.singletonList(FieldDefinition.Unit));
        add(Missing.RequireCommentsForTxtValue, Collections.singletonList(FieldDefinition.Comments));
        add(Conflict.RequireValueOrTxtValue, Arrays.asList(FieldDefinition.Value, FieldDefinition.Txt_Value));
        add(Missing.RequireValueOrTxtValue, Arrays.asList(FieldDefinition.Value, FieldDefinition.Txt_Value));
        add(Conflict.UniqueIdentifierSiteConflict, Arrays.asList(FieldDefinition.EA_ID, FieldDefinition.Site_Name));
        add(DependencyConflict.Rel_Trans, Collections.singletonList(FieldDefinition.Rel_Trans));
        add(DependencyConflict.Parameter, new ArrayList<>(Arrays.asList(FieldDefinition.Parameter, FieldDefinition.Rtn_Type)));
        add(DependencyConflict.Unit, new ArrayList<>(Arrays.asList(FieldDefinition.Unit, FieldDefinition.Parameter, FieldDefinition.Rtn_Type)));
    }

    public static List<FieldDefinition> getFieldDependencies(String message) {
        return messageFieldsMap.get(message);
    }
}
