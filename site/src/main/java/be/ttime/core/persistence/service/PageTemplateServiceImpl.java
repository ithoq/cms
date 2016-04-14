package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.PageTemplateEntity;
import be.ttime.core.persistence.repository.IPageTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PageTemplateServiceImpl implements IPageTemplateService {

    @Autowired
    private IPageTemplateRepository pageTemplateRepository;

    @Override
    public List<PageTemplateEntity> findAll() {
        return pageTemplateRepository.findAll();
    }

    @Override
    public PageTemplateEntity find(Long id) {
        return pageTemplateRepository.findOne(id);
    }

    @Override
    public PageTemplateEntity save(PageTemplateEntity pageTemplate) {
        return pageTemplateRepository.save(pageTemplate);
    }
}