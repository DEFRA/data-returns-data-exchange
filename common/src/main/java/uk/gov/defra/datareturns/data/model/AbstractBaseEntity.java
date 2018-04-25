package uk.gov.defra.datareturns.data.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.util.Date;
import java.util.Objects;

/**
 * Base class for all entities represented by this API
 *
 * @author Sam Gardner-Dell
 */
@MappedSuperclass
@EntityListeners(
        {
                AuditingEntityListener.class
        }
)
@Getter
@Setter
public abstract class AbstractBaseEntity {
    /**
     * ID Generator Name
     */
    public static final String DEFINITIONS_ID_GENERATOR = "definitions_idgen";
    /**
     * ID Generator Strategy
     */
    public static final String DEFINITIONS_ID_SEQUENCE_STRATEGY = "org.hibernate.id.enhanced.SequenceStyleGenerator";

    /**
     * Primary key of the entity
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = DEFINITIONS_ID_GENERATOR)
    @Column(name = "id")
    @ApiModelProperty(readOnly = true)
    private Long id;

    /**
     * Creation date of the entity
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonProperty("_created")
    @ApiModelProperty(readOnly = true)
    @SuppressFBWarnings("EI_EXPOSE_REP")
    private Date created;

    /**
     * Last modified date of the entity
     */
    @LastModifiedDate
    @Column(nullable = false)
    @JsonProperty("_last_modified")
    @ApiModelProperty(readOnly = true)
    @SuppressFBWarnings("EI_EXPOSE_REP")
    private Date lastModified;

    /**
     * Version of the entity (ETag support)
     */
    @Version
    @Column(nullable = false)
    @ApiModelProperty(readOnly = true)
    private short version;

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
        final AbstractBaseEntity that = (AbstractBaseEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        if (getId() == null) {
            return System.identityHashCode(this);
        }
        return Objects.hashCode(getId());
    }
}
