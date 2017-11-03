package uk.gov.defra.datareturns.data.model;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.QueryDslJpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * Base repository for all repositories
 *
 * @param <E>  the type of the entity
 * @param <ID> the type of the entity's primary key
 * @author Sam Gardner-Dell
 */
@NoRepositoryBean
@PreAuthorize("hasRole('USER')")
public interface BaseRepository<E, ID extends Serializable>
        extends JpaRepository<E, ID>, JpaSpecificationExecutor<E>, QueryDslPredicateExecutor<E> {
    /**
     * Perform a full-text search of the entity based on the given keywords.
     *
     * @param keywords the keywords to search for
     * @param fields   the fields to search
     * @return the {@link List} of matching entities
     */
    List<E> findByKeyword(final String keywords, final String[] fields);

    /**
     * @param <E>  the type of the entity
     *             Default implementation for the {@link BaseRepository}
     * @param <ID> the type of the entity's primary key
     */
    @Slf4j
    class BaseRepositoryImpl<E, ID extends Serializable> extends QueryDslJpaRepository<E, ID> implements BaseRepository<E, ID> {
        private final EntityInformation<E, ID> entityInformation;
        private final EntityManager entityManager;

        /**
         * Default constructor (as per spring data framework)
         *
         * @param entityInformation the {@link JpaEntityInformation} for the entity targeted by this repository
         * @param entityManager     the JPA entity manager.
         */
        public BaseRepositoryImpl(@SuppressWarnings("SpringJavaAutowiringInspection") final JpaEntityInformation<E, ID> entityInformation,
                                  final EntityManager entityManager) {
            super(entityInformation, entityManager);
            this.entityInformation = entityInformation;
            this.entityManager = entityManager;
        }

        /**
         * Allow subclasses to retrieve the entity manager
         *
         * @return the {@link EntityManager} backing the repository
         */
        protected EntityManager getEntityManager() {
            return entityManager;
        }

        @Override
        @Transactional(readOnly = true)
        public List<E> findByKeyword(final String keywords, final String[] fields) {
            // FIXME: We need to investigate the use of Elasticsearch to provide a common index for the API cluster.
            final FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

            try {
                fullTextEntityManager.createIndexer(this.entityInformation.getJavaType()).startAndWait();
            } catch (final InterruptedException e) {
                log.warn("Interrupted while building lucene index", e);
            }

            final QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                    .buildQueryBuilder()
                    .forEntity(this.entityInformation.getJavaType())
                    .get();

            final org.apache.lucene.search.Query luceneQuery = queryBuilder
                    .keyword()
                    //                    .wildcard()
                    //                    .fuzzy().withEditDistanceUpTo(2)
                    .onFields(fields)
                    .matching(keywords)
                    .createQuery();
            //
            final BooleanJunction<BooleanJunction> outer = queryBuilder.bool();
            outer.must(luceneQuery);

            final javax.persistence.Query jpaQuery = fullTextEntityManager.createFullTextQuery(
                    outer.createQuery(),
                    this.entityInformation.getJavaType()
            );
            return (List<E>) jpaQuery.getResultList();
        }
    }
}
