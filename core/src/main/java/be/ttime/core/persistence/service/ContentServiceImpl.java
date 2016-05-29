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
import java.util.*;

@Service
@Transactional
public class ContentServiceImpl implements IContentService {

    private final static int MAX_EXPANDED_TREE_LEVEL = 3; // 0 based
    @Autowired
    private IContentRepository pageRepository;
    @Autowired
    private IContentDataRepository contentRepository;

    @Autowired
    private EntityManager entityManager;

    @Override

    public ContentEntity find(Long id) {
        return pageRepository.findOne(id);
    }

    @Override
    public ContentDataEntity findBySlug(String slug, Locale locale) {


        QContentEntity contentEntity = QContentEntity.contentEntity;
        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        QCommentEntity commentEntity = QCommentEntity.commentEntity;
        QFileEntity fileEntity = QFileEntity.fileEntity;
        QContentDataDictionaryEntity contentDataDictionaryEntity = QContentDataDictionaryEntity.contentDataDictionaryEntity;

        JPAQuery query = new JPAQuery(entityManager);
        JPAQuery query2 = query.clone(entityManager);

        ContentDataEntity resultData =  query.from(contentDataEntity)
                .leftJoin(contentDataEntity.commentList, commentEntity).fetch()
                .leftJoin(commentEntity.commentParent, commentEntity).fetch()
                .leftJoin(contentDataEntity.dictionaryList, contentDataDictionaryEntity).fetch()
                .leftJoin(contentDataEntity.contentFiles, fileEntity).fetch()
                .where(contentDataEntity.computedSlug.eq(slug).and(contentDataEntity.language.locale.eq(locale.toString())))
                .singleResult(contentDataEntity);

        ContentEntity result = query2.from(contentEntity)
                        .where(contentEntity.id.eq(resultData.getContent().getId()))
                        .leftJoin(contentEntity.privileges).fetch()
                        .leftJoin(contentEntity.contentParent)
                        .singleResult(contentEntity);

        return resultData;
    }

    @Override
    public ContentEntity findContentParent(Long id) {
        JPAQuery query = new JPAQuery(entityManager);
        QContentEntity contentEntity = QContentEntity.contentEntity;
        query = new JPAQuery(entityManager);
        ContentEntity result =
                query
                        .from(contentEntity)
                        .where(contentEntity.id.eq(id))
                        .leftJoin(contentEntity.privileges).fetch()
                        .leftJoin(contentEntity.contentParent)
                        .singleResult(contentEntity);

        return result;
    }

    @Override
    public ContentEntity savePage(ContentEntity p) {

        if (p.getId() == 0) {
            ContentEntity parent = p.getContentParent();

            ContentEntity result = pageRepository.findFirstByContentParentOrderByOrderDesc(parent);

            if (result == null) {
                p.setOrder(0);
            } else {
                p.setOrder(result.getOrder() + 1);
            }
        }

        return pageRepository.save(p);
    }

    @Override
    public List<ContentEntity> savePage(List<ContentEntity> pages) {
        return pageRepository.save(pages);
    }


    @Override
    public void delete(Long id) throws Exception {
        ContentEntity current = pageRepository.findOne(id);

        // Exist
        if (current == null)
            throw new ResourceNotFoundException();
        // No children
        if (current.getContentChildren().size() > 0)
            throw new Exception("Page with id = " + id + " has children !");
        // delete
        pageRepository.delete(id);

        List<ContentEntity> pages = pageRepository.findByContentParentOrderByOrderAsc(current.getContentParent());

        if (pages.size() > 0) {
            int counter = 0;
            for (ContentEntity p : pages) {
                p.setOrder(counter);
                counter++;
            }
            pageRepository.save(pages);
        }
    }

    @Override
    public List<ContentEntity> getNavPages() {
        List<ContentEntity> pages = pageRepository.findByMenuItemTrueAndEnabledTrue();
        return getRootPage(pages);
    }

    @Override
    public String getPagesTree() {
        List<ContentEntity> pages = pageRepository.findAll(); // force first level cache
        List<ContentEntity> roots = pageRepository.findByContentParentIsNullOrderByOrderAsc();
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
    public ContentEntity findWithChildren(Long id) {
        ContentEntity p = pageRepository.findOne(id);
        p.getContentChildren().size(); // force lazy loading
        return p;
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

    @Override
    public List<ContentDataEntity> saveContents(List<ContentDataEntity> contents) {
        return contentRepository.save(contents);
    }

    @Override
    public ContentDataEntity saveContent(ContentDataEntity content) {
        return contentRepository.save(content);
    }

    @Override
    public ContentDataEntity findContentById(Long id) {
        return contentRepository.findOne(id);
    }
}
