package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.ContentTemplateEntity;
import be.ttime.core.persistence.repository.IContentTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ContentTemplateServiceImpl implements IContentTemplateService {

    @Autowired
    private IContentTemplateRepository contentTemplateRepository;

    @Override
    @Cacheable(value = "templateList", key = "#type")
    public List<ContentTemplateEntity> findAllByTypeLike(String type) {
        return contentTemplateRepository.findByContentTypeNameLike(type);
    }

    @Override
    public ContentTemplateEntity findByName(String name) {
        return contentTemplateRepository.findFirstByName(name);
    }

    @Override
    @Cacheable(value = "template", key = "#id")
    public ContentTemplateEntity find(Long id) {
        return contentTemplateRepository.findByIdWithFieldset(id);
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "template", key = "#contentTemplate.id"),
            @CacheEvict(value = "templateList", allEntries = true),
    })
    public ContentTemplateEntity save(ContentTemplateEntity contentTemplate) {
        return contentTemplateRepository.save(contentTemplate);
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "template", key = "#id"),
            @CacheEvict(value = "templateList", allEntries = true),
    })
    public void delete(Long id) throws Exception {
        ContentTemplateEntity contentTemplateEntity = contentTemplateRepository.findOne(id);
        if (contentTemplateEntity == null) {
            throw new Exception("Content Template with id " + id + " is not found!");
        } else if (!contentTemplateEntity.isDeletable()) {
            String message = "Content Template with name \" + name + \" is not deletable!";
            log.error(message);
            throw new Exception(message);
        }
        contentTemplateRepository.delete(id);
    }

    @Override
    //@Cacheable(value = "block", key = "'json'")
    public String jsonContent() {

        List<ContentTemplateEntity> contentTemplateEntityList = contentTemplateRepository.findAll();
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;

        for (ContentTemplateEntity c : contentTemplateEntityList) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", c.getId()));
            row.add("name", c.getName());
            row.add("description", c.getDescription() != null ? c.getDescription() : "");
            row.add("deletable", c.isDeletable());
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }
}