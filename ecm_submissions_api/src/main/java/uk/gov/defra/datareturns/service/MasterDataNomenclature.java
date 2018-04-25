package uk.gov.defra.datareturns.service;

import uk.gov.defra.datareturns.util.TextUtils;
import uk.gov.defra.datareturns.validation.service.MasterDataEntity;
import uk.gov.defra.datareturns.validation.service.MasterDataLookupService;
import uk.gov.defra.datareturns.validation.service.dto.MdAliasableEntity;
import uk.gov.defra.datareturns.validation.service.dto.MdBaseEntity;

import java.util.List;
import java.util.function.BiConsumer;

public final class MasterDataNomenclature {

    private MasterDataNomenclature() {
    }

    public static String relaxNomenclature(final MasterDataEntity entity, final String inputValue) {
        // TODO: Implement this using annotation lookup from entity model
        String relaxed = inputValue;
        if (inputValue != null) {
            if (MasterDataEntity.UNIQUE_IDENTIFIER.equals(entity)
                    || MasterDataEntity.UNIT.equals(entity)) {
                relaxed = TextUtils.normalize(inputValue, TextUtils.WhitespaceHandling.REMOVE);
            } else if (MasterDataEntity.METHOD_OR_STANDARD.equals(entity)) {
                relaxed = TextUtils.normalize(inputValue.toUpperCase(), TextUtils.WhitespaceHandling.REMOVE);
            } else {
                relaxed = TextUtils.normalize(inputValue.toUpperCase());
            }
        }
        return relaxed;
    }

    public static boolean matches(final MasterDataEntity entity, final String expected, final String actual) {
        return relaxNomenclature(entity, expected).equals(relaxNomenclature(entity, actual));
    }

    public static <T extends MdBaseEntity> T resolveMasterDataEntity(final MasterDataLookupService lookupService, final MasterDataEntity entity,
                                                                     final Class<T> cls,
                                                                     final String csvValue) {
        return resolveMasterDataEntity(lookupService, entity, cls, csvValue, null);
    }

    public static <T extends MdBaseEntity> T resolveMasterDataEntity(final MasterDataLookupService lookupService, final MasterDataEntity entity,
                                                                     final Class<T> cls,
                                                                     final String csvValue, final BiConsumer<String, String> subHandler) {
        final List<T> allowedValues = lookupService.list(cls, entity.getCollectionLink()).orThrow();

        for (final T value : allowedValues) {
            if (value.getNomenclature().equalsIgnoreCase(csvValue)) {
                return value;
            }

            if (value instanceof MdAliasableEntity) {
                final MdAliasableEntity<?> aliasable = (MdAliasableEntity<?>) value;
                for (final MdBaseEntity alias : aliasable.getAliases()) {
                    if (alias.getNomenclature().equalsIgnoreCase(csvValue)) {
                        if (subHandler != null) {
                            subHandler.accept(alias.getNomenclature(), value.getNomenclature());
                        }

                        return value;
                    }
                }
            }
        }
        return null;
    }
}
