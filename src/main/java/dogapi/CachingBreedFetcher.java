package dogapi;

import java.util.*;

public class CachingBreedFetcher implements BreedFetcher {
    private final BreedFetcher fetcher;
    private final Map<String, List<String>> cache = new HashMap<>();
    private int callsMade = 0;

    public CachingBreedFetcher(BreedFetcher fetcher) {
        if (fetcher == null) {
            throw new IllegalArgumentException("fetcher must not be null");
        }
        this.fetcher = fetcher;
    }

    @Override
    public List<String> getSubBreeds(String breed) {
        if (cache.containsKey(breed)) {
            return new ArrayList<>(cache.get(breed));
        }

        callsMade++;
        try {
            List<String> result = fetcher.getSubBreeds(breed);
            cache.put(breed, new ArrayList<>(result));
            return new ArrayList<>(result);
        } catch (BreedNotFoundException e) {
            rethrow(e);
            return List.of();
        }
    }

    public int getCallsMade() {
        return callsMade;
    }
    // had to do A LOT of googling to try and fix this.
    private static <E extends Throwable> void rethrow(Throwable t) throws E {
        throw (E) t;
    }
}