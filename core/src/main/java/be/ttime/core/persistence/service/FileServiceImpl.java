package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.FileEntity;
import be.ttime.core.persistence.repository.IFileRepository;
import be.ttime.core.util.CmsUtils;
import be.ttime.core.util.FileExtensionUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.List;

@Service
@Transactional
public class FileServiceImpl implements IFileService {

    @Autowired
    private IFileRepository pageFileRepository;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),
            @CacheEvict(value = "adminContent", allEntries = true),

    })
    public FileEntity save(FileEntity file) {
        return pageFileRepository.save(file);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),
            @CacheEvict(value = "adminContent", allEntries = true),

    })
    public List<FileEntity> save(List<FileEntity> files) {
        return pageFileRepository.save(files);
    }

    @Override
    public FileEntity findOne(Long id) {
        return pageFileRepository.findOne(id);
    }

    @Override
    public FileEntity findServerName(String serverName) {
        return pageFileRepository.findByServerName(serverName);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),
            @CacheEvict(value = "adminContent", allEntries = true),

    })
    public void delete(Long id) {
        pageFileRepository.delete(id);
    }

    @Override
    public String getFilesListJson(Long contentDataId, String type) {
        List<FileEntity> files = null;
        if(type == null){
            files = pageFileRepository.findByContentDataEntityId(contentDataId);
        } else{
            files = pageFileRepository.findByContentDataEntityIdAndFileType(contentDataId, type);
        }

        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;

        for (FileEntity file : files) {
            row = Json.createObjectBuilder();
            //row.add("DT_RowId", "x"); // add an name
            //row.add("DT_RowClass", "x"); // add a class
            row.add("DT_RowData", Json.createObjectBuilder().add("id", file.getId()));
            row.add("name", CmsUtils.emptyStringIfnull(file.getName()));
            row.add("description", CmsUtils.emptyStringIfnull(file.getDescription()));
            row.add("group", CmsUtils.emptyStringIfnull(file.getFileGroup()));
            row.add("active", file.isEnabled());
            row.add("type", CmsUtils.emptyStringIfnull(FileExtensionUtils.getFileImage(file.getExtension())));
            row.add("size", CmsUtils.emptyStringIfnull(FileUtils.byteCountToDisplaySize(file.getSize())));
            data.add(row);
        }

        String result = Json.createObjectBuilder().add("data", data).build().toString();
        return result;
    }
}
