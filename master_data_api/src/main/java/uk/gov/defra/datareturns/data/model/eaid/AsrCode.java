package uk.gov.defra.datareturns.data.model.eaid;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "md_asr_code")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_asr_code_id_seq")
                  }
)
@Getter
@Setter
public class AsrCode extends AbstractMasterDataEntity implements MasterDataEntity {
    @Basic
    @Column(name = "asr_full_code_description", nullable = false, length = 80)
    private String asrFullCodeDescription;

    @Basic
    @Column(name = "asr_code", nullable = false, length = 40)
    private String asrCode;

    @Basic
    @Column(name = "asr_code_description", nullable = false, length = 80)
    private String asrCodeDescription;
}
