package pl.coderslab.charityApp.user.validation.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.coderslab.charityApp.user.validation.ValidationService;
import pl.coderslab.charityApp.user.validation.constraint.UniqueEmail;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@Scope("prototype")
@Slf4j
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private ValidationService validationService;

    public void initialize(UniqueEmail constraint) {
    }

    public boolean isValid(String value, ConstraintValidatorContext context) {
        log.debug("Checking if email {} is unique...", value);
        return validationService.isUniqueEmail(value);
    }

    @Autowired
    public void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }
}

