package uk.gov.ea.datareturns.domain.jpa.service;

/**********************************************************************

 Rules for the dependency table and cache

 **********************************************************************

 These symbols are used in the dependencies table

 *      - Any item - must supply (Not terminating)
 ^Item  - disallowed item (Terminating)
 Item   - allowed item (Not terminating)
 ^*     - No item or error (Not terminating)
 *-     - Any item but optionally not supplied (Not terminating)
 ...    - Irrelevant (Terminating)

 Example 1 - must be either Item1,Item2,Item3

 Item1
 Item2
 Item3

 Example 2 - must be either Item1, Item2, Item3 but optionally not supplied

 Item1
 Item2
 Item3
 *-

 Example 3 - must not be supplied otherwise error
 ^*

 Example 4 - must be either Item1, Item2, Item3 but not item4
 Item1
 Item2
 Item3
 ^Item4,...,...

 Example 5 - Any Item
 *

 Example 6 - Any item except Item4
 *
 ^Item4,...,...

 Example 7 Any item except Item4 but optionally not supplied
 ^Item4
 *-

 **************************************************************/
public class DependencyValidationSymbols {
    public static final String EXCLUDE = "^";
    public static final String EXCLUDE_ALL = "^*";
    public static final String INCLUDE_ALL_OPTIONALLY = "*-";
    public static final String INCLUDE_ALL = "*";
    public static final String NOT_APPLICABLE = "...";
}
