package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.FileEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface IFileService {

    FileEntity findOne(Long id);

    FileEntity findServerName(String serverName);

    @PreAuthorize("hasRole('ROLE_ADMIN_FILE')")
    FileEntity save(FileEntity file);

    @PreAuthorize("hasRole('ROLE_ADMIN_FILE')")
    List<FileEntity> save(List<FileEntity> files);

    @PreAuthorize("hasRole('ROLE_ADMIN_FILE_DELETE')")
    void delete(Long id);

    String getFilesListJson(Long contentDataId, String type);
}
