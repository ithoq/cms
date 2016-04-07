package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.IpAttemptsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IIpAttemptsRepository extends JpaRepository<IpAttemptsEntity, Long>, QueryDslPredicateExecutor<IpAttemptsEntity> {
    IpAttemptsEntity findByIp(String ip);
}
