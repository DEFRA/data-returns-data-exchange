package uk.gov.defra.datareturns.data.model.releaseortransfer;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

/**
 * The persistent class for the methods_or_standards database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_release_or_transfer")
@Cacheable
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(
                        name = SequenceStyleGenerator.SEQUENCE_PARAM,
                        value = "md_release_or_transfer_id_seq"
                )
        }
)
@Getter
@Setter
public class ReleaseOrTransfer extends AbstractMasterDataEntity implements MasterDataEntity {
}
