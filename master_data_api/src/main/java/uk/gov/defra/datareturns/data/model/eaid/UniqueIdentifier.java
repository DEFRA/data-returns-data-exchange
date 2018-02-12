package uk.gov.defra.datareturns.data.model.eaid;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractAliasedEntity;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AliasedEntity;
import uk.gov.defra.datareturns.data.model.site.Site;

import javax.persistence.*;

/**
 * The persistent class for the unique_identifiers database table.
 *
 * @author Graham Willis
 */
@Entity(name = "md_unique_identifier")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_unique_identifier_id_seq")}
)
@Getter
@Setter
public class UniqueIdentifier extends AbstractAliasedEntity<UniqueIdentifierAlias> implements AliasedEntity<UniqueIdentifierAlias> {

    public enum Type {
        IPC,
        RAS,
        IPPC,
        WML,
        WIA,
        EPRTR
    }

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Site site;

    @Column
    @Enumerated(EnumType.STRING)
    private Type type;

}
