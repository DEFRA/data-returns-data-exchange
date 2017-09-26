package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import org.hibernate.annotations.NaturalId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.repositories.events.MasterDataUpdateEventListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Mapped superclass for master data entities
 *
 * @author Sam Gardner-Dell
 */
@MappedSuperclass
@EntityListeners(MasterDataUpdateEventListener.class)
public abstract class AbstractMasterDataEntity implements MasterDataEntity, Serializable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractMasterDataEntity.class);

    public static final String DEFINITIONS_ID_GENERATOR = "definitions_idgen";
    public static final String DEFINITIONS_ID_SEQUENCE_STRATEGY = "org.hibernate.id.enhanced.SequenceStyleGenerator";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DEFINITIONS_ID_GENERATOR)
    @Column(name = "id")
    private Long id;

    @NaturalId
    @Column(name = "name", nullable = false)
    private String name;

    public AbstractMasterDataEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * Prevent subclasses from overriding equals and hashCode
     */
    @Override public final boolean equals(Object o) {
        // Use interface for equality checking.
        if (this == o)
            return true;
        if (!(o instanceof MasterDataEntity))
            return false;
        MasterDataEntity that = (MasterDataEntity) o;
        return Objects.equals(getName(), that.getName());
    }

    /*
     * Prevent subclasses from overriding equals and hashCode
     */
    @Override public final int hashCode() {
        return Objects.hash(getName());
    }
}