package uk.gov.ea.datareturns.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Text processing utilities
 *
 * @author Sam Gardner-Dell
 */
public class TextUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextUtils.class);

    /**
     * Process the provided {@link String} substituting any special characters based on the {@link CharacterSubstitution} enumeration
     *
     * @param inputString the {@link String} to be processed
     * @return the resultant {@link String} with special characters substituted
     */
    public static String normalize(String inputString) {
        final CharacterVisitor[] visitors = {
                new SubstitutionProcessor(), new WhitespaceProcessor()
        };

        if (inputString != null) {
            final StringBuilder buf = new StringBuilder(Math.max(16, StringUtils.length(inputString)));
            for (int strPos = 0; strPos < inputString.length(); strPos++) {
                Character input = inputString.charAt(strPos);
                Character output = input;

                for (CharacterVisitor v : visitors) {
                    output = v.process(output);
                }

                if (output != null) {
                    buf.append(output);
                }
            }
            return buf.toString();
        }
        return null;
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
        private boolean inWhitespace = false;

        @Override public Character process(Character c) {
            boolean wasInWhitespace = inWhitespace;
            inWhitespace = CharacterSubstitution.isSpace(c);
            return (wasInWhitespace && inWhitespace) ? null : c;
        }
    }

    /**
     * Processor to perform substitution based on the rules defined int he {@link CharacterSubstitution} enumeration.
     */
    public static class SubstitutionProcessor implements CharacterVisitor {
        @Override public Character process(Character c) {
            final Character substitute = CharacterSubstitution.getSubstitute(c);
            return substitute != null ? substitute : c;
        }
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

        private final int codepoint;
        private CharacterSubstitution sub;
        /** Map of aliases to their target character */
        private static final int CAPACITY = Math.round(CharacterSubstitution.values().length / 0.75f);
        public static final Map<Character, Character> SUBSTITUTIONS = new HashMap<>(CAPACITY);
        static {
            Arrays.stream(CharacterSubstitution.values())
                    .filter(e -> e.substitution() != null)
                    .forEach(e -> SUBSTITUTIONS.put(e.character(), e.substitution().character()));
        }

        CharacterSubstitution(int codepoint, CharacterSubstitution sub) {
            this.codepoint = codepoint;
            this.sub = sub;
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

        /**
         * Retrieve the appropriate output {@link Character} for a given input {@link Character}
         *
         * @param c the {@link Character} for which an appropriate subsitution should be returned
         * @return the appropriate output {@link Character}, or null of no substitution is required
         */
        public static Character getSubstitute(Character c) {
            return SUBSTITUTIONS.get(c);
        }

        /**
         * Is the given character considered to be whitespace
         *
         * @param c the {@link Character} to test
         * @return true if the character is treated as whitespace, false otherwise.
         */
        public static boolean isSpace(Character c) {
            return c != null && (Character.isSpaceChar(c) || Character.isWhitespace(c));
        }

        /**
         * Retrieve the hex unicode reference for a given character
         *
         * @param c the desired character
         * @return the hex unicode reference for the given character
         */
        public static String unicode(Character c) {
            return c != null ? String.format("\\u%04x", (int) c.charValue()) : null;
        }
    }

    /**
     * Simple main method to report the current substitution rules defined in this processor.
     *
     * @param args
     */
    public static void main(String[] args) {
        Map<Character, List<Character>> subsitutionSummary = new LinkedHashMap<>();

        for (Map.Entry<Character, Character> entry : CharacterSubstitution.SUBSTITUTIONS.entrySet()) {
            List<Character> values = subsitutionSummary.get(entry.getValue());
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(entry.getKey());
            subsitutionSummary.put(entry.getValue(), values);
        }

        for (Map.Entry<Character, List<Character>> group : subsitutionSummary.entrySet()) {
            System.out.println(System.lineSeparator()
                    + "Target: " + CharacterSubstitution.unicode(group.getKey())
                    + " (" + Character.getName(group.getKey()) + ")");
            for (Character alias : group.getValue()) {
                System.out.println("Alias : " + CharacterSubstitution.unicode(alias)
                        + " (" + Character.getName(alias) + ")");
            }
        }
    }
}