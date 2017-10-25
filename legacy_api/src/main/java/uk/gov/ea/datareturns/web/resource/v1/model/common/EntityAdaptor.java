package uk.gov.ea.datareturns.web.resource.v1.model.common;

import uk.gov.ea.datareturns.domain.jpa.entities.userdata.Userdata;

/**
 * @author Graham Willis
 * Adaptor to convert between the objects in the RESTFul layer and the corresponding
 * persistence objects
 */
public interface EntityAdaptor<E extends EntityBase, U extends Userdata> {
    U convert(E e);
    E convert(U u);
    U merge(U u, E e);
}
