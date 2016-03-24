package be.ttime.core.persistence.dao;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "page", schema = "cognosco")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "slug", "createdDate"})
public class PageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date createdDate;
    @Temporal(TemporalType.DATE)
    @Column
    private Date modifiedDate;
    @Lob
    @Column(name = "data")
    private String data;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;
    @Column(name = "pos", nullable = false)
    private int order = -1;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(unique = true)
    private String slug;
    @Column
    private String seoTitle;
    @Column
    private String seoTag;
    @Column
    private String seoDescription;
    @Column
    private String seoH1;
    @Column(columnDefinition = "TINYINT(1) default '1'")
    private boolean menuItemLink = true;
    @Column
    private String devIncludeTop;
    @Column
    private String devIncludeBot;
    @Column(nullable = false, columnDefinition = "SMALLINT(2) default '-1'")
    private int level = -1;
    @Column(columnDefinition = "TINYINT(1) default '1'")
    private boolean menuItem = true;
    @Enumerated(EnumType.STRING)
    @Column(name = "type", columnDefinition = "ENUM('Page', 'Link', 'Module')")
    private Type type = Type.Page;
    @ManyToOne
    private PageEntity pageParent;
    @OneToMany(mappedBy = "pageParent")
    private List<PageEntity> pageChildren;
    @ManyToOne
    private PageTemplateEntity pageTemplate;
    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<FileEntity> pageFiles;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Lazy for now (not used in the current version)
    @JoinTable(
            name = "page_authority",
            joinColumns = @JoinColumn(name = "page_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private Set<AuthorityEntity> authorities;

    public enum Type {
        Page("Page"),
        Link("Link"),
        Module("Module");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getType() {
            return this.name;
        }
    }
}