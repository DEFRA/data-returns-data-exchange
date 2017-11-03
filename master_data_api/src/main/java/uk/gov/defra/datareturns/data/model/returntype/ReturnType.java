package uk.gov.defra.datareturns.data.model.returntype;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * The persistent class for the return_types database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_return_type")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_return_type_id_seq") }
)
@Getter @Setter
public class ReturnType extends AbstractMasterDataEntity implements MasterDataEntity {
    @Basic
    @Column(name = "applicability", nullable = false, length = 20)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String applicability;
}
