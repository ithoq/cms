package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
    private long id;

    @Column(nullable = false)
    private String namespace;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "page_template_id")
    private ContentTemplateEntity contentTemplate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fieldset_id")
    private FieldsetEntity fieldset;

}
