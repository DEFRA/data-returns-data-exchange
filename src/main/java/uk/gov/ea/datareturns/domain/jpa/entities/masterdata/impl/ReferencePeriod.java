package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;

import javax.persistence.*;

/**
 * The persistent class for the reference_periods database table.
 *
 */
@Entity
@Table(name = "md_reference_periods")
@Cacheable
@GenericGenerator(name = AbstractMasterDataEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractMasterDataEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_reference_periods_id_seq") }
)
public class ReferencePeriod
        extends AbstractAliasingEntity<ReferencePeriod>
        implements MasterDataEntity, AliasingEntity<ReferencePeriod> {

    @Basic
    @Column(name = "notes", length = 250)
    private String notes;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String description) {
        this.notes = description;
    }
}