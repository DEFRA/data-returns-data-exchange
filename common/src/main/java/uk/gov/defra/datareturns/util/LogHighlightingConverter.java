package uk.gov.defra.datareturns.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiStyle;

/**
 * Colours log statements according to their severity.
 *
 * @author Sam Gardner-Dell
 */
public class LogHighlightingConverter extends ForegroundCompositeConverterBase<ILoggingEvent> {
    /* (non-Javadoc)
     * @see ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase#getForegroundColorCode(java.lang.Object)
     */
    @Override
    protected String getForegroundColorCode(final ILoggingEvent event) {
        final Level level = event.getLevel();
        switch (level.toInt()) {
            case Level.ERROR_INT:
                return toAnsi(AnsiStyle.BOLD, AnsiColor.BRIGHT_RED);
            case Level.WARN_INT:
                return toAnsi(AnsiStyle.NORMAL, AnsiColor.BRIGHT_YELLOW);
            case Level.INFO_INT:
                return toAnsi(AnsiStyle.BOLD, AnsiColor.CYAN);
            case Level.DEBUG_INT:
                return toAnsi(AnsiStyle.NORMAL, AnsiColor.CYAN);
            default:
                return toAnsi(AnsiStyle.NORMAL, AnsiColor.DEFAULT);
        }
    }

    /**
     * Generate the appropriate ANSI escape sequence for the given style and color
     *
     * @param style the style (bold, normal etc) - see {@link AnsiStyle}
     * @param color the colour - see {@link AnsiColor}
     * @return the ANSI code for the given style and colour
     */
    private static String toAnsi(final AnsiStyle style, final AnsiColor color) {
        return style.toString() + ";" + color.toString();
    }
}
