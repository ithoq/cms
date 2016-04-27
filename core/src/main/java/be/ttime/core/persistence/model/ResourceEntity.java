package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "resource")
@Getter
@Setter
@EqualsAndHashCode
public class ResourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ResourceTypeEntity resourceType;

    @ManyToOne(fetch = FetchType.LAZY)
    private CategoryEntity category;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="resource_tags", joinColumns=@JoinColumn(name="tag_name"), inverseJoinColumns=@JoinColumn(name="resource_id"))
    private Collection<TagEntity> tags;
}
