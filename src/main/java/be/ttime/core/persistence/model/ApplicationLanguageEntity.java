package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "application_language", schema = "cognosco")
@Getter
@Setter
@EqualsAndHashCode(of = {"locale"})
public class ApplicationLanguageEntity {

    @Id
    @Access(AccessType.PROPERTY)
    private String locale;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '0'")
    private boolean rtl = false;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '0'")
    private boolean enabledForAdmin = false;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '0'")
    private boolean enabledForPublic = false;

    @OneToMany(mappedBy = "language")
    private List<PageContentEntity> pageContents;

    @OneToMany(mappedBy = "language")
    private List<PageBlockEntity> pageBlocks;

    @OneToMany(mappedBy = "language")
    private List<MessageTranslationsEntity> translations;

}
