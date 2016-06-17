package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.TaxonomyTermEntity;
import be.ttime.core.persistence.model.TaxonomyTypeEntity;
import be.ttime.core.persistence.repository.ITaxonomyTermDataRepository;
import be.ttime.core.persistence.repository.ITaxonomyTermRepository;
import be.ttime.core.persistence.repository.ITaxonomyTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TaxonomyServiceImpl implements ITaxonomyService {

    @Autowired
    private ITaxonomyTermDataRepository taxonomyTermDataRepository;
    @Autowired
    private ITaxonomyTypeRepository taxonomyTypeRepository;
    @Autowired
    private ITaxonomyTermRepository taxonomyTermRepository;

    @Override
    public void deleteTerm(String term, String type) {
        TaxonomyTermEntity termEntity = taxonomyTermRepository.findByNameAndTaxonomyTypeName(term.toLowerCase(), type);
        if(termEntity == null){
            taxonomyTermRepository.delete(termEntity);
        }
    }

    @Override
    public TaxonomyTermEntity createIfNotExist(String term, String type) {
        TaxonomyTermEntity termEntity = taxonomyTermRepository.findByNameAndTaxonomyTypeName(term.toLowerCase(), type);
        if(termEntity == null){
            termEntity = add(term, type);
        }
        return termEntity;
    }

    @Override
    public List<TaxonomyTermEntity> createIfNotExist(List<String> terms, String type) {

        List<TaxonomyTermEntity> result = new ArrayList<>();
        for (String term : terms) {
            result.add(createIfNotExist(term, type));
        }

        return result;
    }

    @Override
    public TaxonomyTypeEntity createTypeIfNotExist(String type) {

        TaxonomyTypeEntity typeEntity = taxonomyTypeRepository.findOne(type);
        if(typeEntity == null){
            taxonomyTypeRepository.save(typeEntity);
        }
        return typeEntity;

    }

    @Override
    public void deleteType(String type) {
            taxonomyTypeRepository.delete(type);
    }

    @Override
    public List<TaxonomyTermEntity> findAll() {
        return taxonomyTermRepository.findAll();
    }

    @Override
    public String findByTypeJson(String type) {
        List<TaxonomyTermEntity> terms = taxonomyTermRepository.findAllByTaxonomyTypeName(type);
        List<String> result = new ArrayList<>();
        JsonArrayBuilder data = Json.createArrayBuilder();
        for (TaxonomyTermEntity term : terms) {
            // result.add(term.getName());
            data.add(term.getName());
        }

        return data.build().toString();

      //  return result;
    }

    private TaxonomyTermEntity add(String term, String type){
        TaxonomyTermEntity termEntity = new TaxonomyTermEntity();
        termEntity.setName(term.toLowerCase());
        TaxonomyTypeEntity typeEntity = createTypeIfNotExist(type);
        termEntity.setTaxonomyType(typeEntity);
        return taxonomyTermRepository.save(termEntity);
    }
}
