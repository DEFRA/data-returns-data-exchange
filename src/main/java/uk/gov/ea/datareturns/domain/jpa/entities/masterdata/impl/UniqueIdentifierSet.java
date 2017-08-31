package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "unique_identifier_sets")
@GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @org.hibernate.annotations.Parameter(name = "sequence_name", value = "unique_identifier_sets_id_seq") }
)
public class UniqueIdentifierSet implements Serializable {

    public enum UniqueIdentifierSetType {
        LARGE_LANDFILL_USERS, POLLUTION_INVENTORY
    }

    @Id
    @GeneratedValue(generator = "idGenerator")
    @JsonIgnore
    private Long id;

    @ManyToOne
    @JoinColumn(name="operator_id")
    private Operator operator;

    @Basic @Column(name = "unique_identifier_changed_date")
    private Instant uniqueIdentifierChangeDate;

    @Enumerated(EnumType.STRING) @Column(name = "type")
    private UniqueIdentifierSetType uniqueIdentifierSetType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Instant getUniqueIdentifierChangeDate() {
        return uniqueIdentifierChangeDate;
    }

    public void setUniqueIdentifierChangeDate(Instant uniqueIdentifierChangeDate) {
        this.uniqueIdentifierChangeDate = uniqueIdentifierChangeDate;
    }

    public UniqueIdentifierSetType getUniqueIdentifierSetType() {
        return uniqueIdentifierSetType;
    }

    public void setUniqueIdentifierSetType(UniqueIdentifierSetType uniqueIdentifierSetType) {
        this.uniqueIdentifierSetType = uniqueIdentifierSetType;
    }
}
