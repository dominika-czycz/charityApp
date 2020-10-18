package pl.coderslab.charityApp.user.validation.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.coderslab.charityApp.user.resources.UserResource;
import pl.coderslab.charityApp.user.validation.ValidationService;
import pl.coderslab.charityApp.user.validation.constraint.UniqueEmailForUpdate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
@Scope("prototype")
@Slf4j
public class UniqueEmailForUpdateValidator implements ConstraintValidator<UniqueEmailForUpdate, UserResource> {
    private ValidationService validationService;

    @Override
    public void initialize(UniqueEmailForUpdate constraint) {
    }

    @Override
    public boolean isValid(UserResource userResource, ConstraintValidatorContext context) {
        log.debug("Checking if email {} is unique...", userResource.getEmail());
        final boolean valid = validationService.isUniqueEmail(userResource.getEmail(), userResource.getId());
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{UniqueEmail.userResource.email}")
                    .addPropertyNode("email").addConstraintViolation();
        }
        return valid;
    }

    @Autowired
    public void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }
}

