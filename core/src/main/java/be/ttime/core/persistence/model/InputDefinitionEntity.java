package be.ttime.core.persistence.model;

import com.google.gson.annotations.Expose;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "input_definition")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class InputDefinitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    @Expose
    private long id;
    @Expose
    private int sort;
    @Expose
    private String name;
    @Expose
    private boolean array;
    @Expose
    private String validation;

    @ManyToOne
    private FieldsetEntity fieldset;
}
