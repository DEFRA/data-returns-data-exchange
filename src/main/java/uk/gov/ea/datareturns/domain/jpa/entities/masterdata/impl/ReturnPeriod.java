package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;

import javax.persistence.*;

/**
 * The persistent class for the return_periods database table.
 *
 */
@Entity
@Table(name = "md_return_periods")
@Cacheable
@GenericGenerator(name = AbstractMasterDataEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractMasterDataEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_return_periods_id_seq") }
)
public class ReturnPeriod extends AbstractMasterDataEntity implements MasterDataEntity {
    @Basic
    @Column(name = "definition", nullable = false, length = 600)
    public String definition;

    @Basic
    @Column(name = "example", nullable = false, length = 20)
    public String example;

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String description) {
        this.definition = description;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}