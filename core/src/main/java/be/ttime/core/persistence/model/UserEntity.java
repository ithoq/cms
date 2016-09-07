package be.ttime.core.persistence.model;

import be.ttime.core.persistence.converter.UserGenderConverter;
import be.ttime.core.util.CmsUtils;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "username", "password"}, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends AbstractTimestampEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    private String username;
    @Column(nullable = false)
    private String password;
    private String firstName;
    private String lastName;
    private String userTitle;
    private String avatar;
    @Column(unique = true, nullable = false)
    private String email;
    private String street1;
    private String street2;
    private String street3;
    private String city;
    private String zip;
    private String countyCode;
    private String countryName;
    private String comment;
    private String organisation;
    @Temporal(TemporalType.DATE)
    private Date birthday;
    @Temporal(TemporalType.DATE)
    private Date passwordModifiedDate;
    @Convert(converter = UserGenderConverter.class)
    @Column(length = 1)
    private Gender gender;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean accountNonExpired = true;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean accountNonLocked = true;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean credentialsNonExpired = true;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCustomEntity> dictionaryList;
    @ManyToOne
    private CompanyEntity company;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_group", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "userGroup_id", referencedColumnName = "id"))
    @Fetch(FetchMode.JOIN)
    private Set<GroupEntity> groups;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        boolean isSuperAdmin = CmsUtils.hasGroup(this, CmsUtils.GROUP_SUPER_ADMIN);
        return isSuperAdmin ? CmsUtils.fullPrivilegeList : getGrantedAuthorities(getPrivileges(groups));
    }

    private final List<String> getPrivileges(final Collection<GroupEntity> roles) {
        final List<String> privileges = new ArrayList<>();
        final List<RoleEntity> collection = new ArrayList<>();
        for (final GroupEntity role : roles) {
            collection.addAll(role.getRoles());
        }
        for (final RoleEntity item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private final Set<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final Set<GrantedAuthority> authorities = new HashSet<>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    public enum Gender {MALE, FEMALE}
}