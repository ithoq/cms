package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.IpAttemptsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.Date;

public interface IIpAttemptsRespository extends JpaRepository<IpAttemptsEntity, Long>, QueryDslPredicateExecutor<IpAttemptsEntity> {

    Long countByIpAndDateAfter(String ip, Date date);

    IpAttemptsEntity findByIp(String ip);
}
