package be.ttime.core.persistence.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @ManyToMany(mappedBy = "roles")
    private Collection<GroupEntity> groups;
    @ManyToMany(mappedBy = "roles")
    private Set<FileEntity> files;
    @ManyToMany(mappedBy = "roles")
    private Set<ContentEntity> pages;

    private String name;

    @Column(nullable = false, columnDefinition = "TINYINT(1) default '0'")
    private boolean superAdmin;

    private String description;

    private String section;

    public RoleEntity() {
        super();
    }

    public RoleEntity(final String name) {
        super();
        this.name = name;
    }
}
