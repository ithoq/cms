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

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "taxonomyTermEntities")
    private List<ContentEntity> contents;

    @ManyToOne(optional = false)
    private TaxonomyTypeEntity taxonomyType;

    @OneToMany(mappedBy = "taxonomyTerm",
            cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TaxonomyTermDataEntity> termDataList;

    @ManyToOne
    private TaxonomyTermEntity taxonomyParent;

    @OneToMany(mappedBy = "taxonomyParent")
    private List<TaxonomyTermEntity> taxonomyChildren;
}
