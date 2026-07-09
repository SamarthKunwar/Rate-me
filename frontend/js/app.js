let map;
let pois = [];
let poiMarkers = [];
const API_BASE_URL = "http://localhost:8080";
let selectedPoiId = null;
let sessionToken = null;
let currentUser = null;
let authMode = null;
let editingRatingId = null;



function initMap() {
    map = L.map('map').setView([49.2597766, 7.3599692], 13);
    L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);
}

function initTabs() {
    const tabButtons = document.querySelectorAll(".tab-button");

    tabButtons.forEach((clickedButton) => {
        clickedButton.addEventListener("click", () => {
            const selectedTabId = clickedButton.dataset.tab;
            showTab(selectedTabId);
        });
    });
}

function showTab(selectedTabId) {
    const tabButtons = document.querySelectorAll(".tab-button");
    const tabContents = document.querySelectorAll(".tab-content");

    tabButtons.forEach((button) => {
        button.classList.toggle("active", button.dataset.tab === selectedTabId);
    });

    tabContents.forEach((content) => {
        content.classList.remove("active");
    });

    document.getElementById(selectedTabId).classList.add("active");

    if (selectedTabId === "myRatingsTab") {
        loadMyRatings();
    }
}

function getInputValue(id) {
    return document.getElementById(id).value.trim();
}

function setAuthMessage(message, isError = false) {
    const authMessage = document.getElementById("authMessage");
    authMessage.textContent = message;
    authMessage.classList.toggle("error", isError);
}

function setRatingMessage(message, isError = false) {
    const ratingMessage = document.getElementById("ratingMessage");
    ratingMessage.textContent = message;
    ratingMessage.classList.toggle("error", isError);
}

function setMyRatingsMessage(message) {
    const myRatingsList = document.getElementById("myRatingsList");
    myRatingsList.textContent = message;
}

function updateAuthDisplay() {
    const currentUserLabel = document.getElementById("currentUser");
    const logoutButton = document.getElementById("logoutButton");
    const deleteUserButton = document.getElementById("deleteUserButton");

    if (currentUser) {
        currentUserLabel.textContent = "Angemeldet als " + currentUser.username;
        logoutButton.disabled = false;
        deleteUserButton.disabled = false;
        closeAuthModal();
    } else {
        currentUserLabel.textContent = "Nicht angemeldet";
        logoutButton.disabled = true;
        deleteUserButton.disabled = true;
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
    setMyRatingsMessage("Bitte anmelden, um eigene Bewertungen zu sehen.");
    resetRatingForm();
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
        authTitle.textContent = "Anmelden";
        submitAuthButton.textContent = "Anmelden";
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
        setAuthMessage("Anmeldung erfolgreich.");
    } catch (error) {
        console.error(error);
        setAuthMessage("Anmeldung fehlgeschlagen.", true);
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
        setAuthMessage("Abmeldung erfolgreich.");
    } catch (error) {
        console.error(error);
        setAuthMessage("Abmeldung fehlgeschlagen.", true);
    }
}

