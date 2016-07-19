package be.ttime.core.persistence.service;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.PageableResult;
import be.ttime.core.persistence.model.*;
import be.ttime.core.persistence.repository.IContentDataRepository;
import be.ttime.core.persistence.repository.IContentRepository;
import be.ttime.core.persistence.repository.IContentTypeRepository;
import be.ttime.core.util.CmsUtils;
import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

//import javax.persistence.EntityManagerFactory;

@Service
@Transactional
public class ContentServiceImpl implements IContentService {

    private final static int MAX_EXPANDED_TREE_LEVEL = 3; // 0 based
    @Autowired
    private IContentRepository contentRepository;
    @Autowired
    private IContentDataRepository contentDataRepository;
    @Autowired
    private IContentTypeRepository contentTypeRepository;
//    @Autowired
//    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext(unitName = "core")
    private EntityManager entityManager;

    @Override
    @Cacheable(value = "content", key = "#slug")
    /**
     * Public page / when user request a page
     */
    public ContentDataEntity findBySlug(String slug, Locale locale) {

        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        QContentEntity contentEntity = QContentEntity.contentEntity;
//        QTaxonomyTermEntity taxonomyTermDataEntity = QTaxonomyTermEntity.taxonomyTermEntity;
        JPAQuery query = new JPAQuery(entityManager);
        ContentDataEntity result = query.from(contentDataEntity)
                .leftJoin(contentDataEntity.commentList).fetch()
                .leftJoin(contentDataEntity.contentFiles).fetch()
                .where(contentDataEntity.computedSlug.eq(slug)
                        .and(contentDataEntity.enabled.eq(true))
                        .and(contentDataEntity.language.locale.eq(locale.toString())))
                .singleResult(contentDataEntity);
//        entityManager.clear();

        if (result != null) {
            query = new JPAQuery(entityManager);
            ContentEntity parent = query.from(contentEntity)
                    .leftJoin(contentEntity.contentDataList, contentDataEntity).fetch()
                    .where(contentEntity.id.eq(result.getContent().getId()).and(contentDataEntity.language.locale.eq(locale.toString())))
                    .leftJoin(contentEntity.contentParent).fetch()
                            //.leftJoin(contentEntity.contentTemplate).fetch()
                            //.leftJoin(contentEntity.dictionaryList).fetch()
                    .leftJoin(contentEntity.privileges).fetch()
                            //.leftJoin(contentEntity.taxonomyTermEntities, taxonomyTermDataEntity).fetch()
                    .leftJoin(contentEntity.taxonomyTermEntities).fetch()
                    .singleResult(contentEntity);
            // we add parent to the first result
            result.setContent(parent);
//            entityManager.close();
        }
        return result;
    }

    @Override
    /*
        Find without fetch
     */
    public ContentEntity findContent(Long id) {
        return contentRepository.findOne(id);
    }

    @Override
        /*
        Find without fetch
     */
    public ContentDataEntity findContentData(Long id) {
        return contentDataRepository.findOne(id);
    }

    /**
     * Get a page ( admin CMS ) with his children.
     * Used to prevent delete a page.
     */
    @Override
    @Cacheable(value = "adminContent", key = "#id")
    public ContentEntity findContentAdmin(Long id) {
        QContentEntity contentEntity = QContentEntity.contentEntity;
        QContentEntity parentEntity = QContentEntity.contentEntity;
        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        QTaxonomyTermEntity taxonomyTermDataEntity = QTaxonomyTermEntity.taxonomyTermEntity;
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
        JPAQuery query = new JPAQuery(entityManager);
        ContentEntity result = query.from(contentEntity)
                .leftJoin(contentEntity.contentDataList, contentDataEntity).fetch()
                .where(contentEntity.id.eq(id))
                .leftJoin(contentEntity.contentParent).fetch()
                .leftJoin(contentEntity.contentTemplate).fetch()
                        //.leftJoin(contentEntity.dictionaryList).fetch()
                .leftJoin(contentEntity.privileges).fetch()
                .leftJoin(contentEntity.taxonomyTermEntities, taxonomyTermDataEntity).fetch()
                .leftJoin(taxonomyTermDataEntity.termDataList).fetch()
                .singleResult(contentEntity);
//        entityManager.close();

        if(result.getContentParent() != null){
            Hibernate.initialize(result.getContentParent().getContentDataList());
        }
        return result;
    }

