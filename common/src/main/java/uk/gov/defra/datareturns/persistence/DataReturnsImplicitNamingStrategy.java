package uk.gov.defra.datareturns.persistence;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitJoinTableNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;

/**
 * Custom version of the {@link org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy}
 * to use the component based hibernate naming strategy.
 * <p>
 * This affects JPA compliancy somewhat but reduces complexity in the API entity models and the time
 * taken to model these.  Enables use of repeated @{@link javax.persistence.Embeddable} types without
 * having to use @{@link javax.persistence.AttributeOverrides}
 *
 * @author Sam Gardner-Dell
 */
public class DataReturnsImplicitNamingStrategy extends ImplicitNamingStrategyComponentPathImpl {
    @Override
    public Identifier determineJoinTableName(final ImplicitJoinTableNameSource source) {
        final String name = source.getOwningPhysicalTableName() + "_"
                + source.getAssociationOwningAttributePath().getProperty();
        return toIdentifier(name, source.getBuildingContext());
    }
}
