package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CategoryEntity {

    @Id
    @Access(AccessType.PROPERTY)
    private String name;
}
