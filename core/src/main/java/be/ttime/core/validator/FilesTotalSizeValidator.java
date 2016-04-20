package be.ttime.core.validator;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class FilesTotalSizeValidator implements ConstraintValidator<FilesTotalSize, MultipartFile[]> {

    private long max;
    private long min;
    private long total;

    @Override
    public void initialize(FilesTotalSize constraintAnnotation) {
        max = constraintAnnotation.max() * 1024 * 1024;
        min = constraintAnnotation.min() * 1024 * 1024;
    }

    @Override
    public boolean isValid(MultipartFile[] values, ConstraintValidatorContext context) {
        for (MultipartFile value : values) {
            if (!value.isEmpty()) {
                total = +value.getSize();
            }
        }
        return !(total < min || total > max);
    }
}
