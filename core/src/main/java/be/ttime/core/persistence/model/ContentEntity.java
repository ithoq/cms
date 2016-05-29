package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "content")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "createdDate"})
public class ContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date createdDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date beginDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    private boolean enabled = true;
    @Column(name = "pos", nullable = false)
    private int order = -1;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(columnDefinition = "TINYINT(1) default '1'")
    private boolean menuItem = true;
    @ManyToOne
    private ContentEntity contentParent;
    @OneToMany(mappedBy = "contentParent")
    private Set<ContentEntity> contentChildren;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContentDataEntity> dataList;
    @ManyToOne
    private ContentTemplateEntity contentTemplate;
    @ManyToOne
    private ContentTypeEntity contentType;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL) // Lazy for now (not used in the current version)
    @JoinTable(
            name = "content_privilege",
            joinColumns = @JoinColumn(name = "content_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private Set<PrivilegeEntity> privileges;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "contents")
    private Set<TaxonomyTermEntity> taxonomyTermEntities;
    @OneToMany(mappedBy = "content", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ContentDictionaryEntity> dictionaryList;
}
