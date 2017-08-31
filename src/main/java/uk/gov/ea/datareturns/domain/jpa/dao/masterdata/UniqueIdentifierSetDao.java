package uk.gov.ea.datareturns.domain.jpa.dao.masterdata;

import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.Operator;
import uk.gov.ea.datareturns.domain.jpa.entities.masterdata.impl.UniqueIdentifierSet;

import java.util.List;

public interface UniqueIdentifierSetDao {

    List<UniqueIdentifierSet> listSetsFor(UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType);

    List<UniqueIdentifierSet> listSetsFor(UniqueIdentifierSet.UniqueIdentifierSetType uniqueIdentifierSetType, Operator operator);
}
