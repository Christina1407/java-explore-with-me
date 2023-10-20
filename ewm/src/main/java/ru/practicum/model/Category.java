package ru.practicum.model;

import lombok.*;

import javax.persistence.*;

@Builder
@Entity
@Table(name = "categories", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Category {
    @Id
    @Column(name = "category_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 50)
    private String name;
}
