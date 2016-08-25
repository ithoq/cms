package be.ttime.core.model.form;

import be.ttime.core.validator.FilesEmpty;
import be.ttime.core.validator.FilesExtension;
import be.ttime.core.validator.FilesSize;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class AdminFileUploadForm {

    @FilesEmpty
    @FilesExtension
    @FilesSize(max = 50)
    private MultipartFile[] files;
    private String[] mimeTypes;
    private Long pageId;
}