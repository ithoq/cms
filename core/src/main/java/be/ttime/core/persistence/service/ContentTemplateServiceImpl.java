package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.ContentTemplateEntity;
import be.ttime.core.persistence.repository.IContentTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentTemplateServiceImpl implements IPageTemplateService {

    @Autowired
    private IContentTemplateRepository contentTemplateRepository;

    @Override
    public List<ContentTemplateEntity> findAll() {
        return contentTemplateRepository.findAll();
    }

    @Override
    public ContentTemplateEntity find(Long id) {
        return contentTemplateRepository.findOne(id);
    }

    @Override
    public ContentTemplateEntity save(ContentTemplateEntity contentTemplate) {
        return contentTemplateRepository.save(contentTemplate);
    }
}