package be.ttime.core.persistence.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "page_content", indexes = {
        @Index(name = "idx_slug", columnList = "computedSlug,language_locale", unique = true)})
@Getter
@Setter
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
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<FileEntity> pageFiles;
    @ManyToOne
    private PageEntity page;
    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationLanguageEntity language;
    @ManyToOne(fetch = FetchType.LAZY)
    private ResourceTypeEntity resourceType;
}
