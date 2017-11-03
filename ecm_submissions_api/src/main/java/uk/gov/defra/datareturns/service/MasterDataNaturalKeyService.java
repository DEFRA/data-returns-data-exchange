package uk.gov.defra.datareturns.service;

import org.springframework.stereotype.Service;
import uk.gov.defra.datareturns.util.TextUtils;

public interface MasterDataNaturalKeyService {
    String relaxKey(String entityName, String inputValue);

    @Service
    class MasterDataNaturalKeyServiceImpl implements MasterDataNaturalKeyService {
        public MasterDataNaturalKeyServiceImpl() {
        }

        @Override
        public String relaxKey(final String entityName, final String inputValue) {
            // TODO: Implement this using annotation lookup from entity model
            String relaxed = inputValue;
            if (inputValue != null) {
                if (MasterDataEntity.EA_ID.equals(entityName)) {
                    relaxed = inputValue;
                } else if (MasterDataEntity.METHOD_OR_STANDARD.equals(entityName)) {
                    relaxed = TextUtils.normalize(inputValue.toUpperCase(), TextUtils.WhitespaceHandling.REMOVE);
                } else {
                    relaxed = TextUtils.normalize(inputValue.toUpperCase());
                }
            }
            return relaxed;
        }
    }
}
