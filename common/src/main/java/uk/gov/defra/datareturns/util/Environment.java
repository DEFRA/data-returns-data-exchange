package uk.gov.defra.datareturns.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.trim;

/**
 * Environent related utilities.
 */
public final class Environment {
    private static final Logger LOGGER = LoggerFactory.getLogger(Environment.class);

    // Private utility class constructor
    private Environment() {
    }

    /**
     * Hostname caching supplier
     */
    private static final CachingSupplier<String> HOSTNAME = CachingSupplier.of(() -> {
        String host = trim(defaultString(System.getenv("HOSTNAME"), System.getenv("COMPUTERNAME")));
        String osName = StringUtils.defaultString(System.getProperty("os.name"), "");
        if (host == null && StringUtils.containsAny(osName, "Linux", "OS X")) {
            try {
                Process proc = Runtime.getRuntime().exec("hostname");
                host = trim(IOUtils.toString(proc.getInputStream(), StandardCharsets.UTF_8));
            } catch (Throwable t) {
                // Ignored
            }
        }
        if (host == null) {
            try {
                host = InetAddress.getLocalHost().getHostName();
            } catch (Throwable t) {
                // Ignored
            }
        }
        return host;
    });

    private static final CachingSupplier<String> VERSION = CachingSupplier.of(() -> {
        String version = "UNKNOWN";
        try {
            version = PropertiesLoaderUtils.loadAllProperties("version.properties").getProperty("version");
        } catch (IOException e) {
            LOGGER.error("Unable to read version.properties");
        }
        return version;
    });

    /**
     * Retrieve the hostname of the system.
     * Attempts to use HOSTNAME/COMPUTERNAME environment variables first
     * Failing that, on MacOSX/Linus attempts to use the output of the hostname command
     * Finally attempts to resolve via DNS.
     *
     * @return the hostname as a String
     */
    public static String getHostname() {
        return HOSTNAME.get();
    }

    /**
     * Retrieve the application version from version.properties
     *
     * @return the version as a String
     */
    public static String getVersion() {
        return VERSION.get();
    }

    /**
     * Find all classes within a given package (and subpackages)
     *
     * @param packageName the package from which to search
     * @return a {@link List} of {@link Class}es within the package
     */
    public static List<Class<?>> findClasses(final String packageName) {
        return findClasses(packageName, (TypeFilter) null);
    }

    /**
     * Find all classes within a given package (and subpackages) and applies the given predicate to filter the list.
     *
     * @param packageName the package from which to search
     * @param predicate   the predicate used to filter the list of classes returned
     * @return a {@link List} of {@link Class}es within the package
     */
    public static List<Class<?>> findClasses(final String packageName, final Predicate<MetadataReader> predicate) {
        return findClasses(packageName, (metadataReader, metadataReaderFactory) -> predicate == null || predicate.test(metadataReader));
    }

    /**
     * Find all classes within a given package (and subpackages) and applies the given {@link TypeFilter} predicate to filter the list.
     *
     * @param packageName   the package from which to search
     * @param includeFilter the predicate used to filter the list of classes returned
     * @return a {@link List} of {@link Class}es within the package
     */
    public static List<Class<?>> findClasses(final String packageName, final TypeFilter includeFilter) {
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(includeFilter);
        final List<Class<?>> classes = new ArrayList<>();
        final Set<BeanDefinition> result = scanner.findCandidateComponents(packageName);
        for (final BeanDefinition defintion : result) {
            try {
                classes.add(Class.forName(defintion.getBeanClassName()));
            } catch (final ClassNotFoundException e) {
                LOGGER.error("Classpath scan returned reference to bean not found on classpath.", e);
            }
        }
        return classes;
    }
}
