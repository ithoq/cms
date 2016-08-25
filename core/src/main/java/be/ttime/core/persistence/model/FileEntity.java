package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "file")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "description", "serverName"} , callSuper = true)
public class FileEntity extends AbstractTimestampEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(nullable = false)
    private String name;
    private String description;
    private String mimeType;
    @Column(nullable = false)
    private String extension;
    @Column(nullable = false)
    private String serverName;
    private String fileGroup;
    @Column(nullable = false, columnDefinition = "INT(11) UNSIGNED")
    private long size;

    private boolean directory = false;

    private String fileType;

    @ManyToOne
    private FileEntity fileParent;

    @OneToMany(mappedBy = "fileParent")
    private List<FileEntity> taxonomyChildren;

    @ManyToOne
    private ContentDataEntity contentDataEntity;

    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;
    @ManyToMany(fetch = FetchType.LAZY) // lazy because not used for now !
    @JoinTable(
            name = "file_role",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;
}
