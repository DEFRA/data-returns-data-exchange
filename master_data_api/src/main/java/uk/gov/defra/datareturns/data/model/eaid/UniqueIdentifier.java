package uk.gov.defra.datareturns.data.model.eaid;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractAliasedEntity;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AliasedEntity;
import uk.gov.defra.datareturns.data.model.Context;
import uk.gov.defra.datareturns.data.model.geography.Area;
import uk.gov.defra.datareturns.data.model.regime.Regime;
import uk.gov.defra.datareturns.data.model.site.Site;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
            name="md_regime_unique_identifiers",
            joinColumns={ @JoinColumn(name="unique_identifier_id", referencedColumnName="id") },
            inverseJoinColumns={ @JoinColumn(name="regime_id", referencedColumnName="id" ) })
    @MapKeyColumn(name = "context")
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Context, Regime> regime = new HashMap<>();

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private Site site;

    @ManyToOne(cascade = CascadeType.ALL)
    private Operator operator;

    @ManyToOne(cascade = CascadeType.ALL)
    private Area area;

    @ManyToOne(cascade = CascadeType.ALL)
    private AsrCode asrCode;

}
