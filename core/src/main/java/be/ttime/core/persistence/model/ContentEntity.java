package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "content", indexes = {
        @Index(name = "idx_slug", columnList = "computedSlug,language_locale", unique = true)})
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "slug"})
public class ContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date createdDate;
    @Temporal(TemporalType.DATE)
    private Date modifiedDate;
    @Lob
    private String data;
    private String slug;
    private String computedSlug;
    private String seoTitle;
    private String seoTag;
    private String seoDescription;
    private String seoH1;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileEntity> pageFiles;
    @ManyToOne
    private PageEntity page;
    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationLanguageEntity language;
    @ManyToOne(fetch = FetchType.LAZY)
    private ResourceTypeEntity resourceType;
}
