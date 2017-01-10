package uk.gov.ea.datareturns.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import sun.awt.OSInfo;

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
    private Environment() {}

    /** Hostname caching supplier */
    private static final CachingSupplier<String> HOSTNAME = CachingSupplier.of(() -> {
        String host = trim(defaultString(System.getenv("HOSTNAME"), System.getenv("COMPUTERNAME")));
        if (host == null && (OSInfo.getOSType() == OSInfo.OSType.LINUX || OSInfo.getOSType() == OSInfo.OSType.MACOSX)) {
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
     * Find all classes within a given package (and subpackages)
     *
     * @param packageName the package from which to search
     * @return a {@link List} of {@link Class}es within the package
     */
    public static List<Class<?>> findClasses(String packageName) {
        return findClasses(packageName, null);
    }

    /**
     * Find all classes within a given package (and subpackages) and applies the given predicate to filter the list.
     *
     * @param packageName the package from which to search
     * @param predicate the predicate used to filter the list of classes returned
     * @return a {@link List} of {@link Class}es within the package
     */
    public static List<Class<?>> findClasses(String packageName, Predicate<MetadataReader> predicate) {
        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> predicate == null || predicate.test(metadataReader));
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