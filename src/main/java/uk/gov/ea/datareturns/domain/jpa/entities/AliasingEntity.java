package uk.gov.ea.datareturns.domain.jpa.entities;

import java.util.Set;

/**
 * Created by graham on 16/08/16.
 */
public interface AliasingEntity extends PersistedEntity {
    String getPreferred();
    void setPreferred(String preferred);
    Set<String> getAliases();
    void setAliases(Set<String> aliases);
}
