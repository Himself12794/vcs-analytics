package com.cisco.dft.sdk.vcs.app;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a default value for the parameter.
 * 
 * @author phwhitin
 *
 */
@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {
	String value();
}
