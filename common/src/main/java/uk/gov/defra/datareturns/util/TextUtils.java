package uk.gov.defra.datareturns.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Text processing utilities
 *
 * @author Sam Gardner-Dell
 */
public final class TextUtils {

    private TextUtils() {
    }

    /**
     * Process the provided {@link String} substituting any special characters based on the {@link CharacterSubstitution} enumeration
     * Multiple whitespace will be collapsed to a single space character.  Return values are always trimmed.
     *
     * @param inputString the {@link String} to be processed
     * @return the resultant {@link String} with special characters substituted
     */
    public static String normalize(final String inputString) {
        return normalize(inputString, WhitespaceHandling.COLLAPSE);
    }

    /**
     * Process the provided {@link String} substituting any special characters based on the {@link CharacterSubstitution} enumeration
     * Whitespace handling is determined by the whitespaceMode parameter.  Return values are always trimmed.
     *
     * @param inputString    the {@link String} to be processed
     * @param whitespaceMode determines how whitespace will be handled - see {@link WhitespaceHandling}
     * @return the resultant {@link String} with special characters substituted
     */

    public static String normalize(final String inputString, final WhitespaceHandling whitespaceMode) {
        final CharacterVisitor[] visitors = {
                new SubstitutionProcessor(),
                new WhitespaceProcessor(whitespaceMode != null ? whitespaceMode : WhitespaceHandling.COLLAPSE)
        };

        if (inputString != null) {
            final StringBuilder buf = new StringBuilder(Math.max(16, StringUtils.length(inputString)));
            for (int strPos = 0; strPos < inputString.length(); strPos++) {
                final Character input = inputString.charAt(strPos);
                Character output = input;

                for (final CharacterVisitor v : visitors) {
                    output = v.process(output);
                }

                if (output != null) {
                    buf.append(output);
                }
            }
            return buf.toString().trim();
        }
        return null;
    }

    /**
     * Simple main method to report the current substitution rules defined in this processor.
     *
     * @param args
     */
    public static void main(final String[] args) {
        final Map<Character, List<Character>> subsitutionSummary = new LinkedHashMap<>();

        for (final Map.Entry<Character, Character> entry : CharacterSubstitution.SUBSTITUTIONS.entrySet()) {
            List<Character> values = subsitutionSummary.get(entry.getValue());
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(entry.getKey());
            subsitutionSummary.put(entry.getValue(), values);
        }

        for (final Map.Entry<Character, List<Character>> group : subsitutionSummary.entrySet()) {
            System.out.println(System.lineSeparator()
                    + "Target: " + CharacterSubstitution.unicode(group.getKey())
                    + " (" + Character.getName(group.getKey()) + ")");
            for (final Character alias : group.getValue()) {
                System.out.println("Alias : " + CharacterSubstitution.unicode(alias)
                        + " (" + Character.getName(alias) + ")");
            }
        }
    }

    /**
     * Enumeration for whitespace handling when normalising
     */
    public enum WhitespaceHandling {
        /**
         * Multiple whitespaces should be collapsed to a single space character
         */
        COLLAPSE,
        /**
         * Whitepsace should be removed entirely
         */
        REMOVE
    }

    /**
     * Character substitution enumeration.
     */
    public enum CharacterSubstitution {
        APOSTROPHE(0x0027, null),
        LEFT_SINGLE_QUOTATION_MARK(0x2018, APOSTROPHE),
        RIGHT_SINGLE_QUOTATION_MARK(0x2019, APOSTROPHE),
        NKO_HIGH_TONE_APOSTROPHE(0x07f4, APOSTROPHE),
        ARMENIAN_APOSTROPHE(0x055a, APOSTROPHE),
        COMBINING_COMMA_ABOVE(0x0313, APOSTROPHE),
        COMBINING_COMMA_ABOVE_RIGHT(0x0315, APOSTROPHE),
        MODIFIER_LETTER_APOSTROPHE(0x02bc, APOSTROPHE),
        MODIFIER_LETTER_VERTICAL_LINE(0x02c8, APOSTROPHE),
        HEBREW_PUNCTUATION_GERESH(0x05f3, APOSTROPHE),
        LATIN_SMALL_LETTER_SALTILLO(0xa78c, APOSTROPHE),
        GRAVE_ACCENT(0x0060, APOSTROPHE),
        MODIFIER_LETTER_GRAVE_ACCENT(0x02cb, APOSTROPHE),
        COMBINING_GRAVE_ACCENT(0x0300, APOSTROPHE),
        REVERSED_PRIME(0x2035, APOSTROPHE),

        QUOTATION_MARK(0x0022, null),
        MODIFIER_LETTER_DOUBLE_PRIME(0x02ba, QUOTATION_MARK),
        COMBINING_DOUBLE_ACUTE_ACCENT(0x030b, QUOTATION_MARK),
        COMBINING_DOUBLE_VERTICAL_LINE_ABOVE(0x030e, QUOTATION_MARK),
        HEBREW_PUNCTUATION_GERSHAYIM(0x05f4, QUOTATION_MARK),
        //DOUBLE_PRIME(0x2033, QUOTATION_MARK),
        DITTO_MARK(0x3003, QUOTATION_MARK),
        LEFT_DOUBLE_QUOTATION_MARK(0x201c, QUOTATION_MARK),
        RIGHT_DOUBLE_QUOTATION_MARK(0x201d, QUOTATION_MARK),

