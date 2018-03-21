package uk.gov.defra.datareturns.data.model.textvalue;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractAliasedEntity;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AliasedEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;

/**
 * The persistent class for the text value database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_text_value")
@Cacheable
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_text_value_id_seq")
                  }
)
@Getter
@Setter
public class TextValue extends AbstractAliasedEntity<TextValueAlias> implements AliasedEntity<TextValueAlias> {
}
