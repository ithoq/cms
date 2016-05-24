package be.ttime.core.persistence.model;

import com.google.gson.annotations.Expose;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "taxonomy_type")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TaxonomyTypeEntity {

    @Expose
    @Id
    @Access(AccessType.PROPERTY)
    private String name;
}