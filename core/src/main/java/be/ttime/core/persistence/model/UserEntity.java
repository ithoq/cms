package be.ttime.core.persistence.model;

import be.ttime.core.persistence.AbstractTimestampEntity;
import be.ttime.core.persistence.converter.UserGenderConverter;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCustomEntity> dictionaryList;
    @ManyToOne
    private CompanyEntity company;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Collection<RoleEntity> roles;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getGrantedAuthorities(getPrivileges(roles));
    }

    private final List<String> getPrivileges(final Collection<RoleEntity> roles) {
        final List<String> privileges = new ArrayList<>();
        final List<PrivilegeEntity> collection = new ArrayList<>();
        for (final RoleEntity role : roles) {
            collection.addAll(role.getPrivileges());
        }
        for (final PrivilegeEntity item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private final List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    public enum Gender {MALE, FEMALE}
}