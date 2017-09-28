package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasedEntity;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Graham Willis
 * The persistent class for the unique_identifiers database table.
 *
 */
@Entity
@Table(name = "md_unique_identifiers")
@Cacheable
@GenericGenerator(name = AbstractMasterDataEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractMasterDataEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_unique_identifiers_id_seq") }
)
public class UniqueIdentifier extends AbstractMasterDataEntity implements AliasedEntity<UniqueIdentifierAlias> {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "site_id")
    private Site site;

    @JsonIgnore
    @OneToMany(mappedBy = "preferred", fetch = FetchType.EAGER, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Set<UniqueIdentifierAlias> aliases;

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @Override public Set<UniqueIdentifierAlias> getAliases() {
        return aliases;
    }

    @Override public void setAliases(Set<UniqueIdentifierAlias> aliases) {
        this.aliases = aliases;
    }
}