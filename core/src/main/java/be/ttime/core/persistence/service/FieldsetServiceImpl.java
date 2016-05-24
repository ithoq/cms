package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;
import be.ttime.core.persistence.model.FieldsetEntity;
import be.ttime.core.persistence.repository.IContentTemplateFieldsetRepository;
import be.ttime.core.persistence.repository.IFieldsetRepository;
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
        return contentTemplateFieldsetRepository.save(list);
    }

    @Override
    public ContentTemplateFieldsetEntity saveContentTemplateFieldset(ContentTemplateFieldsetEntity ctf) {
        return contentTemplateFieldsetRepository.save(ctf);
    }

    @Override
    public List<FieldsetEntity> saveFieldset(List<FieldsetEntity> list) {
        return fieldsetRepository.save(list);
    }

    @Override
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
            //row.add("DT_RowId", "x"); // add an name
            //row.add("DT_RowClass", "x"); // add a class
            row.add("DT_RowData", Json.createObjectBuilder().add("id", f.getId()));
            row.add("name", f.getName());
            row.add("description", f.getDescription());
            row.add("deletable", f.isDeletable());
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }

    @Override
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
