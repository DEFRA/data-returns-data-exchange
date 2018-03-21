package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class Unit extends BaseEntity {
    private BigDecimal conversion;
}
