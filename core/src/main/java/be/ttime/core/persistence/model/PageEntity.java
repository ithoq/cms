package be.ttime.core.persistence.model;

import be.ttime.core.persistence.converter.PageTypeConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "page")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "createdDate"})
public class PageEntity {

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
    private boolean enabled = true;
    @Column(name = "pos", nullable = false)
    private int order = -1;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(columnDefinition = "TINYINT(1) default '1'")
    private boolean menuItemLink = true;
    @Column(nullable = false, columnDefinition = "SMALLINT(2) default '-1'")
    private int level = -1;
    @Column(columnDefinition = "TINYINT(1) default '1'")
    private boolean menuItem = true;
    @Convert(converter = PageTypeConverter.class)
    @Column(name = "type", length = 1)
    private Type type = Type.Page;
    @ManyToOne
    private PageEntity pageParent;
    @OneToMany(mappedBy = "pageParent")
    private List<PageEntity> pageChildren;
    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.DELETE, org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private List<ContentEntity> pageContents;
    @ManyToOne
    private ContentTemplateEntity pageTemplate;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // Lazy for now (not used in the current version)
    @JoinTable(
            name = "page_privilege",
            joinColumns = @JoinColumn(name = "page_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private Set<PrivilegeEntity> privileges;

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
