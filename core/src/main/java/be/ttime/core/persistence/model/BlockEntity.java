package be.ttime.core.persistence.model;

import com.google.gson.annotations.Expose;
import lombok.*;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "page_block")
@Getter
@Setter
@EqualsAndHashCode(of = {"name", "displayName"})
@NoArgsConstructor
@AllArgsConstructor
public class BlockEntity {

    @Expose
    @Id
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private String name;
    @Expose
    @Column(nullable = false, unique = true)
    private String displayName;
    @Expose
    @Lob
    private String content;
    @Expose
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;
    @Expose
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean deletable = true;
    @Expose
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean cacheable = true;
    @Expose
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '0'")
    private boolean dynamic = false;
    @Expose
    @ManyToOne
    private BlockTypeEntity blockType;
    @ManyToOne
    private ApplicationLanguageEntity language;
}