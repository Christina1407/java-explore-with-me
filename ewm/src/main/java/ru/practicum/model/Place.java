package ru.practicum.model;

import lombok.*;
import org.hibernate.annotations.Where;
import ru.practicum.model.enums.SeasonEnum;

import javax.persistence.*;
import java.util.List;

@Builder
@Entity
@Table(name = "places", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Place {
    @Id
    @Column(name = " place_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "latitude", nullable = false)
    private Double latitude;
    @Column(name = "longitude", nullable = false)
    private Double longitude;
    @Column(name = "radius", nullable = false)
    private Integer radius;
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private PlaceType type;
    @Column(name = "season", length = 20)
    @Enumerated(EnumType.STRING)
    private SeasonEnum season;
    @Column(name = "feature", length = 50)
    private String feature;
    @ManyToMany
    @Where(clause = "state = 'PUBLISHED'")
    @JoinTable(
            name = "events_places",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "place_id"))
    private List<Event> publishedEvents;
    @ManyToMany
    @JoinTable(
            name = "events_places",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "place_id"))
    private List<Event> allEvents;
    @Transient
    private Double distance;
}
