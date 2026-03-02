package org.personal.pokedle.repository;

import org.personal.pokedle.model.Pokemon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

    // Custom query to grab a random Pokémon from our DB for the "Infinite Mode"
    @Query(value = "SELECT * FROM pokemon ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Pokemon> findRandomPokemon();

    // To check if a guessed name is even a valid Pokémon
    Optional<Pokemon> findByNameIgnoreCase(String name);

    @Query("SELECT p.id FROM Pokemon p ORDER BY p.id ASC")
    List<Long> findAllIds();
}