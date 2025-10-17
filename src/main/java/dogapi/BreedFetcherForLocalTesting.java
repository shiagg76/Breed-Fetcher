package dogapi;

import java.util.List;

public class BreedFetcherForLocalTesting implements BreedFetcher {
    private int callCount = 0;

    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        callCount++;
        if ("hound".equalsIgnoreCase(breed)) {
            return List.of("afghan", "basset");
        }
        throw new BreedNotFoundException("Breed not found: " + breed);
    }

    public int getCallCount() { return callCount; }
}
