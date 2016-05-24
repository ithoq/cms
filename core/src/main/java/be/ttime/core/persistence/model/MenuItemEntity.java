package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "menu_item")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class MenuItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    private int position;
    @ManyToOne
    private MenuEntity menu;
    @ManyToOne
    private MenuItemEntity menuItemParent;
    @OneToMany(mappedBy = "menuItemParent")
    private List<MenuItemEntity> menuItemChildrens;

    @OneToOne(targetEntity = ContentEntity.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "content")
    private ContentEntity content;
}
