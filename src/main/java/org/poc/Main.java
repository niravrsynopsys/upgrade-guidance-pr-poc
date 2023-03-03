package org.poc;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        try {
            String inputFilePath = "/jsonPayloadDetect.json";
            InputStream inputStream = Main.class.getResourceAsStream(inputFilePath);

            VulnComponentDataset vulnComponentDataset = new VulnComponentDataset();
            String jsonData = new String(inputStream.readAllBytes());
            JSONObject jsonObject = new JSONObject(jsonData);

            // Part 1: Generate vulnerability-component dataset
            JSONObject intermediateResult = vulnComponentDataset.generateVulnComponentDataset(jsonObject);

             System.out.println("\nResult:\n" + intermediateResult.toString(4));

            // Write the intermediate output to a folder
            File targetDir = new File("target/output-files");
            targetDir.mkdirs();
            File outputFile = new File(targetDir, "intermediate-output.json");
            PrintWriter fileWriter = new PrintWriter(outputFile);
            fileWriter.println(intermediateResult.toString(4));
            fileWriter.close();


        } catch (IOException | JSONException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    }
}