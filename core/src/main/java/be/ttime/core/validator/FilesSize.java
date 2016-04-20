package be.ttime.core.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilesSizeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilesSize {
    String message() default "{be.ttime.validator.FilesSize}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Max size in MB
     */
    long max() default 20; // 20 MB

    /**
     * Max size in MB
     */
    long min() default 0;
}
