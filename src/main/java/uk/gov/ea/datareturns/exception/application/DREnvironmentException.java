package uk.gov.ea.datareturns.exception.application;

import static uk.gov.ea.datareturns.type.ApplicationExceptionType.ENVIRONMENT;

public class DREnvironmentException extends AbstractDRApplicationException
{
	private static final long serialVersionUID = 1L;

	public DREnvironmentException(String message)
	{
		super(ENVIRONMENT.getAppStatusCode(), message);
	}
}