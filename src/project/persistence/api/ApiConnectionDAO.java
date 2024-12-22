package project.persistence.api;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import project.business.entities.adventure.Adventure;
import project.business.entities.character.Character;
import project.business.entities.monster.Monster;
import project.persistence.exceptions.ApiServerException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class with the responsibility of reading and posting Strings to an HTTPS API. Due to a misconfiguration
 * in the SimpleRPG API, it's set up to ignore SSL certificates, connecting to any server.
 * Be aware that this should NOT be used in real production environments, as verifying certificates is a
 * key part of ensuring security in the context of Internet communications
 */
public final class ApiConnectionDAO implements ApiConnection {
    private final HttpClient client;
    private final Gson gson;
    private final String API_ADVENTURES_URL;
    private final String API_CHARACTERS_URL;
    private final String API_MONSTERS_URL;

    /**
     * Default constructor, where the client used for HTTPS communication is set up
     *
     * @throws ApiServerException If your computer doesn't support SSL at all. If you get this exception when calling the
     *                            constructor, contact the OOPD teachers.
     */
    public ApiConnectionDAO() throws ApiServerException {
        // We set up the URLs we will use to communicate with the API
        this.API_ADVENTURES_URL = "https://balandrau.salle.url.edu/dpoo/S1-Project_52/adventures";
        this.API_CHARACTERS_URL = "https://balandrau.salle.url.edu/dpoo/S1-Project_52/characters";
        this.API_MONSTERS_URL = "https://balandrau.salle.url.edu/dpoo/shared/monsters";

        // We use a Gson object to parse JSON responses into Java objects
        this.gson = new Gson();

        // We set up the HTTPClient we will (re)use across requests, with a custom *INSECURE* SSL context
        try {
            client = HttpClient.newBuilder().sslContext(insecureContext()).build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            // Exceptions are simplified for any classes that need to catch them
            throw new ApiServerException();
        }
    }

