package be.ttime.core.model.validator;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class FilesUploadedValidator implements ConstraintValidator<FilesUploaded, MultipartFile[]> {

    private long max;
    private long min;

    @Override
    public void initialize(FilesUploaded constraintAnnotation) {
        max = constraintAnnotation.max();
        min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(MultipartFile[] values, ConstraintValidatorContext context) {
        if (values.length <= min)
            return false;
        if (max != 0)
            if (values.length > max)
                return false;
        return true;
    }
}