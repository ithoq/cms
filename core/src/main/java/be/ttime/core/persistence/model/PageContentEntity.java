package be.ttime.core.persistence.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "page_content", schema = "cognosco")
@Getter
@Setter
public class PageContentEntity {

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

    private String test;
    private String slug;
    private String computedSlug;
    private String seoTitle;
    private String seoTag;
    private String seoDescription;
    private String seoH1;

    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationLanguageEntity language;
}
