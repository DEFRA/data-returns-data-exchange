package uk.gov.defra.datareturns.data.model.route;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the md_route database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_route")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_route_id_seq")
        }
)
@Getter
@Setter
public class Route extends AbstractMasterDataEntity implements MasterDataEntity {
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    @Setter(AccessLevel.NONE)
    private Set<Subroute> subroutes = new HashSet<>();
}