    @Override
    public PageableResult<ContentEntity> findWebContent(String locale, Date begin, Date end, String name, String category, List<String> contentType, Long pageNumber, Long limit, Long offset) {
        QContentEntity contentEntity = QContentEntity.contentEntity;
        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        QTaxonomyTermEntity taxonomyTermEntity = QTaxonomyTermEntity.taxonomyTermEntity;
        JPAQuery query = new JPAQuery(entityManager);

        BooleanBuilder builder = new BooleanBuilder();
        if(!StringUtils.isEmpty(name)){
            builder.and(contentDataEntity.title.like("%" + name + "%"));
        }
        if(begin != null){
            builder.and(contentEntity.beginDate.between(begin,end));
        }

        builder.and(contentEntity.contentType.name.in(contentType));

        if(!StringUtils.isEmpty(category)){
            builder.and(taxonomyTermEntity.taxonomyType.name.eq("CATEGORY").and(taxonomyTermEntity.name.eq(category)));
        }

        PageableResult<ContentEntity> pageableResult = new PageableResult<>();

        List<ContentEntity> result = query.from(contentEntity)
                                    .leftJoin(contentEntity.contentDataList, contentDataEntity).fetch()
                                    .where(contentDataEntity.language.locale.eq(locale))
                                    .leftJoin(contentEntity.taxonomyTermEntities, taxonomyTermEntity).fetch()
                                    .where(contentEntity.endDate.isNull().or(contentEntity.endDate.before(new Date())))
                                    .where(contentEntity.enabled.eq(true).and(contentDataEntity.enabled.eq(true)).and(builder))
                                    .orderBy(contentEntity.beginDate.desc())
                                    .limit(limit).offset((pageNumber-1) * offset).list(contentEntity);
        pageableResult.setResult(result);
        pageableResult.setTotalResult(query.count());
        pageableResult.setCurrentPage(pageNumber);
        pageableResult.setTotalPage((long)Math.ceil((pageableResult.getTotalPage() * 1.0 )/ limit));
        return pageableResult;
    }

