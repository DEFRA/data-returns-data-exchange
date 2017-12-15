package uk.gov.defra.datareturns.data.model.applicability;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifierGroup;
import uk.gov.defra.datareturns.data.model.parameter.ParameterGroup;
import uk.gov.defra.datareturns.data.model.returntype.ReturnTypeGroup;
import uk.gov.defra.datareturns.data.model.unit.UnitGroup;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the md_applicability database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_applicability")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_applicability_id_seq")
        }
)
@Getter
@Setter
public class Applicability extends AbstractMasterDataEntity implements MasterDataEntity {
    @ManyToMany
    private Set<UniqueIdentifierGroup> uniqueIdentifierGroups = new HashSet<>();

    @ManyToMany
    private Set<ReturnTypeGroup> returnTypeGroups = new HashSet<>();

    @ManyToMany
    private Set<ParameterGroup> parameterGroups = new HashSet<>();

    @ManyToMany
    private Set<UnitGroup> unitGroups = new HashSet<>();
}
