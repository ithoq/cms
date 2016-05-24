package be.ttime.core.persistence.repository;


import be.ttime.core.persistence.model.TaxonomyTermDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface ITaxonomyTermDataRepository extends JpaRepository<TaxonomyTermDataEntity, Long>, QueryDslPredicateExecutor<TaxonomyTermDataEntity> {
}
