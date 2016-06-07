package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

public interface IFileRepository extends JpaRepository<FileEntity, Long>, QueryDslPredicateExecutor<FileEntity> {
    FileEntity findByServerName(String serverName);

    List<FileEntity> findByContentDataEntityId(Long id);

    List<FileEntity> findByContentDataEntityIdAndContentTypeName(Long id, String type);
}
