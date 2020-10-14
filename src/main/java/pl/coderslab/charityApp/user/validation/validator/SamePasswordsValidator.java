package pl.coderslab.charityApp.user.validation.validator;

import pl.coderslab.charityApp.user.UserResource;
import pl.coderslab.charityApp.user.validation.constraint.SamePasswords;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SamePasswordsValidator implements ConstraintValidator<SamePasswords, UserResource> {
    public void initialize(SamePasswords constraint) {
    }

    public boolean isValid(UserResource userResource, ConstraintValidatorContext context) {
        if (userResource.getPassword() == null || userResource.getPassword2() == null) {
            return true;
        } else {
            boolean valid = userResource.getPassword().equals(userResource.getPassword2());
            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{samePassword.userResource.password2}")
                        .addPropertyNode("password2").addConstraintViolation();
            }
            return valid;
        }
    }
}
