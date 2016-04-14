package be.ttime.core.model.form;

import be.ttime.core.model.validator.FilesEmpty;
import be.ttime.core.model.validator.FilesExtension;
import be.ttime.core.model.validator.FilesSize;
import be.ttime.core.util.FileTypeDetector;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Getter
@Setter
public class AdminFileUploadForm {

    @FilesEmpty
    @FilesExtension
    @FilesSize(max = 1)
    private MultipartFile[] files;
    private String[] mimeTypes;
    private Long pageId;

    public void checkMimtypes(FileTypeDetector fileTypeDetector, String name) {
        if (files.length > 0) {
            mimeTypes = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                String type = null;
                try {
                    type = fileTypeDetector.probeContentType(files[i].getBytes(), name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mimeTypes[i] = type != null ? type : "application/octet-stream";
            }
        }
    }
}