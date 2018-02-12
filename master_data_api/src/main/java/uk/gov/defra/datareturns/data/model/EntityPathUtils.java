package uk.gov.defra.datareturns.data.model;

import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.core.types.dsl.StringPath;

import java.lang.reflect.Field;

public final class EntityPathUtils {

    private EntityPathUtils() {
    }

    /**
     * Retrieve a querydsl {@link StringPath} for a given field name
     *
     * @param entityClass the class of the entity defining the field
     * @param fieldName   the name of the field
     * @param <T>         generic type for the entity class
     * @return a {@link StringPath} for the given entity field
     * @throws RuntimeException if the field cannot be found
     */
    public static <T> StringPath getStringPath(final Class<T> entityClass, final String fieldName) {
        try {
            final Field targetField = entityClass.getDeclaredField(fieldName);
            return getStringPath(entityClass, targetField);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve a querydsl {@link StringPath} for a given field name
     *
     * @param entityClass the class of the entity defining the field
     * @param field       the field object for the target field
     * @param <T>         generic type for the entity class
     * @return a {@link StringPath} for the given entity field
     * @throws RuntimeException if the field cannot be found
     */
    public static <T> StringPath getStringPath(final Class<T> entityClass, final Field field) {
        final PathBuilderFactory factory = new PathBuilderFactory();
        final PathBuilder<T> entity = factory.create(entityClass);
        return entity.getString(field.getName());
    }
}
