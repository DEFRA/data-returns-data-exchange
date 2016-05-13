package uk.gov.ea.datareturns.resource;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by graham on 13/05/16.
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface FilenameAuthorization {}
