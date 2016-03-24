package be.ttime.core.persistence.service;


import be.ttime.core.persistence.dao.FileEntity;

import java.util.List;

public interface IFileService {

    FileEntity findOne(Long id);

    FileEntity findServerName(String serverName);

    FileEntity save(FileEntity file);

    List<FileEntity> save(List<FileEntity> files);

    void delete(Long id);

    String getFilesListJson(Long id);
}
