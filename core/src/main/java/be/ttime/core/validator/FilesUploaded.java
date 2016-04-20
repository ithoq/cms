package be.ttime.core.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilesUploadedValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilesUploaded {
    String message() default "{be.ttime.validator.FilesUploaded}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Max files
     */
    long max() default 0;

    /**
     * Min files
     */
    long min() default 0;
}
