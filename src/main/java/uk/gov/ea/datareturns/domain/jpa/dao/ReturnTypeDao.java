package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.ControlledListEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.ReturnType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DAO for return types.
 *
 * @author Graham Willis
 */
@Repository
public class ReturnTypeDao extends EntityDao {

	public ReturnTypeDao() {
		super(ReturnType.class);
	}

	// For the moment limit these to landfill
	protected Map<String, ReturnType> getCache() {
		if (cacheByName == null) {
			synchronized(this) {
				if (cacheByName == null) {
					LOGGER.info("Build name cache of: " + entityClass.getSimpleName());
					List<ReturnType> results = entityManager.createQuery(
							"select t from ReturnType t where t.sector = :s ", ReturnType.class)
							.setParameter("s", "Landfill")
							.getResultList();

					cacheByName = results
							.stream()
							.collect(Collectors.toMap(ControlledListEntity::getName, k -> k));
				} else {
					return this.cacheByName;
				}
			}
		}
		return this.cacheByName;
	}
}