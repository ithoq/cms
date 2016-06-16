package be.ttime.core.persistence.repository;


import be.ttime.core.persistence.model.TaxonomyTermEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

public interface ITaxonomyTermRepository extends JpaRepository<TaxonomyTermEntity, Long>, QueryDslPredicateExecutor<TaxonomyTermEntity> {
    TaxonomyTermEntity findByNameAndTaxonomyTypeName(String name, String type);
    List<TaxonomyTermEntity> findAllByTaxonomyTypeName(String type);
}
