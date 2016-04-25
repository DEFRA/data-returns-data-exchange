package uk.gov.ea.datareturns.health;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;

import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.jpa.dao.ReturnTypeDao;
import uk.gov.ea.datareturns.resource.DataExchangeResource;

/**
 * Health checks. Check basic connectivity. Reportable from the administration console
 **/
public class DatabaseHealthCheck extends HealthCheck {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataExchangeResource.class);

    private DataExchangeConfiguration config;

    public DatabaseHealthCheck(DataExchangeConfiguration config) {
        this.config = config;
    }

    /**
     * Determine if the database is reachable bypassing the the hibernate mapping and the
     * cache.
     *
     * @return Health Result
     */
    @Override
    protected Result check() throws Exception {
    	Result healthCheckResult = Result.unhealthy(String.format("Cannot connect to database or database not built: %s", this.config.getDatabase().getUrl() ));
    	
        ReturnTypeDao dao = ReturnTypeDao.getInstance();
        EntityManager em = dao.createEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT COUNT(*) FROM data_returns_schema.return_types");
            Object res = query.getSingleResult();
            if (res instanceof BigInteger && ((BigInteger)res).compareTo(BigInteger.ZERO) != 0) {
            	healthCheckResult = Result.healthy();
            }
        } catch (Throwable e) {
        	LOGGER.error("Database Health Check Failure", e);
        } finally {
        	em.close();
        }
        return healthCheckResult;
    }
}
