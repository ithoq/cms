package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "content_data", indexes = {
        @Index(name = "idx_slug", columnList = "computedSlug,language_locale", unique = true)})
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "computedSlug"}, callSuper = true)
public class ContentDataEntity extends AbstractTimestampEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    @Version
    private Long version;
    private Integer position;
    private String title;
    private String intro;
    @Lob
    private String data;
    private String slug;
    private String computedSlug;
    @Column(columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "contentDataEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FileEntity> contentFiles;

    @ManyToOne(fetch = FetchType.LAZY)
    private ContentEntity content;

    @ManyToOne(fetch = FetchType.LAZY)
    private ApplicationLanguageEntity language;

    @OneToMany(mappedBy = "contentData", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentEntity> commentList;

    // TODO : something better than this
    public Map<String, List<FileEntity>> getFileByGroupMap(){
        Map<String, List<FileEntity>> result = new HashMap<>();
        for (FileEntity f : contentFiles) {
            if(!StringUtils.isEmpty(f.getFileGroup()) && f.getFileType().getName().equals("DOWNLOAD")){
                List<FileEntity> list = result.get(f.getFileGroup());
                if(list == null){
                    list = new ArrayList<>();
                }
                list.add(f);
                result.put(f.getFileGroup(), list);
            }
        }
        return result;
    }
}
