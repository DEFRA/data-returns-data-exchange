package uk.gov.defra.datareturns.data.model.eprtr;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "md_eprtr_sector")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_eprtr_sector_id_seq")}
)
@Getter
@Setter
public class EprtrSector extends AbstractMasterDataEntity {

    @Basic(optional = false)
    @Column(length = 40, unique = true)
    private String description;

    @OneToMany(mappedBy = "eprtrSector")
    @Setter(AccessLevel.NONE)
    private Set<EprtrActivity> eprtrActivities = new HashSet<>();
}
