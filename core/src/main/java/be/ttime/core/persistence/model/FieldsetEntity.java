package be.ttime.core.persistence.model;

import com.google.gson.annotations.Expose;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

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
    @Expose
    private long id;
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private boolean array;

    private boolean deletable = true;

    @OneToMany(mappedBy = "fieldset", fetch = FetchType.LAZY)
    private List<ContentTemplateFieldsetEntity> contentTemplateFieldset;

    @OneToMany(mappedBy = "fieldset", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Expose
    private List<InputDefinitionEntity> inputs;

    @OneToOne(targetEntity = BlockEntity.class, fetch = FetchType.EAGER)
    private BlockEntity blockEntity;

}
