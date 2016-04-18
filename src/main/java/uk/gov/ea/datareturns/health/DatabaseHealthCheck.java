package uk.gov.ea.datareturns.health;

import com.codahale.metrics.health.HealthCheck;
import org.hibernate.annotations.QueryHints;
import uk.gov.ea.datareturns.config.DataExchangeConfiguration;
import uk.gov.ea.datareturns.jpa.dao.ReturnTypeDao;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.Set;

/**
 * Health checks. Check basic connectivity. Reportable from the administration console
 **/
public class DatabaseHealthCheck extends HealthCheck {

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
        ReturnTypeDao dao = ReturnTypeDao.getInstance();
        EntityManager em = dao.createEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT COUNT(*) FROM data_returns_schema.return_types");
            Object res = query.getSingleResult();
            if (res instanceof BigInteger && ((BigInteger)res).compareTo(BigInteger.ZERO) != 0) {
                return Result.healthy();
            } else {
                throw new Exception();
            }
        } catch (Exception e) {

            return Result.unhealthy(String.format("Cannot connect to database or database not built: %s", this.config.getDatabase().getUrl() ));
        }
    }
}
