package uk.gov.ea.datareturns.domain.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.core.EntityInformation;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Base repository for all repositories
 *
 * @param <E> the type of the entity
 * @param <ID> the type of the entity's primary key
 * @author Sam Gardner-Dell
 */
@NoRepositoryBean
public interface BaseRepository<E, ID extends Serializable>
        extends JpaRepository<E, ID>, JpaSpecificationExecutor<E>, QueryDslPredicateExecutor<E> {

    /**
     * Retrieve the class of the domain entity
     * @return the {@link Class} for the domain entity
     */
    Class<E> getDomainClass();

    /**
     * Default implementation for the {@link BaseRepository}
     * @param <E> the type of the entity
     * @param <ID> the type of the entity's primary key
     */
    class BaseRepositoryImpl<E, ID extends Serializable> extends QueryDslJpaRepository<E, ID> implements BaseRepository<E, ID> {
        private final EntityInformation<E, ID> entityInformation;
        private final EntityManager entityManager;

        /**
         * Default constructor (as per spring data framework)
         * @param entityInformation the {@link JpaEntityInformation} for the entity targeted by this repository
         * @param entityManager the JPA entity manager.
         */
        public BaseRepositoryImpl(@SuppressWarnings("SpringJavaAutowiringInspection") JpaEntityInformation<E, ID> entityInformation,
                EntityManager entityManager) {
            super(entityInformation, entityManager);
            this.entityInformation = entityInformation;
            this.entityManager = entityManager;
        }

        @Override public Class<E> getDomainClass() {
            return this.entityInformation.getJavaType();
        }

        /**
         * Allow subclasses to retrieve the entity manager
         *
         * @return the {@link EntityManager} backing the repository
         */
        protected EntityManager getEntityManager() {
            return entityManager;
        }
    }
}
