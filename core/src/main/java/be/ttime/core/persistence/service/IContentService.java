package be.ttime.core.persistence.service;

import be.ttime.core.model.PageableResult;
import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.ContentEntity;
import be.ttime.core.persistence.model.ContentTypeEntity;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public interface IContentService {

    ContentEntity saveContent(ContentEntity p);

    List<ContentEntity> saveContent(List<ContentEntity> pages);

    String getNavMenu(String lang, long depth);

    String getPagesTree();

    void deleteContent(Long id) throws Exception;

    void deleteContentData(Long id) throws Exception;

    ContentEntity findContentAdmin(Long id);

    ContentEntity findContentAndContentData(Long id, String locale);

    String getContentJsonByTypeAndLocale(String type, String locale) throws Exception;

    List<ContentDataEntity> saveContentData(List<ContentDataEntity> contents);

    ContentDataEntity saveContentData(ContentDataEntity content);

    ContentEntity findContent(Long id);

    ContentDataEntity findContentData(Long id);

    ContentDataEntity findBySlug(String slug, Locale locale);

    List<ContentTypeEntity> findAllContentType();

    boolean contentTypeExist(String contentType);

    PageableResult<ContentEntity> findWebContent(String locale, Date begin, Date end, String name, String category, String contentType, long pageNumber, Long limit, Long offset);

}
