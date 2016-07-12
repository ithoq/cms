package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "menu")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class MenuEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private String name;
    private String description;
    @OneToMany(mappedBy = "menu")
    List<MenuItemEntity> items;
}
