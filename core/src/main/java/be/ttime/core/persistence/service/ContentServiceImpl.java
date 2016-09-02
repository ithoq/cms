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
import com.mysema.query.types.expr.BooleanExpression;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.SimpleDateFormat;
import java.util.*;

//import javax.persistence.EntityManagerFactory;

@Service(value = "contentService")
@Transactional
public class ContentServiceImpl implements IContentService {

    private final static int MAX_EXPANDED_TREE_LEVEL = 3; // 0 based
    @Autowired
    private IContentRepository contentRepository;
    @Autowired
    private IContentDataRepository contentDataRepository;
    @Autowired
    private IContentTypeRepository contentTypeRepository;
    @Autowired
    private ApplicationContext applicationContext;
    private IContentService contentService;
    @Autowired
    private IApplicationService applicationService;
//    @Autowired
//    private EntityManagerFactory entityManagerFactory;

    @PersistenceContext(unitName = "core")
    private EntityManager entityManager;


    /**
     * Public page / when user request a page
     */
    @Override
    public ContentEntity findBySlug(String slug, Locale locale) {

        Long contentId = applicationContext.getBean(IContentService.class).getContentDataIdBySlug(slug, locale);
        if(contentId == null)
            return null;

        return applicationContext.getBean(IContentService.class).findContentAdmin(contentId);

        /*

        return

        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        QContentEntity contentEntity = QContentEntity.contentEntity;
//        QTaxonomyTermEntity taxonomyTermDataEntity = QTaxonomyTermEntity.taxonomyTermEntity;
        JPAQuery query = new JPAQuery(entityManager);
        ContentDataEntity result = query.from(contentDataEntity)
                .leftJoin(contentDataEntity.commentList).fetch()
                .leftJoin(contentDataEntity.contentFiles).fetch()
                .where(contentDataEntity.computedSlug.eq(slug)
                        .and(contentDataEntity.content.enabled.eq(true))
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
                    .leftJoin(contentEntity.roles).fetch()
                            //.leftJoin(contentEntity.taxonomyTermEntities, taxonomyTermDataEntity).fetch()
                    .leftJoin(contentEntity.taxonomyTermEntities).fetch()
                    .singleResult(contentEntity);
            // we add parent to the first result
            result.setContent(parent);
//            entityManager.close();
        }
        return result;
        */
    }


    @Cacheable(value = "content", key = "#slug + '-' + #locale.toString()")
    public Long getContentDataIdBySlug(String slug, Locale locale){
        ContentDataEntity result = contentDataRepository.findByComputedSlugAndLanguageLocale(slug, locale.toString());
        return result == null ? null : result.getContent().getId();
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
        QContentEntity contentChildren = QContentEntity.contentEntity;
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
                .leftJoin(contentEntity.roles).fetch()
                .leftJoin(contentEntity.taxonomyTermEntities, taxonomyTermDataEntity).fetch()
                .leftJoin(taxonomyTermDataEntity.termDataList).fetch()
                .orderBy(contentEntity.position.asc(), contentEntity.contentChildren.any().position.asc())
                .singleResult(contentEntity);
//        entityManager.close(); inte

        if(result.getContentParent() != null){
            Hibernate.initialize(result.getContentParent().getContentDataList());
        }
        if(result.getContentChildren() != null){
            Hibernate.initialize(result.getContentChildren());
        }

        result.getContentDataList().forEach((k,v) ->{
          Hibernate.initialize(v.getContentFiles());
        });

        return result;
    }

    @Override
    public List<ContentEntity> findByContentParentOrderByOrderAsc(ContentEntity parent) {
        return contentRepository.findByContentParentOrderByPositionAsc(parent);
    }

