package be.ttime.core.persistence.model;


import com.google.gson.annotations.Expose;
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
    @Expose private long id;
    @Expose private String validation;
    @Expose private String title;
    @Expose private String hint;
    @Expose private String defaultValue;
    @ManyToOne
    @Expose private InputDefinitionEntity inputDefinition;

    @ManyToOne
    private FieldsetEntity fieldset;

    @ManyToOne
    private ContentTemplateFieldsetEntity contentTemplateFieldsetEntity;
}
