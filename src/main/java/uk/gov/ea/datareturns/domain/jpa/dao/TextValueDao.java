package uk.gov.ea.datareturns.domain.jpa.dao;

import org.springframework.stereotype.Repository;
import uk.gov.ea.datareturns.domain.jpa.entities.TextValue;

import java.util.regex.Pattern;

/**
 * DAO for return periods
 *
 * @author Sam Gardner-Dell
 */
@Repository
public class TextValueDao extends AliasingEntityDao<TextValue> {
    public TextValueDao() {
        super(TextValue.class);
    }

    Pattern removeSpaces = Pattern.compile("\\s");

    // Allow for no spaces in the method or standard
    public String getKeyFromRelaxedName(String name) {
        return name == null ? null : removeSpaces.matcher(name.toUpperCase().trim()).replaceAll("");
    }
}