package be.ttime.core.persistence.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "privilege")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
public class PrivilegeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @ManyToMany(mappedBy = "privileges")
    private Collection<RoleEntity> roles;
    @ManyToMany(mappedBy = "privileges")
    private Set<FileEntity> files;
    @ManyToMany(mappedBy = "privileges")
    private Set<PageEntity> pages;

    private String name;

    public PrivilegeEntity() {
        super();
    }

    public PrivilegeEntity(final String name) {
        super();
        this.name = name;
    }
}
