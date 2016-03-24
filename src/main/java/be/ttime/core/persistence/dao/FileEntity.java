package be.ttime.core.persistence.dao;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "file", schema = "cognosco")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "description", "serverName"})
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Temporal(TemporalType.DATE)
    @Column(name = "upload_date", nullable = false)
    private Date uploadDate;
    @Column(nullable = false)
    private String name;
    @Column
    private String description;
    @Column
    private String mimeType;
    @Column(nullable = false)
    private String extension;
    @Column(nullable = false)
    private String serverName;
    @Column(nullable = false, columnDefinition = "INT(11) UNSIGNED")
    private long size;
    @ManyToOne
    private PageEntity page;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // lazy because not used for now !
    @JoinTable(
            name = "file_authority",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    private Set<AuthorityEntity> authorities;
}