package org.personal.pokedle.service;

import lombok.Data;
import org.personal.pokedle.model.Pokemon;
import org.personal.pokedle.model.dto.GuessFeedback;
import org.personal.pokedle.model.dto.GuessResult;
import org.personal.pokedle.model.enums.LetterStatus;
import org.personal.pokedle.repository.PokemonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
public class PokedleService {

    private final PokemonRepository pokemonRepository;

    public GuessResult processGuess(Pokemon guess, Pokemon target) {
        boolean isWinner = guess.getId().equals(target.getId());

        // 1. Calculate Letter Hints (The "Wordle" part)
        List<LetterStatus> nameHints = calculateLetterHints(guess.getName(), target.getName());

        // 2. Calculate Attribute Hints (The "Pokemon" part)
        List<GuessFeedback> attributeHints = new ArrayList<>();

        attributeHints.add(compareStrings("Type 1", guess.getTypeOne(), target.getTypeOne()));
        attributeHints.add(compareStrings("Type 2", guess.getTypeTwo(), target.getTypeTwo()));
        attributeHints.add(compareStrings("Generation", guess.getGeneration(), target.getGeneration()));
        attributeHints.add(compareNumbers("Weight", guess.getWeight(), target.getWeight()));
        attributeHints.add(compareNumbers("Height", guess.getHeight(), target.getHeight()));
        attributeHints.add(compareStrings("Color", guess.getColor(), target.getColor()));

        return new GuessResult(guess.getName(), nameHints, attributeHints, isWinner);
    }

    private List<LetterStatus> calculateLetterHints(String guess, String target) {
        List<LetterStatus> statuses = new ArrayList<>();
        // Note: Simple logic assuming equal length for classic Wordle.
        // For Pokemon of diff lengths, you might just compare char-by-char up to the shortest length.
        int len = Math.min(guess.length(), target.length());

        for (int i = 0; i < len; i++) {
            char g = guess.charAt(i);
            if (g == target.charAt(i)) {
                statuses.add(LetterStatus.CORRECT);
            } else if (target.contains(String.valueOf(g))) {
                statuses.add(LetterStatus.PRESENT);
            } else {
                statuses.add(LetterStatus.ABSENT);
            }
        }
        return statuses;
    }

    private GuessFeedback compareStrings(String label, String g, String t) {
        // Handle nulls for Type 2
        String val = (g == null) ? "None" : g;
        String targetVal = (t == null) ? "None" : t;

        return GuessFeedback.builder()
                .attributeName(label)
                .value(val)
                .status(val.equalsIgnoreCase(targetVal) ? "CORRECT" : "WRONG")
                .direction("EQUAL")
                .build();
    }

    private GuessFeedback compareNumbers(String label, Double g, Double t) {
        String direction;
        if (g < t) direction = "HIGHER";
        else if (g > t) direction = "LOWER";
        else direction = "EQUAL";

        return GuessFeedback.builder()
                .attributeName(label)
                .value(g.toString())
                .status(g.equals(t) ? "CORRECT" : "WRONG")
                .direction(direction)
                .build();
    }

    public Pokemon getDailyPokemon() {
        // Fetch only the IDs to keep the memory footprint low
        List<Long> allIds = pokemonRepository.findAllIds();

        if (allIds.isEmpty()) {
            throw new RuntimeException("Pokedex is empty! Did you run the seeder?");
        }

        // Use the date as a seed
        long dayIndex = LocalDate.now().toEpochDay();

        // Use modulo to wrap around the list of available IDs
        int targetIndex = (int) (dayIndex % allIds.size());
        Long targetId = allIds.get(targetIndex);

        return pokemonRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Pokemon not found with ID: " + targetId));
    }
}