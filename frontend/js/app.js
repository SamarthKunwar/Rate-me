let map;
let pois = [];
let poiMarkers = [];
const API_BASE_URL = "http://localhost:8080";
let selectedPoiId = null;
let sessionToken = null;
let currentUser = null;
let authMode = null;



function initMap() {
    map = L.map('map').setView([49.2597766, 7.3599692], 12);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);
}

function initTabs() {
    const tabButtons = document.querySelectorAll(".tab-button");
    const tabContents = document.querySelectorAll(".tab-content");

    tabButtons.forEach((clickedButton) => {
        clickedButton.addEventListener("click", () => {
            const selectedTabId = clickedButton.dataset.tab;

            tabButtons.forEach((button) => {
                button.classList.remove("active");
            });

            tabContents.forEach((content) => {
                content.classList.remove("active");
            });

            clickedButton.classList.add("active");
            document.getElementById(selectedTabId).classList.add("active");
        });
    });
}

function getInputValue(id) {
    return document.getElementById(id).value.trim();
}

function setAuthMessage(message, isError = false) {
    const authMessage = document.getElementById("authMessage");
    authMessage.textContent = message;
    authMessage.classList.toggle("error", isError);
}

function updateAuthDisplay() {
    const currentUserLabel = document.getElementById("currentUser");
    const logoutButton = document.getElementById("logoutButton");

    if (currentUser) {
        currentUserLabel.textContent = "Angemeldet als " + currentUser.username;
        logoutButton.disabled = false;
        closeAuthModal();
    } else {
        currentUserLabel.textContent = "Nicht angemeldet";
        logoutButton.disabled = true;
    }
}

function saveAuthentication(loginResponse) {
    sessionToken = loginResponse.sessionToken;
    currentUser = loginResponse.user;
    updateAuthDisplay();
}

function clearAuthentication() {
    sessionToken = null;
    currentUser = null;
    updateAuthDisplay();
}

function openAuthModal(mode) {
    authMode = mode;
    const authModal = document.getElementById("authModal");
    const loginFields = document.getElementById("loginFields");
    const registerFields = document.getElementById("registerFields");
    const authTitle = document.getElementById("authTitle");
    const submitAuthButton = document.getElementById("submitAuthButton");

    authModal.classList.remove("hidden");
    loginFields.classList.remove("hidden");
    setAuthMessage("");

    if (mode === "register") {
        authTitle.textContent = "Registrieren";
        submitAuthButton.textContent = "Registrieren";
        registerFields.classList.remove("hidden");
    } else {
        authTitle.textContent = "Login";
        submitAuthButton.textContent = "Login";
        registerFields.classList.add("hidden");
    }

    document.getElementById("authUsername").focus();
}

function closeAuthModal() {
    const authModal = document.getElementById("authModal");
    authModal.classList.add("hidden");
    authMode = null;
    setAuthMessage("");
}

async function submitAuthForm() {
    if (authMode === "register") {
        await registerUser();
    } else {
        await loginUser();
    }
}

async function loginUser() {
    const username = getInputValue("authUsername");
    const password = getInputValue("authPassword");

    if (!username || !password) {
        setAuthMessage("Bitte Benutzername und Passwort eingeben.", true);
        return;
    }

    try {
        const response = await fetch(API_BASE_URL + "/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        if (!response.ok) {
            throw new Error("Login failed");
        }

        const loginResponse = await response.json();
        saveAuthentication(loginResponse);
        setAuthMessage("Login erfolgreich.");
    } catch (error) {
        console.error(error);
        setAuthMessage("Login fehlgeschlagen.", true);
    }
}

async function registerUser() {
    const username = getInputValue("authUsername");
    const password = getInputValue("authPassword");
    const email = getInputValue("authEmail");

    if (!username || !password || !email) {
        setAuthMessage("Bitte Benutzername, Passwort und E-Mail eingeben.", true);
        return;
    }

    try {
        const response = await fetch(API_BASE_URL + "/auth/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password,
                email: email,
                firstname: getInputValue("authFirstname"),
                lastname: getInputValue("authLastname"),
                street: getInputValue("authStreet"),
                streetNr: getInputValue("authStreetNr"),
                zip: getInputValue("authZip"),
                city: getInputValue("authCity")
            })
        });

        if (!response.ok) {
            throw new Error("Registration failed");
        }

        const loginResponse = await response.json();
        saveAuthentication(loginResponse);
        setAuthMessage("Registrierung erfolgreich.");
    } catch (error) {
        console.error(error);
        setAuthMessage("Registrierung fehlgeschlagen.", true);
    }
}

async function logoutUser() {
    if (!sessionToken) {
        return;
    }

    try {
        const response = await fetch(API_BASE_URL + "/auth/logout", {
            method: "POST",
            headers: {
                "Authorization": sessionToken
            }
        });

        if (!response.ok) {
            throw new Error("Logout failed");
        }

        clearAuthentication();
        setAuthMessage("Logout erfolgreich.");
    } catch (error) {
        console.error(error);
        setAuthMessage("Logout fehlgeschlagen.", true);
    }
}

