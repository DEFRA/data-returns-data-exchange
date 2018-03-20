package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Unit extends BaseEntity {
    private BigDecimal conversion;
}
