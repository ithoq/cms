package be.ttime.core.persistence.service;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.ContentEntity;
import be.ttime.core.persistence.repository.IContentDataRepository;
import be.ttime.core.persistence.repository.IContentRepository;
import be.ttime.core.persistence.repository.IContentRepositoryCustom;
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
    private IContentRepository contentRepository;
    @Autowired
    private IContentRepositoryCustom contentRepositoryCustom;
    @Autowired
    private IContentDataRepository contentDataRepository;

    @Autowired
    private EntityManager entityManager;

    @Override

    public ContentEntity find(Long id) {
        return contentRepository.findOne(id);
    }

    @Override
    public ContentDataEntity findBySlug(String slug, Locale locale) {


        ContentDataEntity result =  contentRepositoryCustom.findContentData(slug,locale.toString(),null);
        if(result != null) {
            ContentEntity parent = contentRepositoryCustom.findContent(result.getContent().getId(), null);
            // we add parent to the first result
            result.setContent(parent);
        }
        return result;
        //return contentDataRepository.findByComputedSlugAndLanguageLocale(slug,locale.toString());
    }

    @Override
    public ContentEntity findContentWithParent(Long id) {
        return contentRepositoryCustom.findContent(id, null);
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
    public List<ContentEntity> getNavPages() {
        List<ContentEntity> pages = contentRepository.findByMenuItemTrueAndEnabledTrue();
        return getRootPage(pages);
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
    public ContentEntity findWithChildren(Long id) {
        ContentEntity p = contentRepository.findOne(id);
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
}
