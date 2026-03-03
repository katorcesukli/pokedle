package org.personal.pokedle.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Map;

@Component
public class PokeApiClient {
    private final WebClient webClient;


    public PokeApiClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://pokeapi.co/api/v2/")
                .exchangeStrategies(org.springframework.web.reactive.function.client.ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024)) // 16MB
                        .build()).build();
    }

    public List<Map<String, String>> fetchAllPokemonMetadata() {
        return webClient.get()
                .uri("pokemon?limit=151")
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (List<Map<String, String>>) response.get("results"))
                .block();
    }

    public Map<String, Object> fetchPokemonDetails(Long id) {
        return webClient.get()
                .uri("pokemon/{id}", id)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public Map<String, Object> fetchSpeciesDetails(Long id) {
        return webClient.get()
                .uri("pokemon-species/{id}", id)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }


}