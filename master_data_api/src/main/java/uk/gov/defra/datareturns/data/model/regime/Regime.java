package uk.gov.defra.datareturns.data.model.regime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifier;
import uk.gov.defra.datareturns.data.model.regimeobligation.RegimeObligation;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the md_regime database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_regime")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_regime_id_seq")
        }
)
@Getter
@Setter
public class Regime extends AbstractMasterDataEntity implements MasterDataEntity {
    @OneToMany(mappedBy = "regime")
    @Setter(AccessLevel.NONE)
    private Set<RegimeObligation> regimeObligations = new HashSet<>();

    @ManyToMany(mappedBy = "regime")
    private Set<UniqueIdentifier> uniqueIdentifiers;
}
