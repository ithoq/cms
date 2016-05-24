package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "taxonomy_term")
@Getter
@Setter
@EqualsAndHashCode
public class TaxonomyTermEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    private String name;

    private int position;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "content_term", joinColumns = @JoinColumn(name = "taxonomy_term_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "content_id", referencedColumnName = "id"))
    private List<ContentEntity> contents;

    @ManyToOne
    private TaxonomyTypeEntity taxonomyType;

    @OneToMany(mappedBy = "taxonomyTerm")
    private List<TaxonomyTermDataEntity> termDataList;

    @ManyToOne
    private TaxonomyTermEntity taxonomyParent;

    @OneToMany(mappedBy = "taxonomyParent")
    private List<TaxonomyTermEntity> taxonomyChildren;
}
