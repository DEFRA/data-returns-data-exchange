package uk.gov.defra.datareturns.validation.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class MdAliasableEntity<T extends MdBaseEntity> extends MdBaseEntity {
    private List<T> aliases;

    private BigDecimal conversion;
}
