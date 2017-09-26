package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;

import javax.persistence.*;

/**
 * @author Sam Gardner-Dell
 * The persistent class for the units database table.
 *
 */
@Entity
@Table(name = "md_units")
@Cacheable
@GenericGenerator(name = AbstractMasterDataEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractMasterDataEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_units_id_seq") }
)
public class Unit extends AbstractAliasingEntity<Unit> implements AliasingEntity<Unit> {
    @Basic
    @Column(name = "long_name", length = 50)
    private String longName;

    @Basic
    @Column(name = "unicode", length = 50)
    private String unicode;

    @Basic
    @Column(name = "description", length = 200)
    private String description;

    @Basic
    @Column(name = "type", length = 50)
    private String type;

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}