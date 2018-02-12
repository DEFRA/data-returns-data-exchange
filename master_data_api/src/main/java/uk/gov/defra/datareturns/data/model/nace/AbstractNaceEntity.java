package uk.gov.defra.datareturns.data.model.nace;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Base type for NACE codes
 * <p>
 * See http://ec.europa.eu/eurostat/documents/3859598/5902521/KS-RA-07-015-EN.PDF
 * <p>
 * NACE consists of a hierarchical structure (as established in the NACE Regulation), the introductory guidelines and
 * the explanatory notes. The structure of NACE is described in the NACE Regulation as follows:
 * <p>
 * i. a first level consisting of headings identified by an alphabetical code (sections),
 * ii. a second level consisting of headings identified by a two-digit numerical code (divisions),
 * iii. a third level consisting of headings identified by a three-digit numerical code (groups),
 * iv. a fourth level consisting of headings identified by a four-digit numerical code (classes)..
 * <p>
 * The code for the section level is not integrated in the NACE nomenclature that identifies the division, the group and the class
 * describing a specific activity. For example, the activity "Manufacture of glues" is identified by the code 20.52, where 20 is the
 * nomenclature for the division, 20.5 is the code for the group and 20.52 is the nomenclature of the class; section C, to which this class
 * belongs, does not appear in the code itself.
 * <p>
 * In cases where a given level of the classification is not divided further down in the classification, "0" is used in the code position
 * for the next more detailed level. For example, the code for the class "Veterinary activities" is 75.00 because the division
 * "Veterinary activities" (code 75) is divided neither into groups nor into classes. The class "Manufacture of beer" is coded as 11.05
 * since the division "Manufacture of beverages" (code 11) is not divided into several groups but the group "Manufacture of beverages"
 * (code 11.0) is divided into classes.
 *
 * @author Sam Gardner-Dell
 */
@MappedSuperclass
@Getter
@Setter
abstract class AbstractNaceEntity extends AbstractMasterDataEntity {
    @Basic
    @Column(name = "description", length = 255, nullable = false)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String description;

    @Basic
    @Column(name = "details", length = 2000)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String details;
}
