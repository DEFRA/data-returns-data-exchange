package uk.gov.ea.datareturns.domain.jpa.service;

import org.springframework.stereotype.Service;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.MasterDataEntity;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.*;
import uk.gov.ea.datareturns.util.TextUtils;

public interface MasterDataNaturalKeyService {

    String relaxKey(MasterDataEntity entity);

    String relaxKey(Class<? extends MasterDataEntity> entityClass, String inputValue);

    @Service class MasterDataNaturalKeyServiceImpl implements MasterDataNaturalKeyService {
        public MasterDataNaturalKeyServiceImpl() {
        }

        @Override public String relaxKey(Class<? extends MasterDataEntity> entityClass, String inputValue) {
            // TODO: Implement this using annotation lookup from entity model
            String relaxed = inputValue;
            if (inputValue != null) {
                if (UniqueIdentifier.class.equals(entityClass)) {
                    relaxed = inputValue;
                } else if (Site.class.equals(entityClass) || Unit.class.equals(entityClass)) {
                    relaxed = TextUtils.normalize(inputValue, TextUtils.WhitespaceHandling.REMOVE);
                } else if (MethodOrStandard.class.equals(entityClass)) {
                    relaxed = TextUtils.normalize(inputValue.toUpperCase(), TextUtils.WhitespaceHandling.REMOVE);
                } else {
                    relaxed = TextUtils.normalize(inputValue.toUpperCase());
                }
            }
            return relaxed;
        }

        @Override public String relaxKey(MasterDataEntity entity) {
            return relaxKey(entity.getClass(), entity.getName());
        }
    }
}
