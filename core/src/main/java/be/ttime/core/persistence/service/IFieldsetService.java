package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;
import be.ttime.core.persistence.model.FieldsetEntity;

import java.util.List;

public interface IFieldsetService {
    List<ContentTemplateFieldsetEntity> saveContentTemplateFieldset(List<ContentTemplateFieldsetEntity> list);
    ContentTemplateFieldsetEntity saveContentTemplateFieldset(ContentTemplateFieldsetEntity ctf);
    ContentTemplateFieldsetEntity findContentTemplateFieldset(Long id);

    List<FieldsetEntity> saveFieldset(List<FieldsetEntity> list);
    FieldsetEntity saveFieldset(FieldsetEntity fieldset);
    FieldsetEntity findFieldset(Long id);
    String jsonFieldset();

    List<FieldsetEntity> findAllFieldset();

    void deleteFieldset(Long id) throws Exception;

    void deleteContentTemplateFieldset(Long id);

}
