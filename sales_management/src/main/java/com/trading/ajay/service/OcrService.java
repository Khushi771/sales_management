package com.trading.ajay.service;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.protobuf.ByteString;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@ApplicationScoped
public class OcrService {

    // Pulls the file path from application.properties
    @ConfigProperty(name = "google.vision.credentials.path")
    String credentialsPath;

    public String extractTextFromImage(Path imagePath) throws IOException {
        byte[] imgBytes = Files.readAllBytes(imagePath);
        ByteString imgByteString = ByteString.copyFrom(imgBytes);

        Image img = Image.newBuilder().setContent(imgByteString).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                .addFeatures(feat)
                .setImage(img)
                .build();

        // 1. Explicitly load the JSON file into a GoogleCredentials object
        GoogleCredentials credentials;
        try (FileInputStream serviceAccountStream = new FileInputStream(credentialsPath)) {
            credentials = GoogleCredentials.fromStream(serviceAccountStream);
        }

        // 2. Bind the credentials to the Vision API settings
        ImageAnnotatorSettings settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build();

        // 3. Create the client using the explicit settings
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create(settings)) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(Collections.singletonList(request));
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    throw new IOException("Vision API Error: " + res.getError().getMessage());
                }
                if (res.hasFullTextAnnotation()) {
                    return res.getFullTextAnnotation().getText();
                }
            }
        }
        return "";
    }
}