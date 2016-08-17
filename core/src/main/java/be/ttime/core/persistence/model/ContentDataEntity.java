package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "content_data", indexes = {
        @Index(name = "idx_slug", columnList = "computedSlug,language_locale", unique = true)})
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "computedSlug"}, callSuper = true)
public class ContentDataEntity extends AbstractTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Version
    private Long version;
    private Integer position;
    private String title;
    @Column(length = 2000)
    private String intro;
    @Lob
    private String data;
    private String slug;
    @Column(unique = true)
    private String computedSlug;
    @Column(columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contentDataEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FileEntity> contentFiles;

    @ManyToOne(fetch = FetchType.LAZY)
    private ContentEntity content;

    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationLanguageEntity language;

    @OneToMany(mappedBy = "contentData", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentEntity> commentList;
}
