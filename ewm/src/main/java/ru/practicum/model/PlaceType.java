package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "place_type", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlaceType {
    @Id
    @Column(name = "type_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
}
