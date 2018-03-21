package uk.gov.defra.datareturns.data.model.regimeobligation;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.validator.constraints.NotBlank;
import uk.gov.defra.datareturns.data.model.AbstractBaseEntity;
import uk.gov.defra.datareturns.data.model.AbstractMasterDataEntity;
import uk.gov.defra.datareturns.data.model.MasterDataEntity;
import uk.gov.defra.datareturns.data.model.parameter.ParameterGroup;
import uk.gov.defra.datareturns.data.model.regime.Regime;
import uk.gov.defra.datareturns.data.model.route.Route;
import uk.gov.defra.datareturns.data.model.threshold.Threshold;
import uk.gov.defra.datareturns.data.model.unit.Unit;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the md_regime_obligation database table.
 *
 * @author Sam Gardner-Dell
 */
@Entity(name = "md_regime_obligation")
@Cacheable
@GenericGenerator(name = AbstractBaseEntity.DEFINITIONS_ID_GENERATOR,
                  strategy = AbstractBaseEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
                  parameters = {
                          @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_regime_obligation_id_seq")
                  }
)
@Getter
@Setter
public class RegimeObligation extends AbstractMasterDataEntity implements MasterDataEntity {
    @Column(name = "description", nullable = false)
    @NotBlank
    private String description;

    /**
     * Route this {@link RegimeObligation}  belongs to
     */
    @ManyToOne(optional = false)
    private Regime regime;

    /**
     * The {@link Route} this {@link RegimeObligation} applies to.  A null value indicates that this obligation is not return type specific.
     */
    @ManyToOne
    private Route route;
    /**
     * Parameter groups belonging to this {@link RegimeObligation}
     */
    @ManyToMany
    @Setter(AccessLevel.NONE)
    private Set<ParameterGroup> parameterGroups = new HashSet<>();

    /**
     * Unit groups belonging to this {@link RegimeObligation}
     */
    @ManyToMany
    @JoinTable(
            name = "md_regime_obligation_units",
            joinColumns = {@JoinColumn(name = "regime_obligation_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "unit_id", referencedColumnName = "id")})
    @Setter(AccessLevel.NONE)
    private Set<Unit> units = new HashSet<>();

    /**
     * The set of {@link Threshold}s associated with this {@link RegimeObligation}
     */
    @ManyToMany(mappedBy = "regimeObligation", cascade = CascadeType.ALL)
    @Setter(AccessLevel.NONE)
    private Set<Threshold> thresholds = new HashSet<>();
}
