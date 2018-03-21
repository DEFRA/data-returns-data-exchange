package uk.gov.defra.datareturns.data.model.disposalsandrecoveries;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * The persistent class for the md_disposal_codes database table.
 *
 * @author Druid Wood Limited
 */
@Entity(name = "md_disposal_codes")
@Cacheable
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_disposal_codes_id_seq")
                  }
)
@Getter
@Setter
public class DisposalCode extends AbstractMasterDataEntity implements MasterDataEntity {
    @Basic
    @Column(name = "description", nullable = false)
    private String description;

}
