package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Regime extends BaseEntity {
    private String context;
}
