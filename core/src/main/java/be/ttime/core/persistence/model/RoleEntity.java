package be.ttime.core.persistence.model;

import com.google.gson.annotations.Expose;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(nullable = false)
    @Expose
    private String name;

    private String description;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean deletable;

    @ManyToMany(fetch = FetchType.EAGER)
    @OrderBy("section ASC, name ASC")
    @JoinTable(name = "roles_privileges", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Set<PrivilegeEntity> privileges = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users;

    public RoleEntity() {
        super();
    }

    public RoleEntity(final String name) {
        super();
        this.name = name;
    }
}