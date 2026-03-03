const API_BASE = "http://192.168.11.34:8080/api/v1/game";

// 1. On Load: Get Hint
window.onload = async () => {
    try {
        const response = await fetch(`${API_BASE}/daily-info`);
        const data = await response.json();
        document.getElementById("hint-text").innerText =
            `Today's Pokemon has ${data.nameLength} letters and is from ${data.generation}.`;
    } catch (err) {
        console.error("Failed to load game info:", err);
    }
};

// 2. Submit Guess
async function submitGuess() {
    const input = document.getElementById("pokemon-input");
    const name = input.value.trim();

    if (!name) return;

    try {
        const response = await fetch(`${API_BASE}/guess`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ pokemonName: name })
        });

        if (response.status === 404) {
            alert("Pokemon not found in Pokedex!");
            return;
        }

        const result = await response.json();
        renderResult(result);
        input.value = ""; // Clear input
    } catch (err) {
        alert("Error connecting to server.");
    }
}

// 3. Render Result to UI
function renderResult(result) {
    const container = document.getElementById("results-container");
    const guessDiv = document.createElement("div");
    guessDiv.style.border = "1px solid #ccc";
    guessDiv.style.margin = "10px 0";
    guessDiv.style.padding = "10px";

    const nameSpan = document.createElement("h3");
    // Use 'guess' because that's what the JSON says
    nameSpan.innerText = `Guess: ${result.guess}`;
    guessDiv.appendChild(nameSpan);

    const fingerprint = document.createElement("p");
    // Use 'fingerPrint' (case sensitive!)
    fingerprint.innerText = `Letters: ${result.fingerPrint.join(" | ")}`;
    guessDiv.appendChild(fingerprint);

    const attributes = document.createElement("ul");
    // Use 'attributeHints'
    result.attributeHints.forEach(attr => {
        const li = document.createElement("li");
        let directionIcon = "";
        if (attr.direction === "HIGHER") directionIcon = " ↑";
        if (attr.direction === "LOWER") directionIcon = " ↓";

        li.innerText = `${attr.attributeName}: ${attr.value} [${attr.status}]${directionIcon}`;
        attributes.appendChild(li);
    });

    guessDiv.appendChild(attributes);

    if (result.isWinner) {
        const winMsg = document.createElement("h2");
        winMsg.innerText = "🎉 YOU GOT IT! 🎉";
        winMsg.style.color = "green";
        guessDiv.appendChild(winMsg);
    }

    container.prepend(guessDiv);
}