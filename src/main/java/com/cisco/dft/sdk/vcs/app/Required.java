package com.cisco.dft.sdk.vcs.app;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates an option that is required.
 * 
 * @author phwhitin
 *
 *
 */
@Target(FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {

}
