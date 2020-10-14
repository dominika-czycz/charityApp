package pl.coderslab.charityApp.user.validation.constraint;

import pl.coderslab.charityApp.user.validation.validator.SamePasswordsValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SamePasswordsValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SamePasswords {
    String message() default "{samePassword.userResource.password2}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
