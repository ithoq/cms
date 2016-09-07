package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.TaxonomyTermEntity;
import be.ttime.core.persistence.model.TaxonomyTypeEntity;

import java.util.List;

public interface ITaxonomyService {

    void deleteTerm(String term, String type);
    TaxonomyTermEntity findByType(String term, String type);
    TaxonomyTermEntity createIfNotExist(String term, String type);
    TaxonomyTermEntity add(String term, String type);
    List<TaxonomyTermEntity> createIfNotExist(List<String> terms, String type);
    TaxonomyTypeEntity createTypeIfNotExist(String type);
    void deleteType(String type);
    List<TaxonomyTermEntity> findByType(String type);
    String findByTypeJson(String type);
    List<TaxonomyTermEntity> findAll();
}