        HYPHEN_MINUS(0x002d, null),
        ARMENIAN_HYPHEN(0x058a, HYPHEN_MINUS),
        HEBREW_PUNCTUATION_MAQAF(0x05be, HYPHEN_MINUS),
        MONGOLIAN_TODO_SOFT_HYPHEN(0x1806, HYPHEN_MINUS),
        HYPHEN(0x2010, HYPHEN_MINUS),
        NON_BREAKING_HYPHEN(0x2011, HYPHEN_MINUS),
        FIGURE_DASH(0x2012, HYPHEN_MINUS),
        EN_DASH(0x2013, HYPHEN_MINUS),
        EM_DASH(0x2014, HYPHEN_MINUS),
        HORIZONTAL_BAR(0x2015, HYPHEN_MINUS),
        TWO_EM_DASH(0x2e3a, HYPHEN_MINUS),
        THREE_EM_DASH(0x2e3b, HYPHEN_MINUS),
        SMALL_EM_DASH(0xfe58, HYPHEN_MINUS),
        SMALL_HYPHEN_MINUS(0xfe63, HYPHEN_MINUS),
        FULLWIDTH_HYPHEN_MINUS(0xff0d, HYPHEN_MINUS),

        SPACE(0x0020, null),
        LINE_SEPARATOR(0x2028, SPACE),
        IDEOGRAPHIC_SPACE(0x3000, SPACE),
        ZERO_WIDTH_SPACE(0x200b, SPACE),
        NO_BREAK_SPACE(0x00a0, SPACE),
        FOUR_PER_EM_SPACE(0x2005, SPACE),
        FIGURE_SPACE(0x2007, SPACE),
        THIN_SPACE(0x2009, SPACE),
        WORD_JOINER(0x2060, SPACE),
        ZERO_WIDTH_NO_BREAK_SPACE(0xfeff, SPACE),
        NARROW_NO_BREAK_SPACE(0x202f, SPACE),
        CHARACTER_TABULATION(0x0009, SPACE),
        LINE_FEED_LF(0x000a, SPACE),
        CARRIAGE_RETURN_CR(0x000d, SPACE);

        public static final Map<Character, Character> SUBSTITUTIONS;

        static {
            final Map<Character, Character> subMap = Arrays.stream(CharacterSubstitution.values())
                    .filter(e -> e.substitution() != null)
                    .collect(Collectors.toMap(e -> e.character(), e -> e.substitution().character()));
            SUBSTITUTIONS = Collections.unmodifiableMap(subMap);
        }

        private final int codepoint;
        private final CharacterSubstitution sub;

        CharacterSubstitution(final int codepoint, final CharacterSubstitution sub) {
            this.codepoint = codepoint;
            this.sub = sub;
        }

        /**
         * Retrieve the appropriate output {@link Character} for a given input {@link Character}
         *
         * @param c the {@link Character} for which an appropriate subsitution should be returned
         * @return the appropriate output {@link Character}, or null of no substitution is required
         */
        public static Character getSubstitute(final Character c) {
            return SUBSTITUTIONS.get(c);
        }

        /**
         * Is the given character considered to be whitespace
         *
         * @param c the {@link Character} to test
         * @return true if the character is treated as whitespace, false otherwise.
         */
        public static boolean isSpace(final Character c) {
            return c != null && (Character.isSpaceChar(c) || Character.isWhitespace(c));
        }

        /**
         * Retrieve the hex unicode reference for a given character
         *
         * @param c the desired character
         * @return the hex unicode reference for the given character
         */
        public static String unicode(final Character c) {
            return c != null ? String.format("\\u%04x", (int) c) : null;
        }

        /**
         * @return the {@link Character} to be substituted
         */
        public Character character() {
            return (char) codepoint;
        }

        /**
         * @return the reference to the enumeration entry which holds the appropriate substitution for this entry
         */
        public CharacterSubstitution substitution() {
            return sub;
        }
    }

    /**
     * Character visitor interface
     */
    public interface CharacterVisitor {
        /**
         * Process a character.  Called once per character as a string is processed from beginning to end.
         *
         * @param c the current character being processed
         * @return the output character, or null if no output should be generated for the current input
         */
        Character process(Character c);
    }

    /**
     * Processor to remove duplicate whitespace from a given string
     */
    public static class WhitespaceProcessor implements CharacterVisitor {
        private final WhitespaceHandling mode;
        private boolean inWhitespace = false;

        public WhitespaceProcessor(final WhitespaceHandling mode) {
            this.mode = mode;
        }

        @Override
        public Character process(final Character c) {
            final boolean wasInWhitespace = inWhitespace;
            inWhitespace = CharacterSubstitution.isSpace(c);
            return inWhitespace && (wasInWhitespace || WhitespaceHandling.REMOVE.equals(mode)) ? null : c;
        }
    }

    /**
     * Processor to perform substitution based on the rules defined int he {@link CharacterSubstitution} enumeration.
     */
    public static class SubstitutionProcessor implements CharacterVisitor {
        @Override
        public Character process(final Character c) {
            final Character substitute = CharacterSubstitution.getSubstitute(c);
            return substitute != null ? substitute : c;
        }
    }
}
