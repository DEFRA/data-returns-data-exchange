package uk.gov.defra.datareturns.data.model.ewc;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Mapped superclass for all EWC based entities
 *
 * @author Sam Gardner-Dell
 */
@MappedSuperclass
@Getter
@Setter
public class AbstractEwcEntity extends AbstractMasterDataEntity {
    @Column(name = "code", length = 2, nullable = false)
    @NotBlank
    private String code;

    @Basic
    @Column(name = "description", length = 500, nullable = false)
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
    private String description;
}
