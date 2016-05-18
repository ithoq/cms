package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.FieldsetEntity;

import java.util.List;

public interface IContentFieldService {

    FieldsetEntity findFieldset(Long id);

    List<FieldsetEntity> findAll();

    FieldsetEntity saveFieldset(FieldsetEntity fieldset);

    List<FieldsetEntity> saveFieldset(List<FieldsetEntity> fieldset);

}
