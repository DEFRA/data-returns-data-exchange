package uk.gov.ea.datareturns.web.filters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.ws.rs.NameBinding;

/**
 * Created by graham on 13/05/16.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface FilenameAuthorization {
}
