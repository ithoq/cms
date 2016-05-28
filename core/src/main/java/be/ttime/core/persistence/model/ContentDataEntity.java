package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "content_data", indexes = {
        @Index(name = "idx_slug", columnList = "computedSlug,language_locale", unique = true)})
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "computedSlug"})
public class ContentDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Version
    private Long version;
    private int position;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date createdDate;
    @Temporal(TemporalType.DATE)
    private Date modifiedDate;
    private String title;
    @Lob
    private String data;
    private String slug;
    private String computedSlug;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "contentFile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileEntity> contentFiles;

    @ManyToOne(fetch = FetchType.LAZY)
    private ContentEntity content;

    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationLanguageEntity language;

    @OneToMany(mappedBy = "contentData", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentDataDictionaryEntity> dictionaryList;

    @OneToMany(mappedBy = "contentData", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> commentList;
}
