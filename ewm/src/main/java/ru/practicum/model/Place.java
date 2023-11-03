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
    @Column(name = "location_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "latitude", nullable = false)
    private float latitude;
    @Column(name = "longitude", nullable = false)
    private float longitude;
    @Column(name = "radius", nullable = false)
    private int radius;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private LocationType type;
    @Column(name = "season", length = 20)
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
}
