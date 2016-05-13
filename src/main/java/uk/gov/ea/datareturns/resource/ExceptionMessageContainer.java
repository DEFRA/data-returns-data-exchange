package uk.gov.ea.datareturns.resource;

import uk.gov.ea.datareturns.type.ApplicationExceptionType;

public final class ExceptionMessageContainer {
	private final int appStatusCode;

	private final String message;

	public ExceptionMessageContainer(final ApplicationExceptionType exceptionType, final String message) {
		this.appStatusCode = exceptionType.getAppStatusCode();
		this.message = message;
	}

	public int getAppStatusCode() {
		return this.appStatusCode;
	}

	public String getMessage() {
		return this.message;
	}
}