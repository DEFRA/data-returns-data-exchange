package uk.gov.ea.datareturns.domain.result;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidationErrors {
	@JsonProperty("validationErrors")
	private final List<ValidationError> errors = new ArrayList<>();

	public void addError(final ValidationError error) {
		this.errors.add(error);
	}

	@JsonIgnore
	public boolean isValid() {
		return this.errors.isEmpty();
	}
}
