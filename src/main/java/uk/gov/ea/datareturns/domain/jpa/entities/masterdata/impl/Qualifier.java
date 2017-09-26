package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;

import javax.persistence.*;

/**
 * The persistent class for the qualifiers database table.
 *
 */
@Entity
@Table(name = "md_qualifiers")
@Cacheable
@GenericGenerator(name = AbstractMasterDataEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractMasterDataEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_qualifiers_id_seq") }
)
public class Qualifier extends AbstractMasterDataEntity implements MasterDataEntity {
    @Basic
    @Column(name = "notes", length = 100)
    private String notes;

    @Basic
    @Column(name = "type", length = 50)
    private String type;

    @Basic
    @Column(name = "singleormultiple", length = 20)
    private String singleOrMultiple;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String description) {
        this.notes = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String suggested_category) {
        this.type = suggested_category;
    }

    public String getSingleOrMultiple() {
        return singleOrMultiple;
    }

    public void setSingleOrMultiple(String singleOrMultiple) {
        this.singleOrMultiple = singleOrMultiple;
    }
}