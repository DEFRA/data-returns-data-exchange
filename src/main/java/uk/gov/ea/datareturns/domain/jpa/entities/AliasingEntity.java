package uk.gov.ea.datareturns.domain.jpa.entities;

import java.util.Set;

/**
 * Created by graham on 16/08/16.
 */
public interface AliasingEntity extends ControlledListEntity {
    String getPreferred();

    void setPreferred(String preferred);

    Set<String> getAliases();

    void setAliases(Set<String> aliases);

    /**
     * Retrieve the primary name.  This is always the value which should be output to the downstream system.
     * If {@link AliasingEntity#getPreferred()} is available then this value is used, otherwise returns {@link ControlledListEntity#getName()}
     *
     * @return the primary name to be sent to the downstream system
     */
    default String getPrimaryName() {
        final String preferred = getPreferred();
        return preferred != null ? preferred : getName();
    }
}
