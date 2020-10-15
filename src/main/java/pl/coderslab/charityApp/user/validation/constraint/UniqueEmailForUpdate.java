package pl.coderslab.charityApp.user.validation.constraint;

import pl.coderslab.charityApp.user.validation.validator.UniqueEmailForUpdateValidator;
import pl.coderslab.charityApp.user.validation.validator.UniqueEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueEmailForUpdateValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmailForUpdate {
    String message() default "{UniqueEmail.userResource.email}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}