package be.ttime.core.persistence.service;


import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;
import be.ttime.core.persistence.repository.IContentTemplateFieldsetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentTemplateFieldsetServiceImpl implements IContentTemplateFieldsetService {

    @Autowired
    private IContentTemplateFieldsetRepository contentTemplateFieldsetRepository;

    @Override
    public ContentTemplateFieldsetEntity find(Long id) {
        return contentTemplateFieldsetRepository.findOne(id);
    }

    @Override
    public List<ContentTemplateFieldsetEntity> save(List<ContentTemplateFieldsetEntity> list) {
        return contentTemplateFieldsetRepository.save(list);
    }

    @Override
    public ContentTemplateFieldsetEntity save(ContentTemplateFieldsetEntity ctf) {
        return contentTemplateFieldsetRepository.save(ctf);
    }
}
