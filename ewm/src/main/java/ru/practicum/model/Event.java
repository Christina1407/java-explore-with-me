package ru.practicum.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.practicum.model.enums.StateEnum;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Entity
@Table(name = "events", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Event {
    @Id
    @Column(name = "event_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(name = "description", nullable = false, length = 7000)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "published_date", nullable = false)
    private LocalDateTime publishedOn;
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;
    @Column(name = "paid")
    private boolean paid;
    @Column(name = "participant_limit")
    private int participantLimit;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Column(name = "title", nullable = false, length = 120)
    private String title;
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private StateEnum state;
    @OneToMany(mappedBy = "event")
    private List<Request> requests;
    @ManyToMany
    @JoinTable(
            name = "events_compilations",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "compilation_id"))
    List<Compilation> compilations;
    @Transient
    private Long views;
    @Transient
    private Long confirmedRequests;
}
