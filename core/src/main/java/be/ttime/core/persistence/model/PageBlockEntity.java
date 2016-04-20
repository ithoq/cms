package be.ttime.core.persistence.model;

import be.ttime.core.persistence.converter.BlockTypeConverter;
import com.google.gson.annotations.Expose;
import lombok.*;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "page_block")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "name"})
@NoArgsConstructor
@AllArgsConstructor
public class PageBlockEntity {

    @Expose
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Expose
    @Column(nullable = false, unique = true)
    private String name;
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
    @Convert(converter = BlockTypeConverter.class)
    @Column(nullable = false, length = 1)
    private BlockType blockType = BlockType.Content;
    @OneToMany(mappedBy = "pageBlock")
    private List<PageTemplateEntity> pageTemplates;
    @ManyToOne
    private ApplicationLanguageEntity language;

    public enum BlockType {
        Content("Content"),
        Navigation("Navigation"),
        PageTemplate("PageTemplate"),
        System("System"),
        FieldSet("FieldSet");

        private String name;

        BlockType(String name) {
            this.name = name;
        }

        public String getBlockType() {
            return this.name;
        }
    }
}