package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (breed == null || breed.isBlank()) {
            throw new BreedNotFoundException("Breed name cannot be empty.");
        }

        String normalized = breed.trim().toLowerCase(Locale.ROOT);
        String url = "https://dog.ceo/api/breed/" + normalized + "/list";

        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BreedNotFoundException("Failed to fetch sub-breeds (HTTP " + response.code() + ")");
            }

            String body = response.body().string();
            JSONObject json = new JSONObject(body);
            String status = json.optString("status", "error");

            if (!"success".equalsIgnoreCase(status)) {
                String apiMessage = json.optString("message", "Breed not found");
                throw new BreedNotFoundException(apiMessage + " (" + breed + ")");
            }

            JSONArray array = json.optJSONArray("message");
            List<String> subBreeds = new ArrayList<>();
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    subBreeds.add(array.optString(i));
                }
            }

            return subBreeds;
        } catch (IOException e) {
            throw new BreedNotFoundException("Error fetching sub-breeds for '" + breed + "': " + e.getMessage());
        } catch (RuntimeException e) {
            throw new BreedNotFoundException("Unexpected error parsing API response for '" + breed + "': " + e.getMessage());
        }
    }
}

