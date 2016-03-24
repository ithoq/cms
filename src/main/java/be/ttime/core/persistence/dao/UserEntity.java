package be.ttime.core.persistence.dao;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "user", schema = "cognosco")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "username", "password"})
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean accountNonExpired = true;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean accountNonLocked = true;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean credentialsNonExpired = true;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private Set<AuthorityEntity> authorities;
}