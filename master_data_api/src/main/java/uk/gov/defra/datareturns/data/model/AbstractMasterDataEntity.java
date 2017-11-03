package uk.gov.defra.datareturns.data.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.annotations.NaturalId;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.AnalyzerDefs;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.defra.datareturns.data.events.MasterDataUpdateEventListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

/**
 * Mapped superclass for master data entities
 *
 * @author Sam Gardner-Dell
 */
@MappedSuperclass
@AnalyzerDefs(
        {
                @AnalyzerDef(
                        name = "en",
                        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
                        filters = {
                                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                                @TokenFilterDef(factory = SnowballPorterFilterFactory.class,
                                        params = {
                                                @Parameter(name = "language", value = "English")
                                        })
                        })
        }
)
@EntityListeners(
        {
                MasterDataUpdateEventListener.class,
        }
)
@Getter
@Setter
public abstract class AbstractMasterDataEntity extends AbstractBaseEntity implements MasterDataEntity, Serializable {
    @NaturalId
    @Field(index = Index.YES, analyze = Analyze.YES, store = Store.YES)
    @Column(name = "nomenclature", nullable = false, unique = true)
    @NotBlank
    private String nomenclature;

    /*
     * Prevent subclasses from overriding equals and hashCode
     */
    @Override
    public final boolean equals(final Object o) {
        // Use interface for equality checking.
        if (this == o) {
            return true;
        }
        if (!(o instanceof MasterDataEntity)) {
            return false;
        }
        final MasterDataEntity that = (MasterDataEntity) o;
        return Objects.equals(getNomenclature(), that.getNomenclature());
    }

    /*
     * Prevent subclasses from overriding equals and hashCode
     */
    @Override
    public final int hashCode() {
        return Objects.hash(getNomenclature());
    }
}
