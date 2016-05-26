package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.ContentTemplateEntity;
import be.ttime.core.persistence.repository.IContentTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<ContentTemplateEntity> findAllByTypeLike(String type) {
        return contentTemplateRepository.findByContentTypeNameLike(type);
    }

    @Override
    public ContentTemplateEntity find(Long id) {
        return contentTemplateRepository.findOne(id);
    }

    @Override
    public ContentTemplateEntity save(ContentTemplateEntity contentTemplate) {
        return contentTemplateRepository.save(contentTemplate);
    }

    @Override
    public ContentTemplateEntity findWithFieldsetAndData(Long id) {
        return contentTemplateRepository.findByIdWithFieldset(id);
    }

    @Override
    public void delete(Long id) throws Exception {
        ContentTemplateEntity contentTemplateEntity = contentTemplateRepository.findOne(id);
        if (contentTemplateEntity == null) {
            throw new Exception("Fieldset with id " + id + " is not found!");
        } else if (!contentTemplateEntity.isDeletable()) {
            String message = "Fieldset with name \" + name + \" is not deletable!";
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
        // reload tree like this : table.ajax.reload()
        for (ContentTemplateEntity c : contentTemplateEntityList) {
            row = Json.createObjectBuilder();
            //row.add("DT_RowId", "x"); // add an name
            //row.add("DT_RowClass", "x"); // add a class
            row.add("DT_RowData", Json.createObjectBuilder().add("id", c.getId()));
            row.add("name", c.getName());
            row.add("description", c.getDescription() != null ? c.getDescription() : "");
            row.add("deletable", c.isDeletable());
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }
}