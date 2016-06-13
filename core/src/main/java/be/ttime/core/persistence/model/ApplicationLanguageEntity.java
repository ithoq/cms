package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "application_language")
@Getter
@Setter
@EqualsAndHashCode(of = {"locale"})
public class ApplicationLanguageEntity {

    @Id
    @Access(AccessType.PROPERTY)
    private String locale;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '0'")
    private boolean enabledForAdmin = false;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '0'")
    private boolean enabledForPublic = false;

    @Override
    public String toString() {
        return locale;
    }
}
