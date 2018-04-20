package uk.gov.defra.datareturns.validation.service;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientResponseException;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The {@link OptionalResult} provides a fluent api for handling non-server error responses.
 * <p>
 * Note:
 * <p>
 * 4xx errors may be ignored or thrown, see orElse and orThrow methods
 * 5xx errors are always thrown
 *
 * @param <T>
 * @author Sam Gardner-Dell
 */
public interface OptionalResult<T> {
    /**
     * Create an {@link OptionalResult} which uses the given resultExtractor function to extract the required value from the response returned
     * by requestExecutor
     *
     * @param requestExecutor the request executor
     * @param resultExtractor the result extractor
     * @param <R>             the type for the body of the {@link ResponseEntity}
     * @param <T>             the target type which will be extracted from the body of the {@link ResponseEntity}
     * @return a new {@link OptionalResult}
     */
    static <R, T> OptionalResult<T> of(final Supplier<ResponseEntity<R>> requestExecutor, final Function<ResponseEntity<R>, T> resultExtractor) {
        return new OptionalResultImpl<>(requestExecutor, resultExtractor);
    }


    /**
     * Create an {@link OptionalResult} which executes the specified requestExecutor
     *
     * @param requestExecutor the request executor
     * @param <T>             the target type which will be extracted from the body of the {@link ResponseEntity}
     * @return a new {@link OptionalResult}
     */
    static <T> OptionalResult<T> of(final Supplier<ResponseEntity<T>> requestExecutor) {
        return new OptionalResultImpl<>(requestExecutor, ResponseEntity::getBody);
    }

    /**
     * @return true if the entity was successully deserialized from the response
     */
    boolean isPresent();

    /**
     * Return the requested entity, returning {@code defaultValue} if the requested entity could not be found
     *
     * @param defaultValue the default if the requested entity could not be found
     * @return the requested entity if it could be successully deserialized from the response otherwise returns the value of the parameter
     * defaultValue
     */
    T orElse(T defaultValue);

    /**
     * Return the requested entity, throwing the recorded {@link RestClientResponseException} if the entity could not be returned
     *
     * @return the requested entity if one could be found
     * @throws RestClientResponseException if the requested entity could not be found
     */
    T orThrow() throws RestClientResponseException;

    /**
     * Default implementation for {@link OptionalResult}
     *
     * @param <T>
     */
    @Getter
    class OptionalResultImpl<T> implements OptionalResult<T> {
        private ResponseEntity<?> responseEntity;
        private T value;
        private RestClientResponseException exception;

        /**
         * Create a new {@link OptionalResultImpl}
         *
         * @param requestExecutor the request executor
         * @param resultExtractor the result extractor
         * @param <R>             the type for the body of the {@link ResponseEntity} returned by the {@code requestExecutor}
         */
        private <R> OptionalResultImpl(final Supplier<ResponseEntity<R>> requestExecutor, final Function<ResponseEntity<R>, T> resultExtractor) {
            if (this.responseEntity == null && this.exception == null) {
                try {
                    final ResponseEntity<R> response = requestExecutor.get();
                    this.responseEntity = response;
                    this.value = resultExtractor.apply(response);
                } catch (final RestClientResponseException e) {
                    exception = e;
                    final HttpStatus status = HttpStatus.valueOf(e.getRawStatusCode());
                    if (status.is5xxServerError()) {
                        throw e;
                    }
                }
            }
        }

        @Override
        public boolean isPresent() {
            return responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful();
        }

        @Override
        public T orElse(final T defaultValue) {
            if (isPresent()) {
                return value;
            }
            return defaultValue;
        }

        @Override
        public T orThrow() throws RestClientResponseException {
            if (isPresent()) {
                return value;
            }
            throw exception;
        }
    }
}
