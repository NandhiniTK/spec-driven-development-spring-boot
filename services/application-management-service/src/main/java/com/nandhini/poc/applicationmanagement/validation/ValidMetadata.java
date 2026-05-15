package com.nandhini.poc.applicationmanagement.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MetadataValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMetadata {

    String message() default "Metadata must not exceed 20 key-value pairs";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
