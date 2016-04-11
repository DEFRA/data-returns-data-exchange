package uk.gov.ea.datareturns.exception;

public final class ExceptionMessageContainer {
	private final int appStatusCode;
	private final String message;

	public ExceptionMessageContainer(final int appStatusCode, final String message) {
		this.appStatusCode = appStatusCode;
		this.message = message;
	}

	public int getAppStatusCode() {
		return this.appStatusCode;
	}

	public String getMessage() {
		return this.message;
	}
}