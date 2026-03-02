package org.personal.pokedle.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pokemon")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pokemon {

    @Id
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String typeOne;
    @Column(nullable = true)
    private String typeTwo;

    private String generation;

    private String color;

    private String stage;

    private String habitat;

    private Double weight;
    private Double height;

}
