package uk.gov.ea.datareturns.domain.jpa.entities.userdata;

/**
 * @Author Graham Willis
 * Interface defining the user metadata - i.e. datasets, records etc.
 */
public interface Metadata extends Userdata {
    // Each item of metadata has a string identifier
    String getIdentifier();
    void setIdentifier(String identifier);
}
