package be.ttime.core.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilesExtensionValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FilesExtension {
    String message() default "{be.ttime.validator.FilesExtension}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] extension() default {"jpg", "jpeg", "png", "gif", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "zip", "rar", "7z"};
}
