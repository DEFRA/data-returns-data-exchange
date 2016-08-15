package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.PersistedEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DAO for return types.
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class ReturnTypeDao extends AbstractJpaDao {

	public ReturnTypeDao() {
		super(ReturnType.class);
	}

	// For the moment limit these to landfill
	protected Map<String, ReturnType> getCache() {
		if (cacheByName == null) {
			synchronized(this) {
				if (cacheByName == null) {
					LOGGER.info("Build cache of: " + entityClass.getSimpleName());
					List<ReturnType> results = entityManager.createQuery(
							"select t from ReturnType t where t.sector = :s ", ReturnType.class)
							.setParameter("s", "Landfill")
							.getResultList();

					cacheByName = results
							.stream()
							.collect(Collectors.toMap(PersistedEntity::getName, k -> k));
				} else {
					return this.cacheByName;
				}
			}
		}
		return this.cacheByName;
	}

}