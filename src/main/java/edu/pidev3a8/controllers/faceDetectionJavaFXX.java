package edu.pidev3a8.controllers;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import org.bytedeco.opencv.global.*;
import org.opencv.core.Core;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.core.Core;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;

import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.javacpp.IntPointer;


import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;

public class faceDetectionJavaFXX {
    public static void main(String[] args) {
        // Directory with subdirectories of training images
        File trainingDataDir = new File("C:\\Users\\msi\\IdeaProjects\\Tache_Utilisateur\\src\\main\\resources\\Data");
        if (!trainingDataDir.exists() || !trainingDataDir.isDirectory()) {
            System.out.println("Training data directory not found.");
            return;
        }

        // Map to assign a unique integer label to each person (folder name)
        Map<String, Integer> labelMap = new HashMap<>();
        int labelCounter = 1; // starting label

        List<Mat> imagesList = new ArrayList<>();
        List<Integer> labelsList = new ArrayList<>();

        // Load the cascade classifier for face detection.
        CascadeClassifier faceDetector = new CascadeClassifier( "C:\\Users\\msi\\IdeaProjects\\Tache_Utilisateur\\src\\main\\resources\\lbpcascade_frontalface.xml");

        if (faceDetector.empty()) {
            System.err.println("Failed to load cascade classifier. Check the file path.");
            return;
        }

        // Process each subdirectory (each person)
        File[] personDirs = trainingDataDir.listFiles(File::isDirectory);
        if (personDirs == null || personDirs.length == 0) {
            System.out.println("No subdirectories found in training data directory.");
            return;
        }

        for (File personDir : personDirs) {
            String personName = personDir.getName();
            if (!labelMap.containsKey(personName)) {
                labelMap.put(personName, labelCounter++);
            }
            int currentLabel = labelMap.get(personName);

            // Filter image files (jpg, png, jpeg)
            File[] imageFiles = personDir.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".jpg") ||
                            name.toLowerCase().endsWith(".png") ||
                            name.toLowerCase().endsWith(".jpeg"));

            if (imageFiles == null) continue;

            for (File imageFile : imageFiles) {
                Mat image = opencv_imgcodecs.imread(imageFile.getAbsolutePath());
                if (image.empty()) {
                    System.out.println("Could not load image: " + imageFile.getAbsolutePath());
                    continue;
                }
                // Convert to grayscale
                Mat gray = new Mat();
                opencv_imgproc.cvtColor(image, gray, opencv_imgproc.COLOR_BGR2GRAY);

                // Detect faces
                RectVector faces = new RectVector();
                faceDetector.detectMultiScale(gray, faces);

                if (faces.size() > 0) {
                    // Assume the first detected face is the target face
                    Rect faceRect = faces.get(0);
                    Mat face = new Mat(gray, faceRect);
                    // Resize to a consistent size (e.g., 100x100)
                    Mat resizedFace = new Mat();
                    opencv_imgproc.resize(face, resizedFace, new Size(100, 100));

                    imagesList.add(resizedFace);
                    labelsList.add(currentLabel);
                }
            }
        }

        if (imagesList.isEmpty()) {
            System.out.println("No faces were detected in the training data.");
            return;
        }

        // Create a MatVector for training images
        MatVector images = new MatVector(imagesList.size());
        for (int i = 0; i < imagesList.size(); i++) {
            images.put(i, imagesList.get(i));
        }

        // Create a Mat for labels (CV_32SC1)
        int numSamples = labelsList.size();
        Mat labelsMat = new Mat(numSamples, 1, opencv_core.CV_32SC1);
        IntBuffer labelsBuf = labelsMat.createBuffer();
        for (int i = 0; i < numSamples; i++) {
            labelsBuf.put(i, labelsList.get(i));
        }

        // Train the LBPH face recognizer
        LBPHFaceRecognizer recognizer = LBPHFaceRecognizer.create();
        recognizer.train(images, labelsMat);

        // Save the trained model to a file
        recognizer.save("faceModel.xml");
        System.out.println("Training completed. Model saved as faceModel.xml");
        System.out.println("Label mapping: " + labelMap);
    }
}

