package uk.gov.ea.datareturns.domain.result;

import uk.gov.ea.datareturns.domain.exceptions.ApplicationExceptionType;

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