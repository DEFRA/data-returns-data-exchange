package uk.gov.defra.datareturns.data.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * Mapped superclass for master data entities with aliasing capabilities
 *
 * @author Sam Gardner-Dell
 */
@MappedSuperclass
@Getter
@Setter
public abstract class AbstractAliasedEntity<E extends MasterDataEntity> extends AbstractMasterDataEntity implements AliasedEntity<E> {
    @OneToMany(mappedBy = "preferred", orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    @Setter(AccessLevel.NONE)
    private Set<E> aliases = new HashSet<>();
}
