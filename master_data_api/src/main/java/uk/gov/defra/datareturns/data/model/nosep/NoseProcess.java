package uk.gov.defra.datareturns.data.model.nosep;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.*;
import org.hibernate.search.annotations.Index;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "md_nose_process")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_nose_process_id_seq")}
)
@Getter
@Setter
public class NoseProcess extends AbstractMasterDataEntity {

    @Basic
    @Column(name = "description", nullable = false)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "md_nose_activity_process",
            joinColumns = @JoinColumn(name = "nose_process_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name = "nose_activity_id", referencedColumnName = "id")
    )
    private Set<NoseActivity> noseActivities = new HashSet<>();
}
