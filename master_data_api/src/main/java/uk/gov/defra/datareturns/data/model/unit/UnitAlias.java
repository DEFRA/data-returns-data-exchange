package uk.gov.defra.datareturns.data.model.unit;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.AliasingEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * The persistent class for unit aliases.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_unit_alias")
@Cacheable
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(
                                  name = SequenceStyleGenerator.SEQUENCE_PARAM,
                                  value = "md_unit_alias_id_seq"
                          )
                  }
)
@Getter
@Setter
public class UnitAlias extends AbstractMasterDataEntity implements MasterDataEntity, AliasingEntity<Unit> {
    @ManyToOne
    @JoinColumn(name = "preferred", nullable = false)
    private Unit preferred;
}
