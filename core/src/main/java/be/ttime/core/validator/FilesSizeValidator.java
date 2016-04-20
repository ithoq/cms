package be.ttime.core.validator;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class FilesSizeValidator implements ConstraintValidator<FilesSize, MultipartFile[]> {

    private long max;
    private long min;

    @Override
    public void initialize(FilesSize constraintAnnotation) {
        max = constraintAnnotation.max() * 1024 * 1024;
        min = constraintAnnotation.min() * 1024 * 1024;
    }

    @Override
    public boolean isValid(MultipartFile[] values, ConstraintValidatorContext context) {
        for (MultipartFile value : values) {
            if (!value.isEmpty()) {
                if (value.getSize() < min || value.getSize() > max)
                    return false;
            }
        }
        return true;
    }
}
