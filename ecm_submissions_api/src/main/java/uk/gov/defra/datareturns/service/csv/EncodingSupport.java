package uk.gov.defra.datareturns.service.csv;

import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by sam on 27/06/17.
 */
@Slf4j
public final class EncodingSupport {
    private static final Set<Charset> SUPPORTED_CHARSETS = new HashSet<>(Arrays.asList(
            StandardCharsets.UTF_8,
            StandardCharsets.UTF_16,
            StandardCharsets.UTF_16LE,
            StandardCharsets.UTF_16BE,
            StandardCharsets.ISO_8859_1,
            Charset.forName("ISO-8859-15"),
            Charset.forName("windows-1252")
    ));

    /**
     * Private utility class constructor
     */
    private EncodingSupport() {

    }

    /**
     * Attempts to detect the character set used to encode the given byte array.
     * <p>
     * Assumes UTF-8 if the character set cannot be automatically detected (or if the data contains no specially encoded characters)
     *
     * @param data the byte array to test
     * @return the correct character set used to encode the data (defaults to UTF8 if the charset cannot be detected)
     */
    public static Charset detectCharset(final byte[] data) {
        final UniversalDetector detector = new UniversalDetector();
        detector.handleData(data);
        detector.dataEnd();

        // Default to expect UTF-8 encoded data.
        Charset charset = StandardCharsets.UTF_8;
        if (detector.getDetectedCharset() != null) {
            try {
                final Charset detected = Charset.forName(detector.getDetectedCharset());
                if (SUPPORTED_CHARSETS.contains(detected)) {
                    charset = detected;
                }
            } catch (final IllegalArgumentException e) {
                log.warn("Unable to load system charset for the type detected - " + detector.getDetectedCharset());
            }
        }
        return charset;
    }
}
