package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (breed == null || breed.isBlank()) {
            throw new BreedNotFoundException("Invalid input: breed cannot be blank.");
        }

        String apiUrl = String.format("https://dog.ceo/api/breed/%s/list", breed.trim().toLowerCase(Locale.ROOT));
        Request request = new Request.Builder().url(apiUrl).build();

        try (Response apiResponse = client.newCall(request).execute()) {
            if (!apiResponse.isSuccessful() || apiResponse.body() == null) {
                throw new BreedNotFoundException("Failed to contact API or retrieve data.");
            }

            String jsonContent = apiResponse.body().string();
            JSONObject jsonResponse = new JSONObject(jsonContent);

            if (!"success".equalsIgnoreCase(jsonResponse.optString("status"))) {
                throw new BreedNotFoundException("Breed not recognized by API: " + breed);
            }

            JSONArray messageArray = jsonResponse.optJSONArray("message");
            List<String> subBreeds = new ArrayList<>();

            if (messageArray != null) {
                for (int i = 0; i < messageArray.length(); i++) {
                    subBreeds.add(messageArray.getString(i));
                }
            }

            return subBreeds;
        } catch (IOException ioEx) {
            throw new BreedNotFoundException("Network issue while fetching sub-breed data.", ioEx);
        } catch (RuntimeException parseEx) {
            throw new BreedNotFoundException("Error processing API response.", parseEx);
        }
    }
}

