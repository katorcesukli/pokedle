package org.personal.pokedle.model.dto;

import lombok.Builder;

@Builder
public record GuessFeedback(
        String attributeName,
        String value,
        String status, // CORRECT, WRONG, or PARTIAL (for types)
        String direction // HIGHER, LOWER, or EQUAL (for weight/height)
) {

}
