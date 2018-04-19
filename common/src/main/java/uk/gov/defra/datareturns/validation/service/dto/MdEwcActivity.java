package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MdEwcActivity extends MdBaseEntity {
    /**
     * Determines if the EWC activity is hazardous
     */
    private boolean hazardous;
}
