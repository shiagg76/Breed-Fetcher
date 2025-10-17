package dogapi;

import java.util.List;

public interface BreedFetcher {
    List<String> getSubBreeds(String breed) throws BreedNotFoundException;

    class BreedNotFoundException extends Exception {
        public BreedNotFoundException(String message) { super(message); }
        public BreedNotFoundException(String message, Throwable cause) { super(message, cause); }
    }
}