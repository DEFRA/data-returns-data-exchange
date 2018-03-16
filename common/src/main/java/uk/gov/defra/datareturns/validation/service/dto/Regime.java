package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Data;
import org.springframework.hateoas.Resource;

import java.util.List;

@Data
public class Regime extends BaseEntity {
    private String context;
}
