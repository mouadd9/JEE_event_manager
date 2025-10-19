package com.example.jee_event_manager.config;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ApplicationScoped
public class ValidatorProducer {
    
    private ValidatorFactory validatorFactory;
    
    @Produces
    public Validator produceValidator() {
        if (validatorFactory == null) {
            validatorFactory = Validation.buildDefaultValidatorFactory();
        }
        return validatorFactory.getValidator();
    }
    @PreDestroy
    public void destroy() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }
}
