package uk.gov.defra.datareturns.data.model.submissions;

import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.gov.defra.datareturns.data.BaseRepository;

import java.util.List;
import java.util.Set;


/**
 * Spring REST repository for {@link Submission} entities
 *
 * @author Sam Gardner-Dell
 */
@RepositoryRestResource
public interface SubmissionRepository extends BaseRepository<Submission, Long> {

    /**
     * Retrieve a submission by a given reporting reference and the year that it is applicable to
     *
     * @param reportingReference the reporting reference of the submission to lookup
     * @param applicableYear     the year that the returned submission should be applicable to
     * @return the {@link Submission} for the given reporting reference and year or null if not found.
     */

    @SuppressWarnings("unused")
    List<Submission> getByReportingReferenceAndApplicableYear(@Param("reporting_reference") Long reportingReference,
                                                              @Param("applicable_year") Short applicableYear);

    /**
     * Retrieve a list of {@link Submission}s for the given reporting reference
     *
     * @param reportingReference the reporting reference of the submission to lookup
     * @return a {@link List} of the available {@link Submission}s for the given reporting reference
     */

    @SuppressWarnings("unused")
    List<Submission> findByReportingReference(@Param("reporting_reference") Long reportingReference);

    /**
     * Retrieve a list of {@link Submission}s for a set of reporting references for a given year
     *
     * @return
     */
    @SuppressWarnings("unused")
    List<Submission> findByReportingReferenceInAndApplicableYear(@Param("reporting_references") Set<Long> reportingReferences,
                                                                 @Param("applicable_year") Short applicableYear);

    @SuppressWarnings("unused")
    List<Submission> findByApplicableYear(@Param("applicable_year") Short applicableYear);
}
