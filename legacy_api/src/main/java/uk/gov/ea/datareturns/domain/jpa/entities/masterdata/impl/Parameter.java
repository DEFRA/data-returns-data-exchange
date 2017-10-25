package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;

import javax.persistence.*;

/**
 * The persistent class for the parameters database table.
 *
 */
@Entity
@Table(name = "md_parameters")
@Cacheable
@GenericGenerator(name = AbstractMasterDataEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractMasterDataEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_parameters_id_seq") }
)
public class Parameter extends AbstractAliasingEntity<Parameter> implements AliasingEntity<Parameter> {
    @Basic
    @Column(name = "cas", length = 50)
    private String cas;

    @JsonIgnore
    @Basic
    @Column(name = "type", length = 100)
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCas() {
        return cas;
    }

    public void setCas(String cas) {
        this.cas = cas;
    }
}