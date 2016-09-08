package uk.gov.ea.datareturns.util;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles compilation and caching for Mustache templates
 *
 * @author Sam Gardner-Dell
 */
public final class MustacheTemplates {
    /** Class logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(MustacheTemplates.class);

    /** cache of compiled mustache templates */
    private static final Map<String, Template> compiledTemplates = Collections.synchronizedMap(new HashMap<>());

    /**
     * Private utility class constructor
     */
    private MustacheTemplates() {
    }

    /**
     * Retrieve a compiled mustache template for the given resource.  If the template has been used previously then the existing template
     * will be reused without the need to recompile
     *
     * @param resource the mustache template to resolve from the package
     * @return the compiled mustache template
     */
    public static Template get(String resource) {
        Template templ = null;
        try {
            final String templateText = IOUtils.toString(MustacheTemplates.class.getResourceAsStream(resource));
            templ = get(resource, templateText);
        } catch (IOException e) {
            LOGGER.error("Unable to read template file " + resource);
        }
        return templ;
    }

    /**
     * Retrieve a compiled mustache template for the given key and template text.  If a compiled template already exists for the given key
     * then this is returned and the supplied template text will be ignored (even if it differs from what the template was originally compiled with)
     *
     * @param key the key under which to cache the template
     * @param templateText the mustache template text.
     * @return the compiled mustache template
     */
    public static Template get(String key, String templateText) {
        Template templ = compiledTemplates.get(key);
        if (templ == null) {
            synchronized (compiledTemplates) {
                if (templ == null) {
                    LOGGER.info("Compiling mustache template for key " + key);
                    try {
                        final Mustache.Compiler compiler = Mustache.compiler()
                                .escapeHTML(false)
                                .emptyStringIsFalse(true)
                                .defaultValue("")
                                .nullValue("");
                        templ = compiler.compile(templateText);
                        compiledTemplates.put(key, templ);
                    } catch (Throwable t) {
                        LOGGER.error("Failed to compile mustache template for key " + key, t);
                    }
                }
            }
        }
        return templ;
    }
}