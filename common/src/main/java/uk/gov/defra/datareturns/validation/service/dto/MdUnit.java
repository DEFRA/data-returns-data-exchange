package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class MdUnit extends MdBaseEntity {
    private BigDecimal conversion;
}