async function deleteCurrentUser() {
    if (!sessionToken) {
        return;
    }

    const shouldDelete = window.confirm("Account wirklich löschen?");

    if (!shouldDelete) {
        return;
    }

    try {
        const response = await fetch(API_BASE_URL + "/auth/me", {
            method: "DELETE",
            headers: {
                "Authorization": sessionToken
            }
        });

        if (!response.ok) {
            throw new Error("Could not delete user");
        }

        clearAuthentication();
        setAuthMessage("Account gelöscht.");
    } catch (error) {
        console.error(error);
        setAuthMessage("Account konnte nicht gelöscht werden.", true);
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

async function createRating() {
    if (!sessionToken) {
        setRatingMessage("Bitte zuerst anmelden.", true);
        return;
    }

    if (!selectedPoiId) {
        setRatingMessage("Bitte zuerst eine Gastronomie auswählen.", true);
        return;
    }

    const grade = Number(document.getElementById("ratingGrade").value);
    const text = document.getElementById("ratingText").value.trim();
    const imageFile = document.getElementById("ratingImage").files[0];

    if (grade < 1 || grade > 5) {
        setRatingMessage("Bitte Sterne auswählen.", true);
        return;
    }

    if (!text) {
        setRatingMessage("Bitte Bewertungstext eingeben.", true);
        return;
    }

    try {
        const formData = new FormData();
        formData.append("poiId", selectedPoiId);
        formData.append("grade", grade);
        formData.append("text", text);

        if (imageFile) {
            formData.append("image", imageFile);
        }

        const url = editingRatingId
            ? API_BASE_URL + "/ratings/" + editingRatingId
            : API_BASE_URL + "/ratings";

        const response = await fetch(url, {
            method: editingRatingId ? "PUT" : "POST",
            headers: {
                "Authorization": sessionToken
            },
            body: formData
        });

        if (!response.ok) {
            throw new Error("Could not save rating");
        }

        const wasEditing = editingRatingId !== null;
        resetRatingForm();
        setRatingMessage(wasEditing ? "Bewertung aktualisiert." : "Bewertung gespeichert.");
        loadRatingsForPoi(selectedPoiId);
        loadMyRatings();
    } catch (error) {
        console.error(error);
        setRatingMessage("Bewertung konnte nicht gespeichert werden.", true);
    }
}

function resetRatingForm() {
    editingRatingId = null;
    document.getElementById("ratingGrade").value = "";
    document.getElementById("ratingText").value = "";
    document.getElementById("ratingImage").value = "";
    document.getElementById("createRatingButton").textContent = "Bewertung speichern";
    document.getElementById("cancelEditButton").classList.add("hidden");
}

async function loadMyRatings() {
    if (!sessionToken) {
        setMyRatingsMessage("Bitte anmelden, um eigene Bewertungen zu sehen.");
        return;
    }

    try {
        const response = await fetch(API_BASE_URL + "/ratings/me", {
            headers: {
                "Authorization": sessionToken
            }
        });

        if (!response.ok) {
            throw new Error("Could not load my ratings");
        }

        const ratings = await response.json();
        showMyRatings(ratings);
    } catch (error) {
        console.error(error);
        setMyRatingsMessage("Eigene Bewertungen konnten nicht geladen werden.");
    }
}

function showRatings(ratings) {
    const ratingsList = document.getElementById("ratingsList");
    ratingsList.innerHTML = "";

    if (ratings.length === 0) {
        ratingsList.textContent = "Keine Bewertungen vorhanden.";
        return;
    }
    const table = document.createElement("table");
    table.classList.add("ratings-table");

    const tableHead = document.createElement("thead");
    const headRow = document.createElement("tr");
    const headers = ["Datum", "Name", "Kommentar", "Bewertung", "Bild"];

    headers.forEach(headerText => {
        const headerCell = document.createElement("th");
        headerCell.textContent = headerText;
        headRow.appendChild(headerCell);
    });

    tableHead.appendChild(headRow);
    table.appendChild(tableHead);

    const tableBody = document.createElement("tbody");

    ratings.forEach(rating => {
        const row = document.createElement("tr");

        const dateCell = document.createElement("td");
        dateCell.textContent = formatDateTime(rating.createdAt);
        dateCell.classList.add("date-cell");
        row.appendChild(dateCell);

        const usernameCell = document.createElement("td");
        usernameCell.textContent = rating.username;
        row.appendChild(usernameCell);

        const textCell = document.createElement("td");
        textCell.textContent = rating.text;
        row.appendChild(textCell);

        const gradeCell = document.createElement("td");
        gradeCell.innerHTML = formatStars(rating.grade);
        gradeCell.classList.add("stars-cell");
        row.appendChild(gradeCell);

        const imageCell = document.createElement("td");

        if (rating.hasImage) {
            const image = document.createElement("img");
            image.src = API_BASE_URL + "/images/" + rating.imageId;
            image.alt = "Bild zur Bewertung";
            image.classList.add("rating-image");
            imageCell.appendChild(image);
        } else {
            imageCell.textContent = "-";
        }

        row.appendChild(imageCell);
        tableBody.appendChild(row);
    });

    table.appendChild(tableBody);
    ratingsList.appendChild(table);
}

function showMyRatings(ratings) {
    const myRatingsList = document.getElementById("myRatingsList");
    myRatingsList.innerHTML = "";

    if (ratings.length === 0) {
        myRatingsList.textContent = "Du hast noch keine Bewertungen geschrieben.";
        return;
    }

    const table = document.createElement("table");
    table.classList.add("ratings-table");

    const tableHead = document.createElement("thead");
    const headRow = document.createElement("tr");
    const headers = ["Datum", "Ort", "Kommentar", "Bewertung", "Bild", "Aktionen"];

    headers.forEach(headerText => {
        const headerCell = document.createElement("th");
        headerCell.textContent = headerText;
        headRow.appendChild(headerCell);
    });

    tableHead.appendChild(headRow);
    table.appendChild(tableHead);

    const tableBody = document.createElement("tbody");

    ratings.forEach(rating => {
        const row = document.createElement("tr");

        const dateCell = document.createElement("td");
        dateCell.textContent = formatDateTime(rating.createdAt);
        dateCell.classList.add("date-cell");
        row.appendChild(dateCell);

        const poiCell = document.createElement("td");
        poiCell.textContent = rating.poiName || "";
        row.appendChild(poiCell);

        const textCell = document.createElement("td");
        textCell.textContent = rating.text;
        row.appendChild(textCell);

        const gradeCell = document.createElement("td");
        gradeCell.innerHTML = formatStars(rating.grade);
        gradeCell.classList.add("stars-cell");
        row.appendChild(gradeCell);

        const imageCell = document.createElement("td");

        if (rating.hasImage) {
            const image = document.createElement("img");
            image.src = API_BASE_URL + "/images/" + rating.imageId;
            image.alt = "Bild zur Bewertung";
            image.classList.add("rating-image");
            imageCell.appendChild(image);
        } else {
            imageCell.textContent = "-";
        }

        row.appendChild(imageCell);

        const actionsCell = document.createElement("td");

        const editButton = document.createElement("button");
        editButton.type = "button";
        editButton.textContent = "Bearbeiten";
        editButton.addEventListener("click", () => {
            startEditRating(rating);
        });
        actionsCell.appendChild(editButton);

        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.textContent = "Löschen";
        deleteButton.addEventListener("click", () => {
            deleteRating(rating.id);
        });
        actionsCell.appendChild(deleteButton);

        row.appendChild(actionsCell);
        tableBody.appendChild(row);
    });

    table.appendChild(tableBody);
    myRatingsList.appendChild(table);
}

function startEditRating(rating) {
    editingRatingId = rating.id;
    selectedPoiId = rating.poiId;

    showTab("ratingsTab");
    loadPoiDetails(rating.poiId);
    loadRatingsForPoi(rating.poiId);

    document.getElementById("ratingGrade").value = String(rating.grade);
    document.getElementById("ratingText").value = rating.text || "";
    document.getElementById("ratingImage").value = "";
    document.getElementById("createRatingButton").textContent = "Bewertung aktualisieren";
    document.getElementById("cancelEditButton").classList.remove("hidden");
    setRatingMessage("Du bearbeitest deine Bewertung für " + (rating.poiName || "diesen Ort") + ".");
}

async function deleteRating(ratingId) {
    if (!sessionToken) {
        setMyRatingsMessage("Bitte anmelden, um eigene Bewertungen zu sehen.");
        return;
    }

    const shouldDelete = window.confirm("Bewertung wirklich löschen?");

    if (!shouldDelete) {
        return;
    }

    try {
        const response = await fetch(API_BASE_URL + "/ratings/" + ratingId, {
            method: "DELETE",
            headers: {
                "Authorization": sessionToken
            }
        });

        if (!response.ok) {
            throw new Error("Could not delete rating");
        }

        loadMyRatings();

        if (selectedPoiId) {
            loadRatingsForPoi(selectedPoiId);
        }
    } catch (error) {
        console.error(error);
        setMyRatingsMessage("Bewertung konnte nicht gelöscht werden.");
    }
}

function showPoiDetails(poi) {
    const poiName = document.getElementById("poiName");
    const poiDetails = document.getElementById("poiDetails");

    poiName.textContent = poi.name || "Unbenannter Ort";
    poiDetails.innerHTML = "";
    const details = [
        ["Art", poi.amenity],
        ["Küche", poi.cuisine],
        ["Telefon", poi.phone],
        ["Webseite", poi.website],
        ["Öffnungszeiten", formatOpeningHours(poi.openingHours)],
        ["Rollstuhl", poi.wheelchair],
        ["Takeaway", poi.takeaway],
        ["Lieferung", poi.delivery],
        ["Rauchen", poi.smoking],
        ["Außenbereich", poi.outdoorSeating],
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

        if (label === "Öffnungszeiten") {
            detailRow.classList.add("opening-hours-detail");
            description.classList.add("opening-hours-value");
            description.appendChild(createOpeningHoursTable(value));
        } else {
            description.textContent = value;
        }

        detailRow.appendChild(term);
        detailRow.appendChild(description);
        poiDetails.appendChild(detailRow);
    });
}

function formatOpeningHours(openingHours) {
    if (!openingHours) {
        return "";
    }

    return openingHours
        .split(";")
        .map(part => part.trim())
        .filter(Boolean)
        .join("\n");
}

function createOpeningHoursTable(openingHours) {
    const table = document.createElement("table");
    table.classList.add("opening-hours-table");

    openingHours
        .split("\n")
        .map(part => part.trim())
        .filter(Boolean)
        .forEach(part => {
            const row = document.createElement("tr");
            const dayCell = document.createElement("td");
            const timeCell = document.createElement("td");
            const separatorIndex = part.indexOf(" ");

            if (separatorIndex === -1) {
                dayCell.textContent = formatOpeningHoursDay(part);
                timeCell.textContent = "";
            } else {
                dayCell.textContent = formatOpeningHoursDay(part.substring(0, separatorIndex));
                timeCell.textContent = part.substring(separatorIndex + 1);
            }

            row.appendChild(dayCell);
            row.appendChild(timeCell);
            table.appendChild(row);
        });

    return table;
}

function formatOpeningHoursDay(dayText) {
    return dayText.replaceAll("PH", "Feiertage");
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
        marker.bindPopup(poi.name || "Unbenannter Ort");
        marker.on("click", () => {
            selectPoi(poi.id);
        });
        poiMarkers.push(marker);
    });
}

