package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MdEwcActivity extends MdBaseEntity {
    /**
     * Determines if the EWC activity is hazardous
     */
    private boolean hazardous;
}
