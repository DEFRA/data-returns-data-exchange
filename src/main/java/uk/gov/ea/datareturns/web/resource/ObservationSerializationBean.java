package uk.gov.ea.datareturns.web.resource;

import java.io.Serializable;

/**
 * @Author Graham Willis
 *
 * All observation implementations should have a class which implements this interface
 * It is used for serialization and deserialization between instances of the payload bean and
 * XML or JSON strings
 */
public interface ObservationSerializationBean extends Serializable {
}
