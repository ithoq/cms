package be.ttime.core.persistence.repository;


import be.ttime.core.persistence.model.TaxonomyTermEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface ITaxonomyTermRepository extends JpaRepository<TaxonomyTermEntity, Long>, QueryDslPredicateExecutor<TaxonomyTermEntity> {
}
