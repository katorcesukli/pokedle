package org.personal.pokedle.config;

import org.personal.pokedle.client.PokeApiClient;
import org.personal.pokedle.model.Pokemon;
import org.personal.pokedle.repository.PokemonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(PokeApiClient client, PokemonRepository repository) {
        return args -> {
            System.out.println("Checking Pokedex status in MySQL...");

            //Get the list of all Names/URLs
            List<Map<String, String>> allPokemon = client.fetchAllPokemonMetadata();
            int newEntries = 0;

            for (Map<String, String> baseData : allPokemon) {
                try {
                    String[] urlParts = baseData.get("url").split("/");
                    Long id = Long.parseLong(urlParts[urlParts.length - 1]);

                    //Count for natdex number
                    if (id > 151) break;

                    //only fetch if we don't have this ID yet
                    if (!repository.existsById(id)) {

                        //Fetch Detailed Data (Requires 2 API calls per Pokemon)
                        Map<String, Object> details = client.fetchPokemonDetails(id);
                        Map<String, Object> species = client.fetchSpeciesDetails(id);

                        //Extract Types
                        List<Map<String, Object>> types = (List<Map<String, Object>>) details.get("types");
                        String t1 = (String) ((Map<String, Object>) types.get(0).get("type")).get("name");
                        String t2 = types.size() > 1 ? (String) ((Map<String, Object>) types.get(1).get("type")).get("name") : null;

                        //Build the Entity
                        Pokemon p = Pokemon.builder()
                                .id(id)
                                .name(baseData.get("name").toUpperCase())
                                .typeOne(t1)
                                .typeTwo(t2)
                                .color((String) ((Map<String, Object>) species.get("color")).get("name"))
                                .habitat(species.get("habitat") != null ?
                                        (String) ((Map<String, Object>) species.get("habitat")).get("name") : "unknown")
                                .generation((String) ((Map<String, Object>) species.get("generation")).get("name"))
                                .weight(((Number) details.get("weight")).doubleValue() / 10.0) // hg to kg
                                .height(((Number) details.get("height")).doubleValue() / 10.0) // dm to m
                                .stage(species.get("evolves_from_species") == null ? "Basic" : "Evolved")
                                .build();

                        repository.save(p);
                        newEntries++;

                        //Pause for 300ms to avoid 'Connection Reset'
                        Thread.sleep(300);

                        if (id % 10 == 0) {
                            System.out.println("Caught and saved: " + p.getName() + " (ID: " + id + ")");
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Error processing " + baseData.get("name") + ": " + e.getMessage());
                }
            }

            if (newEntries > 0) {
                System.out.println("Database Seeding Complete! Added " + newEntries + " new Pokémon.");
            } else {
                System.out.println("Pokedex is already up to date.");
            }
        };
    }
}