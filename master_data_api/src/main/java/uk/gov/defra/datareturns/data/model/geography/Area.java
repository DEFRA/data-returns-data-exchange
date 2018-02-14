package uk.gov.defra.datareturns.data.model.geography;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;
import uk.gov.defra.datareturns.data.model.eaid.UniqueIdentifier;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the unique_identifiers database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_area")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_area_id_seq")}
)
@Getter
@Setter
public class Area extends AbstractMasterDataEntity implements MasterDataEntity {


    @ManyToOne(optional = false)
    private Region region;

    @Basic
    @Column(name = "description", length = 255, nullable = false)
    private String description;
}
