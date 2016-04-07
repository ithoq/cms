package be.ttime.core.persistence.service;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.persistence.model.PageContentEntity;
import be.ttime.core.persistence.model.PageEntity;
import be.ttime.core.persistence.repository.IPageContentRepository;
import be.ttime.core.persistence.repository.IPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class PageServiceImpl implements IPageService {

    private final static int MAX_EXPANDED_TREE_LEVEL = 3; // 0 based
    @Autowired
    private IPageRepository pageRepository;
    @Autowired
    private IPageContentRepository pageContentRepository;

    @Override

    public PageEntity find(Long id) {
        return pageRepository.findOne(id);
    }

    @Override
    public PageEntity findBySlug(String slug) {
        return pageRepository.findBySlug(slug);
    }

    @Override
    public PageEntity save(PageEntity p) {

        if (p.getId() == 0) {
            PageEntity parent = p.getPageParent();

            PageEntity result = pageRepository.findFirstByPageParentOrderByOrderDesc(parent);

            if (result == null) {
                p.setOrder(0);
            } else {
                p.setOrder(result.getOrder() + 1);
            }
        }

        return pageRepository.save(p);
    }

    @Override
    public List<PageEntity> save(List<PageEntity> pages) {
        return pageRepository.save(pages);
    }


    @Override
    public void delete(Long id) throws Exception {
        PageEntity current = pageRepository.findOne(id);

        // Exist
        if (current == null)
            throw new ResourceNotFoundException();
        // No children
        if (current.getPageChildren().size() > 0)
            throw new Exception("Page with id = " + id + " has children !");
        // delete
        pageRepository.delete(id);

        List<PageEntity> pages = pageRepository.findByPageParentOrderByOrderAsc(current.getPageParent());

        if (pages.size() > 0) {
            int counter = 0;
            for (PageEntity p : pages) {
                p.setOrder(counter);
                counter++;
            }
            pageRepository.save(pages);
        }
    }

    @Override
    public List<PageEntity> getNavPages() {
        List<PageEntity> pages = pageRepository.findByMenuItemTrueAndEnabledTrue();
        return getRootPage(pages);
    }

    @Override
    public String getPagesTree() {
        List<PageEntity> pages = pageRepository.findAll(); // force first level cache
        List<PageEntity> roots = pageRepository.findByPageParentIsNullOrderByOrderAsc();
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
    public PageEntity findWithChildren(Long id) {
        PageEntity p = pageRepository.findOne(id);
        p.getPageChildren().size(); // force lazy loading
        return p;
    }

    private List<PageEntity> getRootPage(List<PageEntity> pages) {
        List<PageEntity> result = new ArrayList<>();
        for (PageEntity pageEntity : pages) {
            if (pageEntity.getPageParent() == null)
                result.add(pageEntity);
            else
                break;
        }
        return result;
    }

    private String buildJsonTree(List<PageEntity> pages, StringBuilder sb, boolean first, int level) {

        for (PageEntity p : pages) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            sb.append("{ \"title\": \"").append(p.getName()).append("\", \"key\": \"").append(p.getId()).append("\"");
            List<PageEntity> childrens = p.getPageChildren();
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
    public List<PageContentEntity> saveContents(List<PageContentEntity> contents) {
        return pageContentRepository.save(contents);
    }

    @Override
    public PageContentEntity saveContent(PageContentEntity content) {
        return pageContentRepository.save(content);
    }

    @Override
    public PageContentEntity findContentById(Long id) {
        return pageContentRepository.findOne(id);
    }
}