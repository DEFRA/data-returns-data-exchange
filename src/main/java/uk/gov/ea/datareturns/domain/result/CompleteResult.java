package uk.gov.ea.datareturns.domain.result;

/**
 * Response structure for calls to the REST completion method
 *
 * @author Sam Gardner-Dell
 */
public class CompleteResult {
	private String fileKey;

	private String userEmail;

	/**
	 * Default zero-arg constructor (for serialization support)
	 */
	@SuppressWarnings("unused")
	public CompleteResult() {}

	/**
	 * Create a new CompleteResult instance for the given filekey and user email
	 *
	 * @param fileKey the file key that the completion request was issued for
	 * @param userEmail the user email that initiated the completion request
	 */
	public CompleteResult(final String fileKey, final String userEmail) {
		this.fileKey = fileKey;
		this.userEmail = userEmail;
	}

	/**
	 * @return the file key that the completion request was issued for
	 */
	public String getFileKey() {
		return this.fileKey;
	}

	/**
	 * Set the file key that the completion request was issued for
	 * @param fileKey the file key that the completion request was issued for
	 */
	public void setFileKey(final String fileKey) {
		this.fileKey = fileKey;
	}

	/**
	 * @return the user email that initiated the completion request
	 */
	public String getUserEmail() {
		return this.userEmail;
	}

	/**
	 * Set the user email that initiated the completion request
	 * @param userEmail the user email that initiated the completion request
	 */
	public void setUserEmail(final String userEmail) {
		this.userEmail = userEmail;
	}
}
