package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;

import java.util.List;

public interface IContentTemplateFieldsetService {
    List<ContentTemplateFieldsetEntity> save(List<ContentTemplateFieldsetEntity> list);
    ContentTemplateFieldsetEntity save(ContentTemplateFieldsetEntity ctf);
}
