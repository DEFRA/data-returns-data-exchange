package uk.gov.ea.datareturns.domain.jpa.dao.masterdata.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.masterdata.TextValueDao;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.TextValue;

import javax.inject.Inject;

/**
 * DAO for text values
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TextValueDaoImpl extends AbstractAliasingEntityDao<TextValue> implements TextValueDao {
    @Inject
    public TextValueDaoImpl() {
        super(TextValue.class);
    }
}