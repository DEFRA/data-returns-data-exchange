package uk.gov.ea.datareturns.domain.jpa.dao.impl;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.dao.TextValueDao;
import uk.gov.ea.datareturns.domain.jpa.entities.TextValue;

/**
 * DAO for text values
 *
 * @author Sam Gardner-Dell
 */
@Repository
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TextValueDaoImpl extends AbstractAliasingEntityDao<TextValue> implements TextValueDao {
    public TextValueDaoImpl() {
        super(TextValue.class);
    }
}