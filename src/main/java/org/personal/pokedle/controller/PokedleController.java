package org.personal.pokedle.controller;

import lombok.Data;
import org.personal.pokedle.model.Pokemon;
import org.personal.pokedle.model.dto.GuessRequest;
import org.personal.pokedle.model.dto.GuessResult;
import org.personal.pokedle.repository.PokemonRepository;
import org.personal.pokedle.service.PokedleService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
@Data
public class PokedleController {
    private final PokedleService pokedleService;
    private final PokemonRepository pokemonRepository;

    public PokedleController(PokedleService pokedleService, PokemonRepository pokemonRepository) {
        this.pokedleService = pokedleService;
        this.pokemonRepository = pokemonRepository;
    }

    /**
     * Gets basic info about today's target (like name length)
     * without giving away the answer.
     */
    @GetMapping("/daily-info")
    public ResponseEntity<Map<String, Object>> getDailyInfo() {
        Pokemon target = pokedleService.getDailyPokemon();
        return ResponseEntity.ok(Map.of(
                "nameLength", target.getName().length(),
                "generation", target.getGeneration()
        ));
    }

    /**
     * Processes a user's guess and returns the Wordle-style feedback.
     */
    @PostMapping("/guess")
    public ResponseEntity<GuessResult> submitGuess(@RequestBody GuessRequest request) {
        //Get the fixed target for the day
        Pokemon target = pokedleService.getDailyPokemon();

        //Validate that the guessed Pokemon exists in our SQL DB
        return pokemonRepository.findByNameIgnoreCase(request.pokemonName())
                .map(guessedPkmn -> {
                    //Process the logic via our unified service
                    GuessResult result = pokedleService.processGuess(guessedPkmn, target);
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
