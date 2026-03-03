package org.personal.pokedle.model.dto;

import org.personal.pokedle.model.enums.LetterStatus;
import java.util.List;

public record GuessResult(
        String guess,
        List<LetterStatus> fingerPrint, // e.g., [CORRECT, ABSENT, PRESENT...]
        List<GuessFeedback> attributeHints,
        boolean isWinner
) {}