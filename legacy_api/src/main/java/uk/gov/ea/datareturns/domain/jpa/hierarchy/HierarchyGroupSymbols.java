package uk.gov.ea.datareturns.domain.jpa.hierarchy;

/**
 * @author Graham Willis
 * Utilities for hierarchy group syntax. A group is indicated by [Group name]
 */
public final class HierarchyGroupSymbols extends HierarchySymbols {
    private static final char GROUP_OPEN = '[';
    private static final char GROUP_CLOSE = ']';

    public static boolean isGroup(String item) {
        return item != null && !item.isEmpty()
                && item.charAt(0) == GROUP_OPEN
                && item.charAt(item.length() - 1) == GROUP_CLOSE;
    }

    public static String extractGroup(String item) {
        return isGroup(item) ? item.substring(1, item.length() - 1) : null;
    }

    public static String injectGroup(String item) {
        return "[" + item.trim() + "]";
    }
}
