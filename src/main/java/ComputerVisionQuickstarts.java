import com.microsoft.azure.cognitiveservices.vision.computervision.*;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class ComputerVisionQuickstarts {

    public static void main(String[] args) {
        String subscriptionKey = System.getenv("COMPUTER_VISION_SUBSCRIPTION_KEY");
        String endpoint = System.getenv("COMPUTER_VISION_ENDPOINT");

        ComputerVisionClient compVisClient = ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
        // END - Create an authenticated Computer Vision client.

        System.out.println("\nAzure Cognitive Services Computer Vision - Java Quickstart Sample");

        // Analyze local and remote images
        AnalyzeLocalImage(compVisClient);
    }

    public static void AnalyzeLocalImage(ComputerVisionClient compVisClient) {
        /*
         * Analyze a local image:
         *
         * Set a string variable equal to the path of a local image. The image path
         * below is a relative path.
         */
        String pathToLocalImage = "src\\main\\resources\\boobs.jpg";

        // This list defines the features to be extracted from the image.
        List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.DESCRIPTION);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.CATEGORIES);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.TAGS);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.FACES);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.ADULT);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.COLOR);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.IMAGE_TYPE);

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

        // Display image captions and confidence values.
        System.out.println("\nCaptions: ");
        for (ImageCaption caption : analysis.description().captions()) {
            System.out.printf("\'%s\' with confidence %f\n", caption.text(), caption.confidence());
        }

        // Display image category names and confidence values.
        System.out.println("\nCategories: ");
        for (Category category : analysis.categories()) {
            System.out.printf("\'%s\' with confidence %f\n", category.name(), category.score());
        }

        // Display image tags and confidence values.
        System.out.println("\nTags: ");
        for (ImageTag tag : analysis.tags()) {
            System.out.printf("\'%s\' with confidence %f\n", tag.name(), tag.confidence());
        }

        // Display whether any adult or racy content was detected and the confidence values.
        System.out.println("\nAdult: ");
        System.out.printf("Is adult content: %b with confidence %f\n", analysis.adult().isAdultContent(),
                analysis.adult().adultScore());
        System.out.printf("Has racy content: %b with confidence %f\n", analysis.adult().isRacyContent(),
                analysis.adult().racyScore());

        // Display any landmarks detected in the image and their locations.
        System.out.println("\nLandmarks: ");
        for (Category category : analysis.categories()) {
            if (category.detail() != null && category.detail().landmarks() != null) {
                for (LandmarksModel landmark : category.detail().landmarks()) {
                    System.out.printf("\'%s\' with confidence %f\n", landmark.name(), landmark.confidence());
                }
            }
        }
    }

}