package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;
import be.ttime.core.persistence.model.FieldsetEntity;
import be.ttime.core.persistence.repository.IContentTemplateFieldsetRepository;
import be.ttime.core.persistence.repository.IFieldsetRepository;
import be.ttime.core.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FieldsetServiceImpl implements IFieldsetService {

    @Autowired
    private IContentTemplateFieldsetRepository contentTemplateFieldsetRepository;
    @Autowired
    private IFieldsetRepository fieldsetRepository;

    @Override
    public ContentTemplateFieldsetEntity findContentTemplateFieldset(Long id) {
        return contentTemplateFieldsetRepository.findOne(id);
    }

    @Override
    public List<ContentTemplateFieldsetEntity> saveContentTemplateFieldset(List<ContentTemplateFieldsetEntity> list) {

        for (ContentTemplateFieldsetEntity c : list) {
            saveContentTemplateFieldset(c);
        }

        return list;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "template", allEntries = true),
    })
    public ContentTemplateFieldsetEntity saveContentTemplateFieldset(ContentTemplateFieldsetEntity ctf) {
        return contentTemplateFieldsetRepository.save(ctf);
    }

    @Override
    public List<FieldsetEntity> saveFieldset(List<FieldsetEntity> list) {

        for (FieldsetEntity fieldsetEntity : list) {
            saveFieldset(fieldsetEntity);
        }
        return list;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "template", allEntries = true),
    })
    public FieldsetEntity saveFieldset(FieldsetEntity fieldset) {
        return fieldsetRepository.save(fieldset);
    }

    @Override
    public FieldsetEntity findFieldset(Long id) {
        return fieldsetRepository.findOne(id);
    }

    @Override
    public List<FieldsetEntity> findAllFieldset() {
        return fieldsetRepository.findAll();
    }

    @Override
    //@Cacheable(value = "block", key = "'json'")
    public String jsonFieldset() {

        List<FieldsetEntity> fieldsets = fieldsetRepository.findAll();
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;
        // reload tree like this : table.ajax.reload()
        for (FieldsetEntity f : fieldsets) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", f.getId()));
            row.add("name", CmsUtils.emptyStringIfnull(f.getName()));
            row.add("description", CmsUtils.emptyStringIfnull(f.getDescription()));
            row.add("deletable", f.isDeletable());
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "template", allEntries = true),
    })
    public void deleteFieldset(Long id) throws Exception {
        FieldsetEntity fieldset = fieldsetRepository.findOne(id);
        if (fieldset == null) {
            throw new Exception("Fieldset with id " + id + " is not found!");
        } else if (!fieldset.isDeletable()) {
            String message = "Fieldset with name \" + name + \" is not deletable!";
            log.error(message);
            throw new Exception(message);
        }
        fieldsetRepository.delete(id);
    }

    @Override
    public void deleteContentTemplateFieldset(Long id) {
        contentTemplateFieldsetRepository.delete(id);
    }
}
