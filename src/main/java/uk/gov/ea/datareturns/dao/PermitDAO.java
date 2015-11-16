package uk.gov.ea.datareturns.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;

public interface PermitDAO
{
	@SqlQuery("SELECT permit_number FROM permit WHERE permit_number = :permitNumber")
	String findByPermitNumber(@Bind("permitNumber") String permitNumber);
	
	void close();
}