    @Override
    public PageableResult<ContentEntity> findWebContent(String locale, Date begin, Date end, String name, String type, String category, String tags, String contentType, Long pageNumber, Long limit) {
        QContentEntity contentEntity = QContentEntity.contentEntity;
        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        QTaxonomyTermEntity taxonomyTermEntity = QTaxonomyTermEntity.taxonomyTermEntity;
        JPAQuery query = new JPAQuery(entityManager);


        BooleanBuilder builder = new BooleanBuilder();
        if(!StringUtils.isEmpty(name)){
            builder.and(contentDataEntity.title.like("%" + name + "%"));
        }
        if(begin != null){
            if(end == null) end = new Date();
            builder.and(contentEntity.beginDate.between(begin,end));
        }
        if(!StringUtils.isEmpty(contentType)) {
            builder.and(contentEntity.contentType.name.in(contentType.split(",")));
        }

        /*
        if(!StringUtils.isEmpty(category)){
            builder.and(taxonomyTermEntity.taxonomyType.name.eq("CATEGORY").and(taxonomyTermEntity.name.eq(category)));
        }*/

        BooleanExpression categoryBool = null;
        BooleanExpression typeBool = null;
        BooleanExpression tagBool = null;
        BooleanExpression taxonomieBool = null;

        if(!StringUtils.isEmpty(category)){
            categoryBool = taxonomyTermEntity.taxonomyType.name.eq("CATEGORY").and(taxonomyTermEntity.name.in(category.split(",")));
        }
        if(!StringUtils.isEmpty(type)){
            typeBool = taxonomyTermEntity.taxonomyType.name.eq("TYPE").and(taxonomyTermEntity.name.in(type.split(",")));
        }
        if(!StringUtils.isEmpty(tags)){
            tagBool = taxonomyTermEntity.taxonomyType.name.eq("TAG").and(taxonomyTermEntity.name.in(tags.split(",")));
        }

        if(categoryBool != null) {
            taxonomieBool = categoryBool.or(tagBool).or(typeBool);
        } else if(tagBool != null){
            taxonomieBool = tagBool.or(typeBool);
        } else if(typeBool != null){
            taxonomieBool = typeBool;
        }

        builder.and(taxonomieBool);

        PageableResult<ContentEntity> pageableResult = new PageableResult<>();

        List<ContentEntity> result = query.from(contentEntity)
                                    .leftJoin(contentEntity.contentDataList, contentDataEntity).fetch()
                                    .where(contentDataEntity.language.locale.eq(locale))
                                    .leftJoin(contentEntity.taxonomyTermEntities, taxonomyTermEntity).fetch()
                                    .where(contentEntity.endDate.isNull().or(contentEntity.endDate.before(new Date())))
                                    .where(contentEntity.enabled.eq(true).and(contentDataEntity.enabled.eq(true)).and(builder))
                                    .orderBy(contentEntity.beginDate.desc())
                                    .limit(limit).offset((pageNumber-1) * limit).distinct().list(contentEntity);

        pageableResult.setResult(result);
        pageableResult.setTotalResult(query.count());
        pageableResult.setCurrentPage(pageNumber);
        pageableResult.setTotalPage(((long)Math.ceil((pageableResult.getTotalResult() * 1.0 )/ limit)));
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
            //@CacheEvict(value = "adminContent", key = "#p.id"),
            @CacheEvict(value = "adminContent", allEntries = true),

    })
    public ContentEntity saveContent(ContentEntity p) {

        if (p.getId() == 0) {
            ContentEntity parent = p.getContentParent();

            ContentEntity result = contentRepository.findFirstByContentParentOrderByPositionDesc(parent);

            if (result == null) {
                p.setPosition(0);
            } else {
                p.setPosition(result.getPosition() + 1);
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
            @CacheEvict(value = "adminContent", allEntries = true),
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
        ContentEntity parent = current.getContentParent();

        contentRepository.delete(id);

        List<ContentEntity> pages = contentRepository.findByContentParentOrderByPositionAsc(parent);
        if (pages != null && pages.size() > 0) {
            int counter = 0;
            for (ContentEntity p : pages) {
                p.setPosition(counter);
                counter++;
            }
            contentRepository.save(pages);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),
            @CacheEvict(value = "adminTree", allEntries = true),
            @CacheEvict(value = "adminContent", allEntries = true),
            @CacheEvict(value = "mainNav", allEntries = true),
    })
    public void deleteContentData(Long id) throws Exception {
        contentDataRepository.delete(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),
            @CacheEvict(value = "adminTree", allEntries = true),
            @CacheEvict(value = "mainNav", allEntries = true),
            @CacheEvict(value = "adminContent", allEntries = true),
    })
    public List<ContentDataEntity> saveContentData(List<ContentDataEntity> contents) {
        return contentDataRepository.save(contents);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),
            @CacheEvict(value = "adminTree", allEntries = true),
            @CacheEvict(value = "mainNav", allEntries = true),
            @CacheEvict(value = "adminContent", allEntries = true),
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
                .leftJoin(contentEntity.contentTemplate).fetch()
                .where(contentEntity.enabled.eq(true)
                        .and(contentEntity.menuItem.eq(true))
                        .and(contentEntity.contentType.name.like("PAGE%"))
                        .and(contentDataEntity.language.locale.eq(lang))
                )
                .orderBy(contentEntity.position.asc())
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
                        .orderBy(contentEntity.position.asc())
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
        if(type == null ){
            throw new Exception("type  must not be null");
        }
        //if(locale.equals("all")){
        //    contentEntities =  contentRepository.findAllByContentTypeName(type);
        //} else{
        if(StringUtils.isEmpty(locale)){
            contentEntities = contentRepository.findAllByContentTypeName(type);
        } else {
            contentEntities = contentRepository.findAllByContentTypeNameAndContentDataListLanguageLocale(type, locale);
        }

        //}
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;
        // reload tree like this : table.ajax.reload()
        SimpleDateFormat sdf = new SimpleDateFormat(CmsUtils.DATETIME_FORMAT);
        for (ContentEntity c : contentEntities) {
            ContentDataEntity contentData = null;
            if(StringUtils.isEmpty(locale)){
                contentData = c.getContentDataList().get(applicationService.getDefaultSiteLang());
                if(contentData == null) {
                    Map.Entry<String, ContentDataEntity> entry = c.getContentDataList().entrySet().iterator().next();
                    contentData = entry.getValue();
                }
            } else {
                contentData = c.getContentDataList().get(locale);
            }
            String s = "";
            for ( String key : c.getContentDataList().keySet() ) {
                s += key + ", ";
            }
            if(s.length()>2) s= s.substring(0, s.length()-2);
            String title = contentData.getTitle();
            if(StringUtils.isEmpty(title)) title = c.getName();
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", c.getId())
                                                            .add("contentDataId", contentData.getId())
                                                            .add("lang", contentData.getLanguage().getLocale()));
            row.add("active", c.isEnabled());
            row.add("title", CmsUtils.emptyStringIfnull(title));
            row.add("lang", s);
            row.add("dateBegin", formatDateTime(sdf, c.getBeginDate()));
            row.add("dateEnd", formatDateTime(sdf, c.getEndDate()));
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }

    private static String formatDateTime(SimpleDateFormat dt, Date date) {
        return (date == null ? "/" : CmsUtils.emptyStringIfnull(dt.format(date)));
    }

    @Override
    @Cacheable(value = "adminTree")
    public String getPagesTree() {
        List<ContentEntity> pages = contentRepository.findAllByContentTypeName("PAGE%"); // force first level cache
        List<ContentEntity> roots = contentRepository.findByContentTypeNameLikeAndContentParentIsNullOrderByPositionAsc("PAGE%");
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

    @Override
    public boolean contentCanBeDeleted(ContentEntity content, String contentDataLocale) {
        if(content == null || content.getId() == 0) return false;
        ContentEntity temp;
        JPAQuery query;
        QContentEntity contentEntity = QContentEntity.contentEntity;
        QContentDataEntity qContentDataEntity = QContentDataEntity.contentDataEntity;

        for (ContentEntity c : content.getContentChildren()) {

            query = new JPAQuery(entityManager);

            long result =
                    query.from(contentEntity)
                            .leftJoin(contentEntity.contentDataList, qContentDataEntity)
                            .where(contentEntity.id.eq(c.getId())
                                    .and(qContentDataEntity.language.locale.eq(contentDataLocale))
                            )
                            .count();

            if(result > 0){
                return false;
            }

        }
        return true;
    }

    @Override
    public boolean contentIsPrivate(ContentEntity content) {
        // force to call cache
        ContentEntity parent = null;

        if(content == null || content.getId() == 0) return false;

        if(content.isMemberOnly())
            return true;

        Long parentId = content.getContentParent() == null ? 0L : content.getContentParent().getId();
        while(true){
            // no more parent
            if(parentId == 0) {
                return false;
            }
            parent = applicationContext.getBean(IContentService.class).findContentAdmin(parentId);
            if(parent.isMemberOnly()){
                return true;
            }
            parentId = parent.getContentParent() == null ? 0L : parent.getContentParent().getId();
        }
    }

    @Override
    public boolean contentIsVisible(ContentEntity content) {
        // force to call cache
        ContentEntity parent = null;

        if(content == null || content.getId() == 0 || !content.isEnabled())
            return false;

        Long parentId = content.getContentParent() == null ? 0L : content.getContentParent().getId();
        while(true){
            // no more parent
            if(parentId == 0) {
                return true;
            }
            parent = applicationContext.getBean(IContentService.class).findContentAdmin(parentId);

            if(!parent.isEnabled()){
                return false;
            }
            parentId = parent.getContentParent() == null ? 0L : parent.getContentParent().getId();
        }
    }

    @Override
    public boolean contentIsVisible(ContentEntity content, ContentDataEntity contentData) {
        // force to call cache
        ContentEntity parent = null;
        ContentDataEntity data = null;

        if(content == null || content.getId() == 0 || !content.isEnabled() || contentData == null || !contentData.isEnabled())
            return false;

        Long parentId = content.getContentParent() == null ? 0L : content.getContentParent().getId();
        while(true){
            // no more parent
            if(parentId == 0) {
                return true;
            }
            parent = applicationContext.getBean(IContentService.class).findContentAdmin(parentId);
            data = parent.getContentDataList().get(contentData.getLanguage().getLocale());

            if(!parent.isEnabled()){
                return false;
            }

            if(data != null && !data.isEnabled()) {
                return false;
            }

            parentId = parent.getContentParent() == null ? 0L : parent.getContentParent().getId();
        }
    }

    @Override
    public Collection<String> getRoleForContent(ContentEntity content) {
        JPAQuery query = new JPAQuery(entityManager);
        Collection<String> result = new HashSet<>();

        if(content == null || content.getId() == 0) return null;

        Set<RoleEntity> roles = content.getRoles();
        for (RoleEntity role : roles) {
            result.add(role.getName());
        }

        ContentEntity parent = null;
        Long parentId = content.getContentParent() == null ? 0L : content.getContentParent().getId();
        while(true){
            // no more parent
            if(parentId == 0) {
                break;
            }
            parent = applicationContext.getBean(IContentService.class).findContentAdmin(parentId);

            Set<RoleEntity> parentRoles = parent.getRoles();
            for (RoleEntity role : parentRoles) {
                result.add(role.getName());
            }

            parentId = parent.getContentParent() == null ? 0L : parent.getContentParent().getId();
        }
        return result;
    }

    @Override
    public Collection<String> contentRequiredRole(ContentEntity content) {
        return null;
    }

    private String buildNavMenu(List<ContentEntity> pages, StringBuilder sb, String locale, long depth) {

        boolean isFolder = false;
        boolean isLink = false;
        for (ContentEntity p : pages) {
            isFolder = p.getContentTemplate().getName().toLowerCase().equals("folder");
            isLink = p.getContentTemplate().getName().toLowerCase().equals("link");
            sb.append("<li>");
            if (!p.getContentDataList().isEmpty()) {
                ContentDataEntity data = p.getContentDataList().get(locale);
                if(data == null) break;
                if(!isFolder) {
                    sb.append("<a href=\"");
                    if (isLink) {

                        sb.append((String)CmsUtils.parseData(data.getData()).get("_text"));
                        sb.append("\" target=\"_blank\">");
                    } else {
                        sb.append(data.getComputedSlug());
                        sb.append("\">");
                    }

                    sb.append(data.getTitle());
                    sb.append("</a>");
                } else {
                    sb.append("<span>" + data.getTitle() + "</span>");
                }
                List<ContentEntity> childrens = p.getContentChildren();
                if (childrens.size() > 0 && depth != 0) {
                    // This is bad :(
                    Collections.sort(childrens, (p1, p2) -> Integer.compare(p1.getPosition(), p2.getPosition()));
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

            boolean isFolderPage = false;
            boolean isInvisible = false;
            boolean isLocked = false;

            if (!first) {
                sb.append(",");
            }
            first = false;

            sb
                .append("{ \"title\": \"")
                .append(p.getName())
                .append("\", \"key\": \"")
                .append(p.getId()).append("\"");

            List<ContentEntity> childrens = p.getContentChildren();

            if (childrens.size() > 0) {
                isFolderPage= true;
                if (level <= MAX_EXPANDED_TREE_LEVEL) {
                    sb.append(", \"expanded\":true");
                }
                sb.append(", \"folder\":true");
                // This is bad :(
                Collections.sort(childrens, (p1, p2) -> Integer.compare(p1.getPosition(), p2.getPosition()));

                sb.append(", \"children\" : [");
                buildJsonTree(childrens, sb, true, level + 1);
                sb.append("]");
            }

            isLocked = this.contentIsPrivate(p);
            isInvisible = !this.contentIsVisible(p);
            String icon = "";
            String name = p.getName();
            String template = p.getContentTemplate().getName().toLowerCase();
            boolean isFolder = p.getContentTemplate().getName().toLowerCase().equals("folder");
            boolean isLink = p.getContentTemplate().getName().toLowerCase().equals("link");

            if(isFolder){ icon= "folder.png";}
            else if(isLink){ icon= "link.png"; }
            else if(!isFolderPage && !isLocked && !isInvisible){ icon= "file.png"; }
            else if(!isFolderPage && !isLocked && isInvisible) { icon= "hidden.png"; }
            else if(!isFolderPage && isLocked && !isInvisible) { icon= "lock.png"; }
            else if(!isFolderPage && isLocked && isInvisible) { icon= "lock-invisible.png"; }
            else if(isFolderPage && !isLocked && !isInvisible){ icon= "folder-page.png"; }
            else if(isFolderPage && !isLocked && isInvisible) { icon= "folder-invisible.png"; }
            else if(isFolderPage && isLocked && !isInvisible) { icon= "folder-locked.png"; }
            else if(isFolderPage && isLocked && isInvisible) { icon= "folder-invisble-locked.png"; }

            sb.append(",\"icon\": \"")
            .append("/resources/cms/img/tree/" + icon + "\"");


            sb.append("}");
        }

        return sb.toString();
    }
}
