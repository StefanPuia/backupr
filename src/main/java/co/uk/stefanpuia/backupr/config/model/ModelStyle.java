package co.uk.stefanpuia.backupr.config.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.immutables.value.Value;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Value.Style(
    get = {"is*", "get*"},
    init = "set*")
public @interface ModelStyle {}
