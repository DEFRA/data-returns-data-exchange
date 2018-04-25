package uk.gov.defra.datareturns.data.model.upload;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.dataset.Dataset;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Set;

/**
 * ECM Submission Record
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "ecm_upload")
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "ecm_upload_id_seq")
                  }
)
@Getter
@Setter
public class Upload extends AbstractBaseEntity {
    @Basic
    private String filename;

    @OneToMany(mappedBy = "upload", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Dataset> datasets;

    @ElementCollection
    private Set<ParserSummary> parserSummary;

    @ElementCollection
    private Set<Substitution> substitutions;
}
