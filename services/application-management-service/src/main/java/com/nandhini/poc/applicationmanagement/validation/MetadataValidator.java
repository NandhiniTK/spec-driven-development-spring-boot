package com.nandhini.poc.applicationmanagement.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Map;

public class MetadataValidator implements ConstraintValidator<ValidMetadata, Map<String, String>> {

    private static final int MAX_ENTRIES = 20;

    @Override
    public boolean isValid(Map<String, String> metadata, ConstraintValidatorContext context) {
        if (metadata == null) {
            return true;
        }
        return metadata.size() <= MAX_ENTRIES;
    }
}
