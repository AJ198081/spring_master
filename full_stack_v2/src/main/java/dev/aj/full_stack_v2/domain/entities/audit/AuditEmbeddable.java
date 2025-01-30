package dev.aj.full_stack_v2.domain.entities.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;

@Embeddable
public class AuditEmbeddable {

    @CreatedBy
    @Column(name = "created_by", updatable = false, columnDefinition = "varchar(40)")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by", insertable = false, columnDefinition = "varchar(40)")
    private String lastModifiedBy;

    @CreatedDate
    @Column(name = "created_timestamp", updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdAt;

    @LastModifiedDate
    @Column(name = "last_modified_timestamp", insertable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime lastModifiedAt;
}
