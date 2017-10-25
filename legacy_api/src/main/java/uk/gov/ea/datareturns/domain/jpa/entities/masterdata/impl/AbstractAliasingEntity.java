package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasedEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;

import javax.persistence.*;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapped superclass for master data entities with aliasing capabilities
 *
 * @author Sam Gardner-Dell
 */
@MappedSuperclass
public abstract class AbstractAliasingEntity<E extends AbstractAliasingEntity> extends AbstractMasterDataEntity
        implements AliasingEntity<E>, AliasedEntity<E> {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "preferred")
    private E preferred;

    /*
     * @JsonIgnore annotation due to controlled list display and permit lookup serializing hibernate entities directly to json
     * rather than using a dto
     * Serializing the alias list objects will create a circular reference as each alias references the preferred
     * value (causing an infinite loop in the serializer).  For this reason, we use custom serialization to just output the names of the
     * aliases. Serialisation support for aliases provided by getAliasNames() method below.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "preferred", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private Set<E> aliases = null;

    public AbstractAliasingEntity() {
    }

    public E getPreferred() {
        return preferred;
    }

    public void setPreferred(E preferred) {
        this.preferred = preferred;
    }

    public Set<E> getAliases() {
        return aliases;
    }

    public void setAliases(Set<E> aliases) {
        this.aliases = aliases;
    }

    @JsonGetter("aliases")
    public Set<String> getAliasNames() {
        return Optional.ofNullable(getAliases()).orElse(Collections.emptySet())
                .stream()
                .map(E::getName)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public E getPrimary() {
        AbstractAliasingEntity<E> entity = this;
        while (entity.getPreferred() != null) {
            entity = entity.getPreferred();
        }
        return (E) entity;
    }
}