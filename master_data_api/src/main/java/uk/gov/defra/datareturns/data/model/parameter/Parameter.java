package uk.gov.defra.datareturns.data.model.parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import uk.gov.defra.datareturns.data.model.AbstractAliasedEntity;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AliasedEntity;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * The persistent class for the parameters database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_parameter")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_parameter_id_seq")
        }
)
@Getter
@Setter
public class Parameter extends AbstractAliasedEntity<ParameterAlias> implements AliasedEntity<ParameterAlias> {
    @Basic
    @Column(name = "cas", length = 50)
    @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
    private String cas;

    @JsonIgnore
    @Basic
    @Column(name = "type", length = 100)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String type;
}
