package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "taxonomy_term_data")
@Getter
@Setter
@EqualsAndHashCode
public class TaxonomyTermDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    private String slug;

    private String name;

    @ManyToOne
    private TaxonomyTermEntity taxonomyTerm;

}
