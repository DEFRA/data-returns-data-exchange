package uk.gov.ea.datareturns.domain.model;

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
        public final static String EA_ID = "{DR9000-Incorrect}";
        public final static String Mon_Point = "{DR9060-Incorrect}";
        public final static String Site_Name = "{DR9110-Incorrect}";
        public final static String Value = "{DR9040-Incorrect}";
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
        public final static String RequireUnitForValue = "{DR9050-Missing}";
        public final static String RequireValueOrTxtValue = "{DR9999-Missing}";
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
    }

    /**
     * The errors categorized as dependent validation errors - these are treated as conflict by the
     * front end but usefully distinguished in the API
     */
    public class DependencyConflict {
        public final static String Unit = "{DR9051-Conflict}";
        public final static String Rtn_Type = "{DR9051-Conflict}";
        public final static String Parameter = "{DR9031-Conflict}";
    }

    private final static Map<String, List<FieldDefinition>> messageFieldsMap = new HashMap<>();

    static {
        /*
         * Add the (atomic) length errors to the map
         */
        messageFieldsMap.put(Length.CiC, new ArrayList<>(Collections.singletonList(FieldDefinition.CiC)));
        messageFieldsMap.put(Length.Comments, new ArrayList<>(Collections.singletonList(FieldDefinition.Comments)));
        messageFieldsMap.put(Length.Mon_Point, new ArrayList<>(Collections.singletonList(FieldDefinition.Mon_Point)));
        messageFieldsMap.put(Length.Site_Name, new ArrayList<>(Collections.singletonList(FieldDefinition.Site_Name)));
        /*
         * Add in the (atomic) Incorrect errors to the map
         */
        messageFieldsMap.put(Incorrect.EA_ID, new ArrayList<>(Collections.singletonList(FieldDefinition.EA_ID)));
        messageFieldsMap.put(Incorrect.Mon_Point, new ArrayList<>(Collections.singletonList(FieldDefinition.Mon_Point)));
        messageFieldsMap.put(Incorrect.Site_Name, new ArrayList<>(Collections.singletonList(FieldDefinition.Site_Name)));
        messageFieldsMap.put(Incorrect.Value, new ArrayList<>(Collections.singletonList(FieldDefinition.Value)));
        /*
         * Add the (atomic) missing errors to map
         */
        messageFieldsMap.put(Missing.Unit, new ArrayList<>(Collections.emptyList()));
        messageFieldsMap.put(Missing.EA_ID, new ArrayList<>(Collections.emptyList()));
        messageFieldsMap.put(Missing.Mon_Point, new ArrayList<>(Collections.emptyList()));
        messageFieldsMap.put(Missing.Parameter, new ArrayList<>(Collections.emptyList()));
        messageFieldsMap.put(Missing.Rtn_Type, new ArrayList<>(Collections.emptyList()));
        messageFieldsMap.put(Missing.Site_Name, Collections.emptyList());
        /*
         * Add the (atomic) controlled list errors to the map
         */
        messageFieldsMap.put(ControlledList.MethodOrStandard, Collections.singletonList(FieldDefinition.Meth_Stand));
        messageFieldsMap.put(ControlledList.EA_ID, Collections.singletonList(FieldDefinition.EA_ID));
        messageFieldsMap.put(ControlledList.Parameter, Collections.singletonList(FieldDefinition.Parameter));
        messageFieldsMap.put(ControlledList.Qualifier, Collections.singletonList(FieldDefinition.Qualifier));
        messageFieldsMap.put(ControlledList.Ref_Period, Collections.singletonList(FieldDefinition.Ref_Period));
        messageFieldsMap.put(ControlledList.Rtn_Period, Collections.singletonList(FieldDefinition.Rtn_Period));
        messageFieldsMap.put(ControlledList.Rtn_Type, Collections.singletonList(FieldDefinition.Rtn_Type));
        messageFieldsMap.put(ControlledList.Txt_Value, Collections.singletonList(FieldDefinition.Txt_Value));
        messageFieldsMap.put(ControlledList.Unit, Collections.singletonList(FieldDefinition.Unit));
        /*
         * Add in the conflicts, optionally missing and dependency validations etc. The convention being applied is to
         * add first the primary data item - the header the error is being reported on and then
         * items in descending order of relevance.
         */
        messageFieldsMap.put(Conflict.ProhibitUnitForTxtValue, Arrays.asList(FieldDefinition.Unit, FieldDefinition.Txt_Value));
        messageFieldsMap.put(Missing.RequireCommentsForTxtValue, Collections.singletonList(FieldDefinition.Txt_Value));
        messageFieldsMap.put(Missing.RequireUnitForValue, Collections.singletonList(FieldDefinition.Value));
        messageFieldsMap.put(Missing.RequireUnitForValue, Collections.emptyList());
        messageFieldsMap.put(Conflict.RequireValueOrTxtValue, Arrays.asList(FieldDefinition.Value, FieldDefinition.Txt_Value));
        messageFieldsMap.put(DependencyConflict.Parameter, new ArrayList<>(Arrays.asList(FieldDefinition.Parameter, FieldDefinition.Rtn_Type)));
        messageFieldsMap.put(DependencyConflict.Unit, new ArrayList<>(Arrays.asList(FieldDefinition.Unit, FieldDefinition.Parameter, FieldDefinition.Rtn_Type)));
    }

    public static List<FieldDefinition> getFieldDependencies(String message) {
        return messageFieldsMap.get(message);
    }
}
