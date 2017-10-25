package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.AliasingEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the units database table.
 *
 */
@Entity
@Table(name = "md_text_values")
@Cacheable
@GenericGenerator(name = AbstractMasterDataEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractMasterDataEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_text_values_id_seq") }
)
public class TextValue extends AbstractAliasingEntity<TextValue> implements AliasingEntity<TextValue> {
}