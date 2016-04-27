package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "fieldset")
@Getter
@Setter
@EqualsAndHashCode
public class FieldsetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "fieldset")
    private Set<ContentTemplateFieldsetEntity> contentTemplateFieldset;

}
