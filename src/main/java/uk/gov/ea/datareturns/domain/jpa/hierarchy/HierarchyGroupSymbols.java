package uk.gov.ea.datareturns.domain.jpa.hierarchy;

/**
 * Created by graham on 21/11/16.
 */
public final class HierarchyGroupSymbols extends HierarchySymbols {
    private static final char GROUP_OPEN = '[';
    private static final char GROUP_CLOSE = ']';

    public static boolean isGroup(String item) {
        return item.charAt(0) == GROUP_OPEN && item.charAt(item.length() - 1) == GROUP_CLOSE;
    }

    public static String extractGroup(String item) {
        return item.substring(1, item.length() - 1);
    }
    public static String injectGroup(String item) {
        return "[" + item.trim() + "]";
    }
}
