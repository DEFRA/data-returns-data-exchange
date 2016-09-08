/**
 *
 */
package uk.gov.ea.datareturns.tests.util;

import org.junit.Assert;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Unit test utlities
 *
 * @author Sam Gardner-Dell
 *
 */
public final class TestUtils {

    /**
     *
     */
    private TestUtils() {
    }

    /**
     * Verifies that a utility class is well defined.
     *
     * @param clazz utility class to verify.
     * @throws ReflectiveOperationException if a problem occurs attempting to reflect the given class
     */
    public static void assertUtilityClassWellDefined(final Class<?> clazz) throws ReflectiveOperationException {
        Assert.assertTrue("Utility classes must be final", Modifier.isFinal(clazz.getModifiers()));
        Assert.assertEquals("There must be only one constructor", 1, clazz.getDeclaredConstructors().length);
        final Constructor<?> constructor = clazz.getDeclaredConstructor();
        if (constructor.isAccessible() || !Modifier.isPrivate(constructor.getModifiers())) {
            Assert.fail("Utility class constructor is not private");
        }
        constructor.setAccessible(true);
        constructor.newInstance();
        constructor.setAccessible(false);
        for (final Method method : clazz.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) && method.getDeclaringClass().equals(clazz)) {
                Assert.fail("Encountered a non-static method within the utility class :" + method);
            }
        }
    }
}
