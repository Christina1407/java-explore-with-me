package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "apps", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class App {
    @Id
    @Column(name = "app_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appId;
    @Column(name = "name", nullable = false, length = 200)
    private String name;
}
