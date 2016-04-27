package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "resource_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ResourceTypeEntity {
    @Id
    @Access(AccessType.PROPERTY)
    private String name;
}
