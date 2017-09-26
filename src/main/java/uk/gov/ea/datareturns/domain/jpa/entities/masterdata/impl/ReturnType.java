package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;

import javax.persistence.*;

/**
 * The persistent class for the return_types database table.
 *
 */
@Entity
@Table(name = "md_return_types")
@Cacheable
@GenericGenerator(name = AbstractMasterDataEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractMasterDataEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_return_types_id_seq") }
)
public class ReturnType extends AbstractMasterDataEntity implements MasterDataEntity {
    @Basic
    @Column(name = "sector", nullable = false, length = 20)
    private String sector;

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
}