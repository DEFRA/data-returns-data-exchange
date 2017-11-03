package uk.gov.defra.datareturns.data.model;

import org.springframework.data.rest.core.config.Projection;


/**
 * Default excerpt (projection used when listing a resource collection) for master data entities
 *
 * @author Sam Gardner-Dell
 */
@Projection(name = "basic", types = {MasterDataEntity.class})
public interface MasterDataExcerpt {
    /**
     * The id of the entity
     *
     * @return the id of the entity as a {@link Long}
     */
    Long getId();

    /**
     * The nomenclature (name/term/code) for the entity(natural key)
     *
     * @return the name of the entity
     */
    String getNomenclature();
}
