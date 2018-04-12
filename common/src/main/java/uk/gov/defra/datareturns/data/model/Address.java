package uk.gov.defra.datareturns.data.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import uk.gov.defra.datareturns.validation.validators.address.Iso3166CountryCode;

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
    @Length(message = "ADDRESS_LINE1_MAX_LENGTH_EXCEEDED", max = 255)
    private String line1;

    /**
     * Second line of address (optional)
     */
    @Basic
    @Column
    @Length(message = "ADDRESS_LINE2_MAX_LENGTH_EXCEEDED", max = 255)
    private String line2;

    /**
     * Town or city
     */
    @Basic
    @Column(nullable = false, length = 100)
    @Length(message = "ADDRESS_TOWN_OR_CITY_MAX_LENGTH_EXCEEDED", max = 100)
    private String townOrCity;

    /**
     * Postal/zip code
     */
    @Basic
    @Column(length = 20)
    @Length(message = "ADDRESS_POST_CODE_MAX_LENGTH_EXCEEDED", max = 20)
    private String postCode;

    /**
     * ISO 3166-2 alpha2 code.
     */
    @Basic
    @Column(nullable = false, length = 2)
    @Length(message = "ADDRESS_COUNTRY_MAX_LENGTH_EXCEEDED", max = 2)
    @Iso3166CountryCode
    private String country;
}
