package uk.gov.defra.datareturns.data.model.ewc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.search.annotations.Indexed;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the EWC Chapters
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_ewc_chapter")
@Cacheable
@Indexed
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_ewc_chapter_id_seq")
                  }
)
@Getter
@Setter
public class EwcChapter extends AbstractEwcEntity {
    /**
     * The subchapters for this chapter.
     */
    @OneToMany(mappedBy = "ewcChapter")
    @Setter(AccessLevel.NONE)
    private Set<EwcSubchapter> ewcSubchapters = new HashSet<>();
}
