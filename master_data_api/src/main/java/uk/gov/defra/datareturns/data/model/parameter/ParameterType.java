package uk.gov.defra.datareturns.data.model.parameter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;
import uk.gov.defra.datareturns.data.model.unit.Unit;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the parameter types database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_parameter_type")
@Cacheable
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_parameter_type_id_seq")}
)
@Getter
@Setter
public class ParameterType extends AbstractMasterDataEntity implements MasterDataEntity {
    @OneToMany(mappedBy = "type")
    @Setter(AccessLevel.NONE)
    private Set<Parameter> parameters = new HashSet<>();
}
