package uk.gov.defra.datareturns.data.model.nosep;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity(name = "md_nose_activity_process")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_nose_activity_process_id_seq")}
)
@Getter
@Setter
public class NoseProcess extends AbstractMasterDataEntity {

    @ManyToOne(optional = false)
    private NoseActivity noseActivity;
}
