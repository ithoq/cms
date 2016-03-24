package be.ttime.core.model.validator;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class FilesExtensionValidator implements ConstraintValidator<FilesExtension, MultipartFile[]> {

    public Set<String> allowedExtension = new HashSet<>();

    @Override
    public void initialize(FilesExtension constraintAnnotation) {
        for (int i = 0; i < constraintAnnotation.extension().length; i++) {
            allowedExtension.add(constraintAnnotation.extension()[i]);
        }
    }

    @Override
    public boolean isValid(MultipartFile[] values, ConstraintValidatorContext context) {
        for (MultipartFile value : values) {
            if (!value.isEmpty()) {
                if (!allowedExtension.contains(FilenameUtils.getExtension(value.getOriginalFilename()).toLowerCase()))
                    return false;
            }
        }
        return true;
    }
}