package be.ttime.core.persistence.model;

import com.google.gson.annotations.Expose;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "userGroup")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
public class GroupEntity {

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
    @JoinTable(name = "group_role", joinColumns = @JoinColumn(name = "userGroup_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<RoleEntity> roles = new HashSet<>();

    @ManyToMany(mappedBy = "groups")
    private Set<UserEntity> users;

    public GroupEntity() {
        super();
    }

    public GroupEntity(final String name) {
        super();
        this.name = name;
    }
}