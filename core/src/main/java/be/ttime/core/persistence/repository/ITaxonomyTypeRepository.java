package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.TaxonomyTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface ITaxonomyTypeRepository extends JpaRepository<TaxonomyTypeEntity, String>, QueryDslPredicateExecutor<TaxonomyTypeEntity> {

}
