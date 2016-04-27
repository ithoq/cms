package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface ICategoryRepository  extends JpaRepository<CategoryEntity, String>, QueryDslPredicateExecutor<CategoryEntity> {
}
