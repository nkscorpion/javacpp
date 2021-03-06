package com.googlecode.javacpp.annotation;

import com.googlecode.javacpp.Generator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Encloses the scope of a Java class inside the scope of the given C++ namespace.
 * The namespace also gets nested when applied to nested Java classes, but the
 * annotation accepts as well a string of nested namespaces with the help of the
 * usual "::" separator, while one that starts with "::" resets the namespace to
 * the global scope.
 * <p>
 * This annotation can also be applied in a more Java-like fashion on each method.
 * Further, a namespace annotation with an empty value can be used to indicate
 * that the identifier does not support namespaces (such as macros).
 *
 * @see Generator
 *
 * @author Samuel Audet
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Namespace {
    String value() default "";
}