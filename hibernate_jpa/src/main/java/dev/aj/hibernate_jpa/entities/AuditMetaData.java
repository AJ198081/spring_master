package dev.aj.hibernate_jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;

@Embeddable
public class AuditMetaData {

    @CreatedBy
    @Column(updatable = false, nullable = false, columnDefinition = "VARCHAR(50)")
    private String createdBy;

    @LastModifiedBy
    @Column(insertable = false, columnDefinition = "VARCHAR(50)")
    private String lastModifiedBy;

    @CreatedDate
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime lastModifiedDate;

}
