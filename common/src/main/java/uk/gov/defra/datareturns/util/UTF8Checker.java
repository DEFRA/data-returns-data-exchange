package uk.gov.defra.datareturns.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sam on 23/09/16.
 */
public final class UTF8Checker {
    public static final int MAX_ASCII = 127;
    public static final int MAX_ASCII_EXTENDED = 255;

    private UTF8Checker() {
    }

    public static void main(final String[] args) {
        if (args.length != 1 || !(new File(args[0]).exists())) {
            System.err.println("Specifiy exactly one argument containing the folder/filename to be checked");
            System.exit(1);
        }
        final File target = new File(args[0]);
        if (target.isDirectory()) {
            final File[] files = target.listFiles();
            if (files != null) {
                for (final File f : files) {
                    if (f.isFile()) {
                        System.out.println(buildFileReport(f, MAX_ASCII_EXTENDED));
                    }
                }
            }
        } else if (target.isFile()) {
            System.out.println(buildFileReport(target, MAX_ASCII_EXTENDED));
        }
    }

    public static String buildFileReport(final File file, final int maxAsciiAllowed) {
        final List<Violation> violations = UTF8Checker.checkFile(file, maxAsciiAllowed);
        final StringBuilder sb = new StringBuilder();

        sb.append(file.getName());
        sb.append(" contains ");
        sb.append(violations.size());
        sb.append(" violations");
        sb.append(System.lineSeparator());

        if (violations.size() > 0) {
            sb.append(String.format("[%25s (%5s:%-5s) Character Details   ] Data%n", "Filename", "Line", "Col"));
            for (final Violation v : violations) {
                sb.append(v.toString());
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    /**
     * Check a file for characters with decimal reference > maxAsciiAllowed
     *
     * @param file            the file to be checked
     * @param maxAsciiAllowed the maximum allowed character reference
     * @return a {@link List} of {@link Violation}s
     */
    public static List<Violation> checkFile(final File file, final int maxAsciiAllowed) {
        final List<Violation> violations = new ArrayList<>();
        try {
            final List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
            int lineNo = 0;
            for (final String line : lines) {
                lineNo++;

                for (int pos = 0; pos < line.length(); pos++) {
                    final char ch = line.charAt(pos);
                    if (ch > maxAsciiAllowed) {
                        violations.add(new Violation(file, line, lineNo, pos + 1, ch));
                    }
                    if (TextUtils.CharacterSubstitution.getSubstitute(ch) != null) {
                        violations.add(new Violation(file, line, lineNo, pos + 1, ch));
                    }
                }
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return violations;
    }

    /**
     * File violation data
     */
    public static class Violation {
        private final File file;
        private final String text;
        private final long line;
        private final long column;
        private final char badChar;

        /**
         * Create a new violation
         *
         * @param file
         * @param text
         * @param line
         * @param column
         * @param badChar
         */
        public Violation(final File file, final String text, final long line, final long column, final char badChar) {
            this.file = file;
            this.text = text;
            this.line = line;
            this.column = column;
            this.badChar = badChar;
        }

        public File getFile() {
            return file;
        }

        public String getText() {
            return text;
        }

        public long getLine() {
            return line;
        }

        public long getColumn() {
            return column;
        }

        public char getBadChar() {
            return badChar;
        }

        @Override
        public String toString() {
            return String
                    .format("[%25s (%5d:%-5d) Char: %-2c (U+%04X)   ] %s", file.getName(), line, column, badChar, badChar & 0x0FFFF, text);
        }
    }
}