    /**
     * Method that reads the contents from a URL using the HTTPS protocol. Specifically, a GET request is sent.
     * Any parameters should be included in the URL.
     *
     * @param url A String representation of the URL to read from, which will be assumed to use HTTP/HTTPS.
     * @return The contents of the URL represented as text.
     * @throws ApiServerException If the URL is malformed or the server can't be reached.
     */
    public String getFromUrl(String url) throws ApiServerException {
        try {
            // Define the request
            // The default method is GET, so we don't need to specify it (but we could do so by calling .GET() before .build()
            // The HttpRequest.Builder pattern offers a ton of customization for the request (headers, body, HTTP version...)
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).build();

            // We use the default BodyHandler for Strings (so we can get the body of the response as a String)
            // Note we could also send the request asynchronously, but things would escalate in terms of coding complexity
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Just return the body
            return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            // Exceptions are simplified for any classes that need to catch them
            throw new ApiServerException();
        }
    }


    /**
     * Retrieves a list of adventures from the API server by sending a GET request to the Adventures endpoint.
     * Returns a list of AdventureDTO objects.
     *
     * @return A List of AdventureDTO objects.
     * @throws RuntimeException if an ApiServerException is thrown while retrieving the adventures.
     */
    public List<Adventure> getAdventuresFromApi() {
        try {
            return this.gson.fromJson(this.getFromUrl(this.API_ADVENTURES_URL), new TypeToken<List<Adventure>>(){}.getType());
        } catch (ApiServerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a list of characters from the API server by sending a GET request to the Characters endpoint.
     * Returns a list of CharacterDTO objects.
     *
     * @return A List of CharacterDTO objects.
     * @throws RuntimeException if an ApiServerException is thrown while retrieving the characters.
     */
    @Override
    public List<Character> getCharactersFromApi() {
        try {
            return this.gson.fromJson(this.getFromUrl(this.API_CHARACTERS_URL), new TypeToken<List<Character>>(){}.getType());
        } catch (ApiServerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Character> getCharacterByPlayerFromApi(String playerName) {
        try {
            return this.gson.fromJson(this.getFromUrl(this.API_CHARACTERS_URL + "?player=" + playerName), new TypeToken<List<Character>>(){}.getType());
        } catch (ApiServerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCharacterFromApi(int position) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(this.API_CHARACTERS_URL + "/" + position)).DELETE().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAdventureToApi(List<LinkedHashMap<String, Integer>> encounters, String adventureName) throws ApiServerException {
        try {
            JSONObject adventure = new JSONObject();
            adventure.put("name", adventureName);
            adventure.put("numberOfEncounters", encounters.size());
            JSONArray encountersArray = new JSONArray();

            int encounterNumber = 1;
            for (LinkedHashMap<String, Integer> encounter : encounters) {
                if (encounter != null && !encounter.isEmpty()) {
                    JSONObject encounterObject = new JSONObject();
                    encounterObject.put("number", encounterNumber);

                    JSONArray monsterArray = new JSONArray();
                    for (Map.Entry<String, Integer> monsterEntry : encounter.entrySet()) {
                        Pattern pattern = Pattern.compile("^(.*)\\s\\(([^)]+)\\)$");
                        Matcher matcher = pattern.matcher(monsterEntry.getKey());
                        if (matcher.matches()) {
                            JSONObject monsterObject = new JSONObject();
                            monsterObject.put("name", matcher.group(1).trim());
                            monsterObject.put("challenge", matcher.group(2).trim());
                            monsterObject.put("quantity", monsterEntry.getValue());
                            monsterArray.put(monsterObject);
                        }
                    }
                    encounterObject.put("monsters", monsterArray);
                    encountersArray.put(encounterObject);
                }
                encounterNumber++;
            }
            adventure.put("encounters", encountersArray);

            String adventureJson = adventure.toString();
            this.postToUrl(this.API_ADVENTURES_URL, adventureJson);
        } catch (ApiServerException e) {
            throw new ApiServerException();
        }
    }

    @Override
    public Adventure getAdventureByID(int adventureIndex) {
        try {
            String adventureJson = this.getFromUrl(this.API_ADVENTURES_URL + "/" + adventureIndex);
            JsonObject adventureObject = JsonParser.parseString(adventureJson).getAsJsonObject();

            String name = adventureObject.get("name").getAsString();
            int numberOfEncounters = adventureObject.get("numberOfEncounters").getAsInt();
            JsonArray encounters = adventureObject.getAsJsonArray("encounters");
            Adventure adventure = new Adventure(name, numberOfEncounters);

            for (int i = 0; i < encounters.size(); i++) {
                JsonObject encounter = encounters.get(i).getAsJsonObject();
                JsonArray monsters = encounter.getAsJsonArray("monsters");

                for (int j = 0; j < monsters.size(); j++) {
                    JsonObject monsterObject = monsters.get(j).getAsJsonObject();
                    String monsterName = monsterObject.get("name").getAsString();
                    String challenge = monsterObject.get("challenge").getAsString();
                    int quantity = monsterObject.get("quantity").getAsInt();
                    adventure.insertNewMonster(monsterName, challenge, quantity, encounter.get("number").getAsInt());
                }
            }

            return adventure;
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving the adventure", e);
        }
    }

    @Override
    public Character getCharacterByID(int whichCharacter) {
        try {
            JsonObject characterObject = JsonParser.parseString(this.getFromUrl(this.API_CHARACTERS_URL + "/" + whichCharacter)).getAsJsonObject();

            return new Character(characterObject.get("name").getAsString(),
                                 characterObject.get("player").getAsString(),
                                 characterObject.get("xp").getAsInt(),
                                 characterObject.get("body").getAsInt(),
                                 characterObject.get("mind").getAsInt(),
                                 characterObject.get("spirit").getAsInt(),
                                 characterObject.get("class").getAsString());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving the character", e);
        }
    }

    @Override
    public List<String> getMonsterNamesInEncounter(int encounterIndex, String adventureName) {
        List<String> monsterNames = new ArrayList<>();

        try {
            String apiUrl = this.API_ADVENTURES_URL + "?name=" + URLEncoder.encode(adventureName, StandardCharsets.UTF_8);

            String jsonAdventuresResponse = this.getFromUrl(apiUrl);

            JsonArray adventuresArray = JsonParser.parseString(jsonAdventuresResponse).getAsJsonArray();

            JsonObject selectedAdventure = null;
            for (JsonElement adventureElement : adventuresArray) {
                JsonObject adventureObject = adventureElement.getAsJsonObject();
                if (adventureObject.get("name").getAsString().equalsIgnoreCase(adventureName)) {
                    selectedAdventure = adventureObject;
                    break;
                }
            }

            if (selectedAdventure != null) {
                JsonArray encountersArray = selectedAdventure.getAsJsonArray("encounters");
                if (encounterIndex > 0 && encounterIndex <= encountersArray.size()) {
                    JsonObject selectedEncounter = encountersArray.get(encounterIndex - 1).getAsJsonObject();
                    JsonArray monsterArray = selectedEncounter.getAsJsonArray("monsters");

                    for (JsonElement monsterElement : monsterArray) {
                        JsonObject monster = monsterElement.getAsJsonObject();
                        String monsterName = monster.get("name").getAsString();
                        int quantity = monster.get("quantity").getAsInt();
                        monsterNames.add("\t- " + quantity + "x " + monsterName);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving the monsters", e);
        }
        return monsterNames;
    }

    @Override
    public void addCharacterToApi(Character updatedCharacter) {
        try {
            JsonObject characterObject = new JsonObject();
            characterObject.addProperty("name", updatedCharacter.name());
            characterObject.addProperty("player", updatedCharacter.player());
            characterObject.addProperty("xp", updatedCharacter.xp());
            characterObject.addProperty("body", updatedCharacter.body());
            characterObject.addProperty("mind", updatedCharacter.mind());
            characterObject.addProperty("spirit", updatedCharacter.spirit());
            characterObject.addProperty("class", updatedCharacter.clas());

            String characterJson = characterObject.toString();
            this.postToUrl(this.API_CHARACTERS_URL, characterJson);
        } catch (Exception e) {
            throw new RuntimeException("Error adding the character", e);
        }
    }

    /**
     * Retrieves a list of monsters from the API server by sending a GET request to the Monsters endpoint.
     * Returns a list of MonsterDTO objects.
     *
     * @return A List of MonsterDTO objects.
     * @throws RuntimeException if an ApiServerException is thrown while retrieving the monsters.
     */
    public @NotNull List<Monster> getMonstersFromApi() {
        try {
            String monstersJson = getFromUrl(this.API_MONSTERS_URL);
            Type monstersListType = new TypeToken<List<Map<String, String>>>(){}.getType();
            List<Map<String, String>> monstersMapList = this.gson.fromJson(monstersJson, monstersListType);
            List<Monster> monsters = new ArrayList<>();
            for (Map<String, String> monsterMap : monstersMapList) {
                monsters.add(new Monster(monsterMap.get("name"), monsterMap.get("challenge"), Integer.parseInt(monsterMap.get("experience")), Integer.parseInt(monsterMap.get("hitPoints")), Integer.parseInt(monsterMap.get("initiative")), monsterMap.get("damageDice"), monsterMap.get("damageType")));
            }
            return monsters;
        } catch (ApiServerException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Method that posts contents to a URL using the HTTPS protocol. Specifically, a POST request is sent.
     * The request body is set to the corresponding parameter, and the response body is returned just in case.
     *
     * @param url  A String representation of the URL to post to, which will be assumed to use HTTP/HTTPS.
     * @param body The content to post, which will be sent to the server in the request body.
     * @return The contents of the response, in case the server sends anything back after posting the content.
     * @throws ApiServerException If the URL is malformed or the server can't be reached.
     */
    public String postToUrl(String url, String body) throws ApiServerException {
        try {
            // Define the request
            // In this case, we have to use the .POST() and .headers() methods to define what we want (to send a string containing JSON data)
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).headers("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();

            // We could use a BodyHandler that discards the response body, but here we return the API's response
            // Note we could also send the request asynchronously, but things would escalate in terms of coding complexity
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            // Exceptions are simplified for any classes that need to catch them
            throw new ApiServerException();
        }
    }


    /**
     * Method that removes the contents from a URL using the HTTPS protocol. Specifically, a DELETE request is sent.
     * Any parameters should be included in the URL.
     *
     * @param url A String representation of the URL to delete from, which will be assumed to use HTTP/HTTPS.
     * @return The contents of the response, in case the server sends anything back after deleting the content.
     * @throws ApiServerException If the URL is malformed or the server can't be reached.
     */
    public String deleteFromUrl(String url) throws ApiServerException {
        try {
            // Define the request
            // The default method is GET, so we don't need to specify it (but we could do so by calling .GET() before .build()
            // The HttpRequest.Builder pattern offers a ton of customization for the request (headers, body, HTTP version...)
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).DELETE().build();

            // We use the default BodyHandler for Strings (so we can get the body of the response as a String)
            // Note we could also send the request asynchronously, but things would escalate in terms of coding complexity
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            // Exceptions are simplified for any classes that need to catch them
            throw new ApiServerException();
        }
    }

    /**
     * Helper function that sets up a SSLContext designed to ignore certificates, accepting anything by default
     * NOT TO BE USED IN REAL PRODUCTION ENVIRONMENTS
     *
     * @return An instance of the SSLContext class, which manages SSL verifications, configured to accept even misconfigured certificates
     */
    private SSLContext insecureContext() throws NoSuchAlgorithmException, KeyManagementException {
        // We set up a TrustManager that accepts every certificate by default
        TrustManager[] insecureTrustManager = new TrustManager[]{new X509TrustManager() {
            // By not throwing any exceptions in these methods we're accepting everything
            public void checkClientTrusted(X509Certificate[] xcs, String string) {
            }

            public void checkServerTrusted(X509Certificate[] xcs, String string) {
            }

            // This doesn't affect our use case, so we just return an empty array
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};
        // We set up the SSLContext with the over-accepting TrustManager
        SSLContext sc = SSLContext.getInstance("ssl");
        sc.init(null, insecureTrustManager, null);
        return sc;
    }

    @Override
    public boolean checkIfApiServerIsUp() {
        try {
            // Define the request
            // The default method is GET, so we don't need to specify it (but we could do so by calling .GET() before .build()
            // The HttpRequest.Builder pattern offers a ton of customization for the request (headers, body, HTTP version...)
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(this.API_MONSTERS_URL)).build();

            // We use the default BodyHandler for Strings (so we can get the body of the response as a String)
            // Note we could also send the request asynchronously, but things would escalate in terms of coding complexity
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            // Exceptions are simplified for any classes that need to catch them
            return false;
        }
    }

    @Override
    public void createCharacter(Character character) {
        try {
            String characterJson = this.gson.toJson(character);
            postToUrl(this.API_CHARACTERS_URL, characterJson);
        } catch (ApiServerException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteAllCharacters() throws ApiServerException {
        try {
            List<Character> characters = getCharactersFromApi();
            for (Character ignored : characters) {
                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(new URI(API_CHARACTERS_URL + "/0"))
                        .DELETE()
                        .build();
                client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
            }
        } catch (Exception e) {
            throw new ApiServerException();
        }
    }
}