async function loadPois() {
    try {
        const response = await fetch(API_BASE_URL + "/pois");

        if (!response.ok) {
            throw new Error("Could not load POIs");
        }

        pois = await response.json();
        renderPoiMarkers();
    } catch (error) {
        console.error(error);
    }
}

function selectPoi(poiId) {
    selectedPoiId = poiId;
    loadPoiDetails(poiId);
    loadRatingsForPoi(poiId);
}

async function loadRatingsForPoi(poiId) {
    try {
        const response = await fetch(API_BASE_URL + "/ratings/poi/" + poiId);
        if (!response.ok) {
            throw new Error("Could not load ratings for POI");
        }
        const ratings = await response.json();
        showRatings(ratings);
    } catch (error) {
        console.error(error);
    }
}

function showRatings(ratings) {
    const ratingsList = document.getElementById("ratingsList");
    ratingsList.innerHTML = "";

    if (ratings.length === 0) {
        ratingsList.textContent = "Keine Bewertungen vorhanden.";
        return;
    }

    ratings.forEach(rating => {
        const ratingElement = document.createElement("article");
        ratingElement.classList.add("rating-item");
        const heading = document.createElement("h3");
        heading.textContent = rating.username + " - " + rating.grade + "/5 Sterne";

        const text = document.createElement("p");
        text.textContent = rating.text;

        const date = document.createElement("small");
        date.textContent = rating.createdAt || "";

        if (rating.hasImage) {
            const image = document.createElement("img");
            image.src = API_BASE_URL + "/images/" + rating.imageId;
            image.alt = "Bild zur Bewertung";
            ratingElement.appendChild(image);
        }

        ratingElement.appendChild(heading);
        ratingElement.appendChild(text);
        ratingElement.appendChild(date);
        ratingsList.appendChild(ratingElement);
    });
}

function showPoiDetails(poi) {
    const poiName = document.getElementById("poiName");
    const poiDetails = document.getElementById("poiDetails");

    poiName.textContent = poi.name || "Unbenannte Kneipe";
    poiDetails.innerHTML = "";
    const details = [
        ["Art", poi.amenity],
        ["Kueche", poi.cuisine],
        ["Telefon", poi.phone],
        ["Webseite", poi.website],
        ["Oeffnungszeiten", poi.openingHours],
        ["Rollstuhl", poi.wheelchair],
        ["Takeaway", poi.takeaway],
        ["Lieferung", poi.delivery],
        ["Rauchen", poi.smoking],
        ["Aussenbereich", poi.outdoorSeating],
        ["Reservierung", poi.reservation],
        ["Adresse", formatAddress(poi)]
    ];
    
    details.forEach(([label, value]) => {
        if (!value) {
            return;
        }
        const detailRow = document.createElement("div");
        const term = document.createElement("dt");
        const description = document.createElement("dd");

        term.textContent = label;
        description.textContent = value;
        detailRow.appendChild(term);
        detailRow.appendChild(description);
        poiDetails.appendChild(detailRow);
    });
}

function formatAddress(poi) {
    const street = [poi.addrStreet, poi.addrHousenumber]
        .filter(Boolean)
        .join(" ");

    const city = [poi.addrPostcode, poi.addrCity]
        .filter(Boolean)
        .join(" ");

    return [street, city]
        .filter(Boolean)
        .join(", ");
}

async function loadPoiDetails(poiId) {
    try {
        const response = await fetch(API_BASE_URL + "/pois/" + poiId);
        if (!response.ok) {
            throw new Error("Could not load POI details");
        }
        const poi = await response.json();
        showPoiDetails(poi);
    } catch (error) {
        console.error(error);
    }
}

function renderPoiMarkers() {
    poiMarkers.forEach(marker => {
        marker.remove();
    });
    poiMarkers = [];

    pois.forEach(poi => {
        if (poi.lat == null || poi.lon == null) {
            return;
        }
        const marker = L.marker([poi.lat, poi.lon]);
        marker.addTo(map);
        marker.bindPopup(poi.name || "Unbenannte Kneipe");
        marker.on("click", () => {
            selectPoi(poi.id);
        });
        poiMarkers.push(marker);
    });
}

document.addEventListener("DOMContentLoaded", () => {
    initMap();
    initTabs();
    updateAuthDisplay();
    document.getElementById("loginButton").addEventListener("click", () => openAuthModal("login"));
    document.getElementById("registerButton").addEventListener("click", () => openAuthModal("register"));
    document.getElementById("logoutButton").addEventListener("click", logoutUser);
    document.getElementById("closeAuthButton").addEventListener("click", closeAuthModal);
    document.getElementById("submitAuthButton").addEventListener("click", submitAuthForm);
    loadPois();
});
