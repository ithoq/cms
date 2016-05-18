package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.FieldsetEntity;
import be.ttime.core.persistence.repository.IFieldsetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ContentFieldServiceImpl implements IContentFieldService {

    @Autowired
    private IFieldsetRepository fieldsetRepository;

    @Override
    public FieldsetEntity findFieldset(Long id) {
        return fieldsetRepository.findOne(id);
    }

    @Override
    public FieldsetEntity saveFieldset(FieldsetEntity fieldset) {
        return fieldsetRepository.save(fieldset);
    }

    @Override
    public List<FieldsetEntity> saveFieldset(List<FieldsetEntity> fieldset) {
        return fieldsetRepository.save(fieldset);
    }

    @Override
    public List<FieldsetEntity> findAll() {
        return fieldsetRepository.findAll();
    }
}