function formatStars(grade) {
    const numericGrade = Number(grade);

    if (numericGrade < 1 || numericGrade > 5) {
        return "-";
    }

    const fullStar = String.fromCharCode(9733);
    const emptyStar = String.fromCharCode(9734);

    return `<span class="star-full">${fullStar.repeat(numericGrade)}</span><span class="star-empty">${emptyStar.repeat(5 - numericGrade)}</span>`;
}

function formatDateTime(value) {
    if (!value) {
        return "";
    }
    const date = new Date(value);
    if (isNaN(date.getTime())) {
        return value;
    }
    return new Intl.DateTimeFormat("de-DE", {
        day: "2-digit",
        month: "2-digit",
        year: "2-digit",
        hour: "2-digit",
        minute: "2-digit"
    }).format(date);
}

document.addEventListener("DOMContentLoaded", () => {
    initMap();
    initTabs();
    updateAuthDisplay();
    document.getElementById("loginButton").addEventListener("click", () => openAuthModal("login"));
    document.getElementById("registerButton").addEventListener("click", () => openAuthModal("register"));
    document.getElementById("logoutButton").addEventListener("click", logoutUser);
    document.getElementById("deleteUserButton").addEventListener("click", deleteCurrentUser);
    document.getElementById("closeAuthButton").addEventListener("click", closeAuthModal);
    document.getElementById("submitAuthButton").addEventListener("click", submitAuthForm);
    document.getElementById("createRatingButton").addEventListener("click", createRating);
    document.getElementById("cancelEditButton").addEventListener("click", resetRatingForm);
    loadPois();
});
