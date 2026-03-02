package org.personal.pokedle.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user;

    @ManyToOne
    private Pokemon targetPokemon;

    private int attempts;
    private boolean completed;
}
