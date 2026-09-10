package com.learn.tinyurl.adapters.outbound.persistence;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "tb_url_alias",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_tb_url_alias_short_key", columnNames = {"short_key"})
        }
)
public class TinyUrlEntity {

    public TinyUrlEntity(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @Column(name = "short_key", nullable = false, length = 20)
    private String shortKey;

    @Column(name = "expired_at", columnDefinition = "TIMESTAMPTZ")
    private Instant expiredAt;

    public TinyUrlEntity(String url, String shortKey, Instant expiredAt, String status, Long version, Instant creationTs, Instant lastUpdatedTs) {
        this.url = url;
        this.shortKey = shortKey;
        this.expiredAt = expiredAt;
        this.status = status;
        this.version = version;
        this.creationTs = creationTs;
        this.lastUpdatedTs = lastUpdatedTs;
    }

    @Column(name = "status", nullable = false, length = 10)
    private String status = "100";

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "creation_ts", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant creationTs;

    @UpdateTimestamp
    @Column(name = "last_updated_ts", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant lastUpdatedTs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShortKey() {
        return shortKey;
    }

    public void setShortKey(String shortKey) {
        this.shortKey = shortKey;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Instant getCreationTs() {
        return creationTs;
    }

    public void setCreationTs(Instant creationTs) {
        this.creationTs = creationTs;
    }

    public Instant getLastUpdatedTs() {
        return lastUpdatedTs;
    }

    public void setLastUpdatedTs(Instant lastUpdatedTs) {
        this.lastUpdatedTs = lastUpdatedTs;
    }

}