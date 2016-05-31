package be.ttime.core.persistence.model;

import com.google.gson.annotations.Expose;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "content_template")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "active"})
public class ContentTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(columnDefinition = "SMALLINT(11) UNSIGNED")
    @Expose private long id;
    @Column(nullable = false)
    @Expose private String name;
    @Expose private String description;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean active;
    @ManyToOne(fetch = FetchType.LAZY)
    private ContentTypeEntity contentType;
    @OneToMany(mappedBy = "contentTemplate")
    private List<ContentEntity> pageEntities;
    private boolean deletable;

    @OneToOne(targetEntity = BlockEntity.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private BlockEntity block;

    @OneToMany(mappedBy = "contentTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Expose
    private List<ContentTemplateFieldsetEntity> contentTemplateFieldset;


}