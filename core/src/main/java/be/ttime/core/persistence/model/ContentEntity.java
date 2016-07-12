package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "content")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"}, callSuper = true)
public class ContentEntity extends AbstractTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date beginDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    private boolean enabled = true;
    @Column(name = "pos", nullable = false)
    private int order = -1;
    @Column(nullable = false, unique = true)
    private String name;
    private String contentGroup;
    private String image;
    @Lob private String data;
    @Lob private String includeTop;
    @Lob private String includeBottom;
    @Column(columnDefinition = "TINYINT(1) default '1'")
    private boolean menuItem = true;
    @ManyToOne(fetch = FetchType.LAZY)
    private ContentEntity contentParent;
    @OneToMany(mappedBy = "contentParent")
    private Set<ContentEntity> contentChildren;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    @MapKeyColumn(name="language_locale")
    private Map<String, ContentDataEntity> contentDataList = new HashMap<>();
    @ManyToOne(fetch = FetchType.LAZY)
    private ContentTemplateEntity contentTemplate;
    @ManyToOne(fetch = FetchType.LAZY)
    private ContentTypeEntity contentType;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Lazy for now (not used in the current version)
    @JoinTable(
            name = "content_privilege",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private Set<PrivilegeEntity> privileges;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "content_term",
            joinColumns = @JoinColumn(name = "content_id", referencedColumnName = "id") ,
            inverseJoinColumns = @JoinColumn(name = "taxonomy_term_id", referencedColumnName = "id"))
    private Set<TaxonomyTermEntity> taxonomyTermEntities = new HashSet<>();


    public void addContentData(ContentDataEntity child) {
        if(contentDataList == null){
            contentDataList = new HashMap<>();
        }
        child.setContent(this);
        contentDataList.put(child.getLanguage().getLocale(), child);
    }

    public void removeContentData(ContentDataEntity child) {
        child.setContent(null);
        contentDataList.remove(child.getLanguage().getLocale());

    }

    public List<TaxonomyTermEntity> getTaxonomy(String type){
        List<TaxonomyTermEntity> result = new ArrayList<>();
        for (TaxonomyTermEntity t : taxonomyTermEntities) {
            if(t.getTaxonomyType().getName().equals(type)){
                result.add(t);
            }
        }
        return result;
    }
}
