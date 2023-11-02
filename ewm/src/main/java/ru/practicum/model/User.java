package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "users", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "email", nullable = false, length = 254)
    private String email;
    @Column(name = "name", nullable = false, length = 250)
    private String name;
}
