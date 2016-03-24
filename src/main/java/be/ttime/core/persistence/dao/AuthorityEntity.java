package be.ttime.core.persistence.dao;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "authority", schema = "cognosco")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "authority"})
public class AuthorityEntity implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(nullable = false, length = 50)
    private String authority;
    @ManyToMany(mappedBy = "authorities")
    private Set<UserEntity> users;
    @ManyToMany(mappedBy = "authorities")
    private Set<FileEntity> files;
    @ManyToMany(mappedBy = "authorities")
    private Set<PageEntity> pages;
}