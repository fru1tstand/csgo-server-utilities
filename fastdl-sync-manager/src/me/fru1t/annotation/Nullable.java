package me.fru1t.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Qualifier for a reference type in a {@link ElementType#TYPE_USE TYPE_USE} position:
 * The type that has this annotation explicitly includes the value <code>null</code>.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE_USE)
public @interface Nullable { }