    @Override
    /**
     * Find a content and a content Data
     */
    public ContentEntity findContentAndContentData(Long id, String locale) {
        QContentEntity contentEntity = QContentEntity.contentEntity;
        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
        JPAQuery query = new JPAQuery(entityManager);
        ContentEntity result = query.from(contentEntity)
                .leftJoin(contentEntity.contentDataList, contentDataEntity).fetch()
                .where(contentEntity.id.eq(id).and(contentDataEntity.language.locale.eq(locale)))
                .singleResult(contentEntity);
//        entityManager.close();
        return result;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),
            @CacheEvict(value = "adminTree", allEntries = true),
            @CacheEvict(value = "mainNav", allEntries = true),
            @CacheEvict(value = "adminContent", key = "#p.id"),

    })
    public ContentEntity saveContent(ContentEntity p) {

        if (p.getId() == 0) {
            ContentEntity parent = p.getContentParent();

            ContentEntity result = contentRepository.findFirstByContentParentOrderByOrderDesc(parent);

            if (result == null) {
                p.setOrder(0);
            } else {
                p.setOrder(result.getOrder() + 1);
            }
        }

        return contentRepository.save(p);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),
            @CacheEvict(value = "adminTree", allEntries = true),
            @CacheEvict(value = "mainNav", allEntries = true),
            @CacheEvict(value = "adminContent", allEntries = true),

    })
    public List<ContentEntity> saveContent(List<ContentEntity> pages) {

        for (ContentEntity page : pages) {
            saveContent(page);
        }
        return pages;
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),
            @CacheEvict(value = "adminTree", allEntries = true),
            @CacheEvict(value = "adminContent", key = "#id"),
            @CacheEvict(value = "mainNav", allEntries = true),
    })
    public void deleteContent(Long id) throws Exception {
        ContentEntity current = contentRepository.findOne(id);

        // Exist
        if (current == null)
            throw new ResourceNotFoundException();
        // No children
        if (current.getContentChildren().size() > 0)
            throw new Exception("Page with id = " + id + " has children !");
        // delete
        contentRepository.delete(id);

        List<ContentEntity> pages = contentRepository.findByContentParentOrderByOrderAsc(current.getContentParent());

        if (pages.size() > 0) {
            int counter = 0;
            for (ContentEntity p : pages) {
                p.setOrder(counter);
                counter++;
            }
            contentRepository.save(pages);
        }
    }

    @Override
    public void deleteContentData(Long id) throws Exception {
        contentDataRepository.delete(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),
            @CacheEvict(value = "adminTree", allEntries = true),
            @CacheEvict(value = "mainNav", allEntries = true),
    })
    public List<ContentDataEntity> saveContentData(List<ContentDataEntity> contents) {
        return contentDataRepository.save(contents);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),
            @CacheEvict(value = "adminTree", allEntries = true),
            @CacheEvict(value = "mainNav", allEntries = true),
    })
    public ContentDataEntity saveContentData(ContentDataEntity content) {
        return contentDataRepository.save(content);
    }

    @Override
    @Cacheable(value = "mainNav")
    public String getNavMenu(String lang, long depth) {
        QContentEntity contentEntity = QContentEntity.contentEntity;
        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        JPAQuery query = new JPAQuery(entityManager);
        // force first level cache
        List<ContentEntity> pages =  query.from(contentEntity)
                //.leftJoin(contentEntity.taxonomyTermEntities).fetch()  - i think bad for performance
                .leftJoin(contentEntity.contentDataList, contentDataEntity).fetch()
                .where(contentEntity.enabled.eq(true)
                        .and(contentEntity.menuItem.eq(true))
                        .and(contentEntity.contentType.name.like("PAGE%"))
                        .and(contentDataEntity.language.locale.eq(lang))
                )
                .orderBy(contentEntity.order.asc())
                .list(contentEntity);

        query = new JPAQuery(entityManager);

        List<ContentEntity> roots =
                query.from(contentEntity)
                        .leftJoin(contentEntity.contentDataList, contentDataEntity)
                        .where(contentEntity.enabled.eq(true)
                                .and(contentEntity.menuItem.eq(true))
                                .and(contentEntity.contentParent.isNull())
                                .and(contentEntity.contentType.name.like("PAGE%"))
                                .and(contentDataEntity.language.locale.eq(lang))
                        )
                        .orderBy(contentEntity.order.asc())
                        .list(contentEntity);

        StringBuilder sb = new StringBuilder();


        sb.append("<ul class='main-menu' id='main-menu'>");
        buildNavMenu(roots, sb, lang, depth);
        sb.append("</ul>");
//        entityManager.close();
//        entityManager2.close();
        return sb.toString();
    }

    @Override
    public String getContentJsonByTypeAndLocale(String type, String locale) throws Exception {
        List<ContentEntity> contentEntities = null;
        if(type == null || locale == null){
            throw new Exception("type and locale must not be null");
        }
        //if(locale.equals("all")){
        //    contentEntities =  contentRepository.findAllByContentTypeName(type);
        //} else{
            contentEntities = contentRepository.findAllByContentTypeNameAndContentDataListLanguageLocale(type, locale);
        //}
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;
        // reload tree like this : table.ajax.reload()
        for (ContentEntity c : contentEntities) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", c.getId()));
            row.add("active", c.isEnabled());
            row.add("title", CmsUtils.emptyStringIfnull(c.getContentDataList().get(locale).getTitle()));
            row.add("category", "TO DO");
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }

    @Override
    @Cacheable(value = "adminTree")
    public String getPagesTree() {
        List<ContentEntity> pages = contentRepository.findAllByContentTypeName("PAGE%"); // force first level cache
        List<ContentEntity> roots = contentRepository.findByContentTypeNameLikeAndContentParentIsNullOrderByOrderAsc("PAGE%");
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        buildJsonTree(roots, sb, true, 0);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public List<ContentTypeEntity> findAllContentType() {
        return contentTypeRepository.findAll();
    }

    @Override
    public boolean contentTypeExist(String contentType) {
        return contentTypeRepository.exists(contentType);
    }

    private String buildNavMenu(List<ContentEntity> pages, StringBuilder sb, String locale, long depth) {

        for (ContentEntity p : pages) {

            sb.append("<li>");
            if (!p.getContentDataList().isEmpty()) {
                ContentDataEntity data = p.getContentDataList().get(locale);
                if(data == null) break;
                sb.append("<a href='");
                sb.append(data.getComputedSlug());
                sb.append("'>");
                sb.append(data.getTitle());
                sb.append("</a>");
                Set<ContentEntity> childrensSet = p.getContentChildren();
                List<ContentEntity> childrens = new ArrayList<>();
                childrens.addAll(childrensSet);
                if (childrens.size() > 0 && depth != 0) {
                    // This is bad :(
                    Collections.sort(childrens, (p1, p2) -> Integer.compare(p1.getOrder(), p2.getOrder()));
                    sb.append("<ul class='main-menu-children'>");
                    buildNavMenu(childrens, sb, locale, depth - 1);
                    sb.append("</ul>");
                }
                sb.append("</li>");
            }
        }

        return sb.toString();
    }

    private String buildJsonTree(List<ContentEntity> pages, StringBuilder sb, boolean first, int level) {

        for (ContentEntity p : pages) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            sb.append("{ \"title\": \"").append(p.getName()).append("\", \"key\": \"").append(p.getId()).append("\"");
            Set<ContentEntity> childrensSet = p.getContentChildren();
            List<ContentEntity> childrens = new ArrayList<>();
            childrens.addAll(childrensSet);
            if (childrens.size() > 0) {
                if (level <= MAX_EXPANDED_TREE_LEVEL) {
                    sb.append(", \"expanded\":true");
                }
                sb.append(", \"folder\":true");
                // This is bad :(
                Collections.sort(childrens, (p1, p2) -> Integer.compare(p1.getOrder(), p2.getOrder()));

                sb.append(", \"children\" : [");
                buildJsonTree(childrens, sb, true, level + 1);
                sb.append("]");
            }
            sb.append("}");
        }

        return sb.toString();
    }
}
