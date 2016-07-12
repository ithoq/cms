package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "module_instance")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name", "description"})
public class ModuleInstanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    private String name;
    private String description;
    @Lob private String data;
    @Column(columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;
    private String moduleName;
    private String moduleGroup;
}
