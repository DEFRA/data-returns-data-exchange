package uk.gov.ea.datareturns.domain.jpa.repositories;

import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.PathBuilderFactory;
import com.querydsl.core.types.dsl.StringPath;

import java.lang.reflect.Field;

public class EntityPathUtils {

    /**
     * Retrieve a querydsl {@link StringPath} for a given field name
     *
     * @param entityClass the class of the entity defining the field
     * @param fieldName the name of the field
     * @param <T> generic type for the entity class
     * @return a {@link StringPath} for the given entity field
     * @throws RuntimeException if the field cannot be found
     */
    public static <T> StringPath getStringPath(Class<T> entityClass, String fieldName) {
        try {
            Field targetField = entityClass.getDeclaredField(fieldName);
            return getStringPath(entityClass, targetField);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve a querydsl {@link StringPath} for a given field name
     *
     * @param entityClass the class of the entity defining the field
     * @param field the field object for the target field
     * @param <T> generic type for the entity class
     * @return a {@link StringPath} for the given entity field
     * @throws RuntimeException if the field cannot be found
     */
    public static <T> StringPath getStringPath(Class<T> entityClass, Field field) {
        PathBuilderFactory factory = new PathBuilderFactory();
        PathBuilder<T> entity = factory.create(entityClass);
        return entity.getString(field.getName());
    }
}
