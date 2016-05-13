/**
 *
 */
package uk.gov.ea.datareturns;

import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiStyle;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.color.ForegroundCompositeConverterBase;

/**
 * @author Sam Gardner-Dell
 *
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

	private static String toAnsi(final AnsiStyle style, final AnsiColor color) {
		return style.toString() + ";" + color.toString();
	}
}
