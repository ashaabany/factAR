import com.microsoft.azure.cognitiveservices.vision.computervision.*;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;

public class ComputerVision {

    private static final int numOfSearchableTerms = 4;

    public static void main(String[] args) throws Exception {
        String subscriptionKey = System.getenv("COMPUTER_VISION_SUBSCRIPTION_KEY");
        String endpoint = System.getenv("COMPUTER_VISION_ENDPOINT");

        ComputerVisionClient compVisClient = ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
        // END - Create an authenticated Computer Vision client.

        System.out.println("\nAzure Cognitive Services Computer Vision - Java Quickstart Sample");

        // Analyze local and remote images
        HashSet<String> searchTerms = AnalyzeLocalImage(compVisClient);
        System.out.println(searchTerms);
    }

    public static HashSet<String> AnalyzeLocalImage(ComputerVisionClient compVisClient) throws Exception {
        /*
         * Analyze a local image:
         *
         * Set a string variable equal to the path of a local image. The image path
         * below is a relative path.
         */
        String pathToLocalImage = "src\\main\\resources\\man_titties.jpg";

        // This list defines the features to be extracted from the image.
        List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.ADULT);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.CATEGORIES);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.TAGS);
//        featuresToExtractFromLocalImage.add(VisualFeatureTypes.DESCRIPTION);
//        featuresToExtractFromLocalImage.add(VisualFeatureTypes.FACES);
//        featuresToExtractFromLocalImage.add(VisualFeatureTypes.COLOR);
//        featuresToExtractFromLocalImage.add(VisualFeatureTypes.IMAGE_TYPE);

        // Need a byte array for analyzing a local image.
        File rawImage = new File(pathToLocalImage);
        byte[] imageByteArray = null;
        try{
            imageByteArray = Files.readAllBytes(rawImage.toPath());
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // Call the Computer Vision service and tell it to analyze the loaded image.
        ImageAnalysis analysis = compVisClient.computerVision()
                .analyzeImageInStream().withImage(imageByteArray)
                .withVisualFeatures(featuresToExtractFromLocalImage).execute();

        if(!analysis.adult().isAdultContent() && !analysis.adult().isRacyContent()) {
            HashSet<String> searchTerms = new HashSet<>();
            for (Category category : analysis.categories()) {
                if (category.detail() != null && category.detail().landmarks() != null) {
                    for (LandmarksModel landmark : category.detail().landmarks()) {
                        searchTerms.add(landmark.name());
                        if (searchTerms.size()==numOfSearchableTerms)
                            return searchTerms;
                    }
                }
            }
            for (ImageTag tag : analysis.tags()) {
                searchTerms.add(tag.name());
                if (searchTerms.size()==numOfSearchableTerms)
                    return searchTerms;
            }
        } else {
            throw new Exception("Adult content found, please take a picture of a different object.");
        }
        return null;
    }

}