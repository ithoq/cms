package be.ttime.core.persistence.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comment")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    private String author;
    private String authorEmail;
    private String authorurl;
    private String authorIp;
    @Temporal(TemporalType.DATE)
    private Date createdDate;
    private boolean approved = false;
    @Lob
    private String content;
    @ManyToOne
    private ContentDataEntity contentData;
    @ManyToOne
    private CommentEntity parent;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentEntity> children;

    public void addChild(CommentEntity child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(CommentEntity child) {
        children.remove(child);
        child.setParent(null);
    }

}
