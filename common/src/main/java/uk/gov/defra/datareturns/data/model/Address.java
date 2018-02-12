package uk.gov.defra.datareturns.data.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Embeddable address type
 *
 * @author Sam Gardner-Dell
 */
@Embeddable
@Getter
@Setter
public class Address {
    /**
     * First line of address
     */
    @Basic
    @Column(nullable = false)
    private String line1;

    /**
     * Second line of address (optional)
     */
    @Basic
    @Column(nullable = true)
    private String line2;

    /**
     * Town or city
     */
    @Basic
    @Column(nullable = false, length = 100)
    private String townOrCity;

    /**
     * Postal/zip code
     */
    @Basic
    @Column(nullable = false, length = 20)
    private String postCode;

    /**
     * ISO 3166-1 country code
     */
    @Basic
    @Column(nullable = false, length = 2)
    private String country;
}
