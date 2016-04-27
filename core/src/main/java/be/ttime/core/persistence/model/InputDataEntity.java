package be.ttime.core.persistence.model;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "input_data")
@Getter
@Setter
@EqualsAndHashCode
public class InputDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    private String validation;

    private String title;

    private String hint;

    @ManyToOne
    private InputDefinitionEntity inputDefinition;

    @ManyToOne
    private ContentTemplateEntity contentTemplateEntity;
}
