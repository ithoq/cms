package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "application_config")
@Getter
@Setter
@EqualsAndHashCode
public class ApplicationConfigEntity {
    @Id
    @Access(AccessType.PROPERTY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    @OneToOne(targetEntity = ApplicationLanguageEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "default_admin_locale")
    private ApplicationLanguageEntity defaultAdminLang;

    @OneToOne(targetEntity = ApplicationLanguageEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "default_public_locale")
    private ApplicationLanguageEntity defaultPublicLang;

    private boolean forcedLangInUrl = false;

    private boolean alreadyInstall = false;

    private String siteName;

    private String url;

    private String seoDescription;
}
