package dev.aj.sdj_hibernate.domain.entities.auditing;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Embeddable
public class AuditMetaData {

    @CreatedBy
    @Column(name = "created_by_user", nullable = false, updatable = false)
    private String createdByUser;

    @LastModifiedBy
    @Column(name = "last_modified_by_user", nullable = true, insertable = false)
    private String lastModifiedByUser;

    @CreatedDate
    @Column(name = "created_timestamp", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE", updatable = false)
    private Instant createdTimestamp;

    @LastModifiedDate
    @Column(name = "last_modified_timestamp", nullable = true, columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false)
    private Instant lastModifiedTimestamp;
}
