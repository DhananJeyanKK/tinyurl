package com.learn.tinyurl.adapters.outbound.persistence;

import jakarta.persistence.*;
import lombok.*;
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
@Getter
@Setter
@NoArgsConstructor             // Required by JPA
@AllArgsConstructor            // Required by @Builder when combined with @NoArgsConstructor
@Builder
public class TinyUrlEntity {

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
}