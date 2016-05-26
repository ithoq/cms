package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "file")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "description", "serverName"})
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date uploadDate;
    @Column(nullable = false)
    private String name;
    private String description;
    private String mimeType;
    @Column(nullable = false)
    private String extension;
    @Column(nullable = false)
    private String serverName;
    @Column(nullable = false, columnDefinition = "INT(11) UNSIGNED")
    private long size;

    private boolean directory = false;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private FileTypeEntity contentType;

    @ManyToOne
    private FileEntity fileParent;

    @OneToMany(mappedBy = "fileParent")
    private List<FileEntity> taxonomyChildren;

    @ManyToOne
    private ContentDataEntity contentFile;

    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // lazy because not used for now !
    @JoinTable(
            name = "file_privilege",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private Set<PrivilegeEntity> privileges;
}
