package uk.gov.defra.datareturns.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.dto.MdUnit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class ValueStandardisationService {
    private final UnitConversionFactorCache conversionFactorCache;

    public BigDecimal getStandardValue(final BigDecimal value, final String unitId) {
        if (value == null) {
            return null;
        }
        final BigDecimal factor = conversionFactorCache.getConversionFactorCache().get(unitId);
        return value.multiply(factor);
    }


    @Service
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    @Slf4j
    @RequiredArgsConstructor
    private static class UnitConversionFactorCache {
        private final MasterDataLookupService lookupService;

        @Cacheable(cacheNames = "ConversionCache", key = "'Units'", unless = "#result.isEmpty()")
        public Map<String, BigDecimal> getConversionFactorCache() {
            final List<MdUnit> unitList = lookupService.list(MdUnit.class, new Link("units")).orThrow();
            return unitList.stream().collect(Collectors.toMap(MasterDataLookupService::getResourceId, MdUnit::getConversion));
        }
    }
}
