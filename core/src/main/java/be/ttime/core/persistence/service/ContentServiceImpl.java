package be.ttime.core.persistence.service;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.persistence.model.*;
import be.ttime.core.persistence.repository.IContentDataRepository;
import be.ttime.core.persistence.repository.IContentRepository;
import com.mysema.query.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.*;

@Service
@Transactional
public class ContentServiceImpl implements IContentService {

    private final static int MAX_EXPANDED_TREE_LEVEL = 3; // 0 based
    @Autowired
    private IContentRepository contentRepository;
    @Autowired
    private IContentDataRepository contentDataRepository;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override

    public ContentEntity find(Long id) {
        return contentRepository.findOne(id);
    }

    @Override
    public ContentDataEntity findBySlug(String slug, Locale locale) {

        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        QContentEntity contentEntity = QContentEntity.contentEntity;
        QTaxonomyTermEntity taxonomyTermDataEntity = QTaxonomyTermEntity.taxonomyTermEntity;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        JPAQuery query = new JPAQuery(entityManager);
        ContentDataEntity result = query.from(contentDataEntity)
             .leftJoin(contentDataEntity.dictionaryList).fetch()
             .leftJoin(contentDataEntity.commentList).fetch()
             .leftJoin(contentDataEntity.contentFiles).fetch()
             .where(contentDataEntity.computedSlug.eq(slug).and(contentDataEntity.language.locale.eq(locale.toString())))
             .singleResult(contentDataEntity);
        entityManager.clear();
        if(result != null) {
            query = new JPAQuery(entityManager);
            ContentEntity parent = query.from(contentEntity)
                                        .leftJoin(contentEntity.dataList, contentDataEntity).fetch()
                                        .where(contentEntity.id.eq(result.getContent().getId()).and(contentDataEntity.language.locale.eq(locale.toString())))
                                        .leftJoin(contentEntity.contentParent).fetch()
                                        .leftJoin(contentEntity.contentTemplate).fetch()
                                        //.leftJoin(contentEntity.dictionaryList).fetch()
                                        .leftJoin(contentEntity.privileges).fetch()
                                        //.leftJoin(contentEntity.taxonomyTermEntities, taxonomyTermDataEntity).fetch()
                                        //.leftJoin(taxonomyTermDataEntity.termDataList).fetch()
                                        .singleResult(contentEntity);
            // we add parent to the first result
            result.setContent(parent);
            entityManager.close();

        }
        return result;
        //return contentDataRepository.findByComputedSlugAndLanguageLocale(slug,locale.toString());
    }

    @Override
    public ContentEntity savePage(ContentEntity p) {

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
    public List<ContentEntity> savePage(List<ContentEntity> pages) {
        return contentRepository.save(pages);
    }


    @Override
    public void delete(Long id) throws Exception {
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
    public String getNavMenu(String lang) {
        QContentEntity contentEntity = QContentEntity.contentEntity;
        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityManager entityManager2 = entityManagerFactory.createEntityManager();
        JPAQuery query = new JPAQuery(entityManager);
        List<ContentEntity> result =
                query.from(contentEntity)
                     .leftJoin(contentEntity.dataList, contentDataEntity).fetch()
                     .leftJoin(contentEntity.privileges).fetch()
                      .where(contentEntity.enabled.eq(true)
                              .and(contentEntity.menuItem.eq(true))
                              .and(contentDataEntity.language.locale.eq(lang))
                              .and(contentDataEntity.computedSlug.isNotNull()))
                     .list(contentEntity);

        query = new JPAQuery(entityManager2);
        List<ContentEntity> roots =
                query.from(contentEntity)
                        .where(contentEntity.enabled.eq(true)
                                .and(contentEntity.menuItem.eq(true))
                                .and(contentEntity.contentParent.isNull()))
                        .orderBy(contentEntity.order.asc())
                        .list(contentEntity);

        StringBuilder sb = new StringBuilder();


        sb.append("<ul class='main-menu' id='main-menu'>");
        buildNavMenu(roots, sb);
        sb.append("</ul>");
        entityManager.close();
        entityManager2.close();
        return sb.toString();
    }

    @Override
    public String getPagesTree() {
        List<ContentEntity> pages = contentRepository.findAll(); // force first level cache
        List<ContentEntity> roots = contentRepository.findByContentParentIsNullOrderByOrderAsc();
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        buildJsonTree(roots, sb, true, 0);
        sb.append("]");
        return sb.toString();
    }


    /**
     * Get a page ( admin CMS ) with his children.
     * Used to prevent delete a page.
     */
    @Override
    public ContentEntity findContentAdmin(Long id) {
        QContentEntity contentEntity = QContentEntity.contentEntity;
        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        QTaxonomyTermEntity taxonomyTermDataEntity = QTaxonomyTermEntity.taxonomyTermEntity;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        JPAQuery query = new JPAQuery(entityManager);
        ContentEntity result = query.from(contentEntity)
                .leftJoin(contentEntity.dataList, contentDataEntity).fetch()
                .where(contentEntity.id.eq(id))
                .leftJoin(contentEntity.contentParent).fetch()
                .leftJoin(contentEntity.contentTemplate).fetch()
                //.leftJoin(contentEntity.dictionaryList).fetch()
                .leftJoin(contentEntity.privileges).fetch()
                .leftJoin(contentEntity.taxonomyTermEntities, taxonomyTermDataEntity).fetch()
                .leftJoin(taxonomyTermDataEntity.termDataList).fetch()
                .singleResult(contentEntity);
        entityManager.close();
        return result;
    }

    @Override
    public ContentEntity findContentData(Long id, String locale) {
        QContentEntity contentEntity = QContentEntity.contentEntity;
        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        JPAQuery query = new JPAQuery(entityManager);
        ContentEntity result = query.from(contentEntity)
                .leftJoin(contentEntity.dataList, contentDataEntity).fetch()
                .where(contentEntity.id.eq(id).and(contentDataEntity.language.locale.eq(locale)))
                .singleResult(contentEntity);
        entityManager.close();
        return result;
    }

    private List<ContentEntity> getRootPage(List<ContentEntity> pages) {
        List<ContentEntity> result = new ArrayList<>();
        for (ContentEntity contentEntity : pages) {
            if (contentEntity.getContentParent() == null)
                result.add(contentEntity);
            else
                break;
        }
        return result;
    }

    @Override
    public List<ContentDataEntity> saveContents(List<ContentDataEntity> contents) {
        return contentDataRepository.save(contents);
    }

    @Override
    public ContentDataEntity saveContent(ContentDataEntity content) {
        return contentDataRepository.save(content);
    }

    @Override
    public ContentDataEntity findContentById(Long id) {
        return contentDataRepository.findOne(id);
    }

    private String buildNavMenu(List<ContentEntity> pages, StringBuilder sb) {

        for (ContentEntity p : pages) {
            sb.append("<li>");
            if(!p.getDataList().isEmpty()) {
                ContentDataEntity data = p.getDataList().iterator().next();
                sb.append("<a href='");
                sb.append(data.getComputedSlug());
                sb.append("'>");
                sb.append(data.getTitle());
                sb.append("</a>");
                Set<ContentEntity> childrensSet = p.getContentChildren();
                List<ContentEntity> childrens = new ArrayList<>();
                childrens.addAll(childrensSet);
                if (childrens.size() > 0) {
                    // This is bad :(
                    Collections.sort(childrens, (p1, p2) -> Integer.compare(p1.getOrder(), p2.getOrder()));
                    sb.append("<ul class='main-menu-children'>");
                    buildNavMenu(childrens, sb);
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
