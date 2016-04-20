package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "page_template")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "active"})
public class PageTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(name = "id", nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Column(nullable = false)
    private String name;
    @Lob
    private String fields;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean active;
    @OneToMany(mappedBy = "pageTemplate")
    private List<PageEntity> pageEntities;
    @ManyToOne
    private PageBlockEntity pageBlock;

}