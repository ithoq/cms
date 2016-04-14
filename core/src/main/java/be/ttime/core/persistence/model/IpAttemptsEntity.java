package be.ttime.core.persistence.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ip_attempts", schema = "cognosco")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "attempts", "lastModified"})
public class IpAttemptsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;
    private String ip;
    private int attempts;
    @Temporal(TemporalType.DATE)
    private Date lastModified;
}
