package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;

import javax.persistence.*;

/**
 * @author Graham Willis
 * The persistent class for the unique_identifiers database table.
 */
@Entity
@Table(name = "md_unique_identifier_aliases")
@Cacheable
@GenericGenerator(name = AbstractMasterDataEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractMasterDataEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_unique_identifier_aliases_id_seq") }
)
public class UniqueIdentifierAlias extends AbstractMasterDataEntity implements MasterDataEntity, AliasingEntity<UniqueIdentifier> {
    @ManyToOne
    @JoinColumn(name = "preferred", nullable = false)
    private UniqueIdentifier preferred;

    @Override public UniqueIdentifier getPreferred() {
        return preferred;
    }

    @Override public void setPreferred(UniqueIdentifier preferred) {
        this.preferred = preferred;
    }
}