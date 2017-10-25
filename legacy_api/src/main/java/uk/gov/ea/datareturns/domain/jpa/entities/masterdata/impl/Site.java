package uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the unique_identifiers database table.
 *
 */
@Entity
@Table(name = "md_sites")
@Cacheable
@GenericGenerator(name = AbstractMasterDataEntity.DEFINITIONS_ID_GENERATOR,
        strategy = AbstractMasterDataEntity.DEFINITIONS_ID_SEQUENCE_STRATEGY,
        parameters = {
                @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "md_sites_id_seq") }
)
public class Site extends AbstractMasterDataEntity implements MasterDataEntity {
}