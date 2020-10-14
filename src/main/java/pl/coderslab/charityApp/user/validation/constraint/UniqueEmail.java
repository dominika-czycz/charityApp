package pl.coderslab.charityApp.user.validation.constraint;

import pl.coderslab.charityApp.user.validation.validator.UniqueEmailValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    String message() default "{UniqueEmail.userResource.email}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}