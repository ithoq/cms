package be.ttime.core.persistence.model;

import com.google.gson.annotations.Expose;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "content_template_fieldset")
@Getter
@Setter
@EqualsAndHashCode
public class ContentTemplateFieldsetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    @Expose private long id;

    @Column(nullable = false)
    @Expose private String namespace;

    @Column(nullable = false)
    @Expose private int position;

    @Column(nullable = false)
    @Expose private String name;

    @ManyToOne
    @JoinColumn(name = "page_template_id")
    private ContentTemplateEntity contentTemplate;

    @ManyToOne
    @JoinColumn(name = "fieldset_id")
    @Expose private FieldsetEntity fieldset;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "contentTemplateFieldsetEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    @Expose private List<InputDataEntity> dataEntities;
}