package be.ttime.core.model.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilesEmptyValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilesEmpty {
    String message() default "{be.ttime.validator.FilesEmpty}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
