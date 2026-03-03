package org.personal.pokedle.controller;

import org.personal.pokedle.model.Pokemon;
import org.personal.pokedle.model.dto.GuessRequest;
import org.personal.pokedle.model.dto.GuessResult;
import org.personal.pokedle.repository.PokemonRepository;
import org.personal.pokedle.service.PokedleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/game")
@CrossOrigin(origins = "*")
public class PokedleController {

    private final PokedleService pokedleService;
    private final PokemonRepository pokemonRepository;

    public PokedleController(PokedleService pokedleService, PokemonRepository pokemonRepository) {
        this.pokedleService = pokedleService;
        this.pokemonRepository = pokemonRepository;
    }

    /**
     * Gets basic info about today's target.
     * Accessible at: GET http://localhost:8080/api/v1/game/daily-info
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
     * Processes a user's guess.
     * Accessible at: POST http://localhost:8080/api/v1/game/guess
     */
    @PostMapping("/guess")
    public ResponseEntity<GuessResult> submitGuess(@RequestBody GuessRequest request) {
        Pokemon target = pokedleService.getDailyPokemon();

        return pokemonRepository.findByNameIgnoreCase(request.pokemonName())
                .map(guessedPkmn -> {
                    GuessResult result = pokedleService.processGuess(guessedPkmn, target);
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}