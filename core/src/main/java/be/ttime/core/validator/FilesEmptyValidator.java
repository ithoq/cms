package be.ttime.core.validator;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FilesEmptyValidator implements ConstraintValidator<FilesEmpty, MultipartFile[]> {

    @Override
    public void initialize(FilesEmpty constraintAnnotation) {
    }

    @Override
    public boolean isValid(MultipartFile[] values, ConstraintValidatorContext context) {
        for (MultipartFile item : values) {
            if (item.isEmpty())
                return false;
        }
        return true;
    }
}
