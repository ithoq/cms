package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ContentDataEntity;

import java.util.List;

public interface IContentRepositoryCustom {

    ContentDataEntity findContent(String slug, String locale, List<String> fetch);
}
