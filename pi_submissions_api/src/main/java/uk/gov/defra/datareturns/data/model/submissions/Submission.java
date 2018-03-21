package uk.gov.defra.datareturns.data.model.submissions;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.springframework.data.rest.core.annotation.RestResource;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.releases.Release;
import uk.gov.defra.datareturns.data.model.transfers.OffsiteWasteTransfer;
import uk.gov.defra.datareturns.data.model.transfers.OverseasWasteTransfer;
import uk.gov.defra.datareturns.validation.validators.submission.ValidSubmission;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import java.util.Objects;
import java.util.Set;

/**
 * PI Submission
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "pi_submission")
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uniq_reference_and_year", columnNames = {"reportingReference", "applicableYear"})
})
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "pi_submission_id_seq")
                  }
)
@Audited
@Getter
@Setter
@ValidSubmission
public class Submission extends AbstractBaseEntity {
    /**
     * The unique identifier for the operation the submission relates to (permit or reference number)
     */
    @Column(nullable = false)
    private Long reportingReference;

    /**
     * The year that the submission is applicable to (not necessarily the year of submission)
     */
    @Column(nullable = false)
    private short applicableYear;

    @Enumerated(EnumType.STRING)
    private SubmissionStatus status;

    @Column(nullable = false)
    private short naceId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pi_submission_nose_process", joinColumns = @JoinColumn(name = "submission_id"))
    @Column(name = "nose_process_id")
    private Set<Long> noseIds;

    /*
    TODO: Investigate/report bug with the spring data rest framework.

    This field has had to be called "releasesData" to avoid a strange issue with the spring data rest framework.  When named "releases" without
    any camel-casing attempting to POST a new submission with an array of releases would result in the following error:
    {"cause":null,"message":"Can not handle managed/back reference 'defaultReference': type: value deserializer of type org.springframework.data
    .rest.webmvc.json.PersistentEntityJackson2Module$UriStringDeserializer does not support them"}

    As a workaround, applying the following annotations to re-map it to "releases" works OK.
        @RestResource(path = "releases", rel = "releases")
        @JsonProperty(value = "releases")

    Once the bug is resolved in the SDR framework, the property can be renamed back to "releases" and these annotations can be removed.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "submission")
    @RestResource(path = "releases", rel = "releases")
    @JsonProperty(value = "releases")
    @JsonManagedReference
    @Valid
    private Set<Release> releasesData;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "submission")
    @JsonManagedReference
    @Valid
    private Set<OverseasWasteTransfer> overseasWasteTransfers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "submission")
    @JsonManagedReference
    @Valid
    private Set<OffsiteWasteTransfer> offsiteWasteTransfers;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (getId() == null) {
            return false;
        }
        final Submission that = (Submission) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getId());
    }


}
