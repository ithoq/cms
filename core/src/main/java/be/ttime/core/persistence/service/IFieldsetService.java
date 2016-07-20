package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;
import be.ttime.core.persistence.model.FieldsetEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface IFieldsetService {
    List<ContentTemplateFieldsetEntity> saveContentTemplateFieldset(List<ContentTemplateFieldsetEntity> list);
    ContentTemplateFieldsetEntity saveContentTemplateFieldset(ContentTemplateFieldsetEntity ctf);
    ContentTemplateFieldsetEntity findContentTemplateFieldset(Long id);

    List<FieldsetEntity> saveFieldset(List<FieldsetEntity> list);

    FieldsetEntity findFieldset(Long id);
    String jsonFieldset();

    List<FieldsetEntity> findAllFieldset();

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    void deleteFieldset(Long id) throws Exception;
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    void deleteContentTemplateFieldset(Long id);
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    FieldsetEntity saveFieldset(FieldsetEntity fieldset);

}
