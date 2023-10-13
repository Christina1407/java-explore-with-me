package ru.practicum.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "hits", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Hit {
    @Id
    @Column(name = "hit_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hitId;
    @Column(name = "uri", nullable = false, length = 2000)
    private String uri;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "app_id")
    private App app;
    @Column(name = "ip", nullable = false, length = 50)
    private String ip;
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
