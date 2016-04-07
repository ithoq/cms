package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ApplicationConfigEntity;
import be.ttime.core.persistence.model.ApplicationLanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;


public interface IApplicationLanguageRepository extends JpaRepository<ApplicationLanguageEntity, String>, QueryDslPredicateExecutor<ApplicationLanguageEntity> {

    List<ApplicationLanguageEntity> findByEnabledForAdminTrue();
    List<ApplicationLanguageEntity> findByEnabledForPublicTrue();
    List<ApplicationLanguageEntity> findByEnabledForPublicTrueOrEnabledForAdminTrue();
}