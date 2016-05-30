package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.ContentEntity;

import java.util.List;

public interface IContentRepositoryCustom {

    ContentDataEntity findContentData(String slug, String locale, List<String> fetch);

    ContentEntity findContent(Long id, List<String> festch);
}
