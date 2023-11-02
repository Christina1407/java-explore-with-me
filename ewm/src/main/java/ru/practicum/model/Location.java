package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "locations", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Location {
    @Id
    @Column(name = "location_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lat", nullable = false)
    private float lat;
    @Column(name = "lon", nullable = false)
    private float lon;
}
