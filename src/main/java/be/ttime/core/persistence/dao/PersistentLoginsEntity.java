package be.ttime.core.persistence.dao;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "persistent_logins", schema = "cognosco")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "username", "series", "token", "lastUsed"})
public class PersistentLoginsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(length = 64, nullable = false)
    private String username;
    @Column(length = 64, nullable = false, unique = true)
    private String series;
    @Column(length = 64, nullable = false)
    private String token;
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date lastUsed;
}