package com.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.net.ssl.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class RestAPICall {

    static FileWriter deliveredWriter;
    static FileWriter notDeliveredWriter;
    static FileWriter uncheckedWriter;

    public static void main(String[] args) throws Exception {
        doTrustToCertificates(); // Trust all certificates method

        String csvFile = "C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\ULSS_Program\\input_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv"; // Path to your input CSV file
        String line;
        String cvsSplitBy = ",";

        int deliveredCount = 0;
        int notDeliveredCount = 0;
        int uncheckedCount = 0;

        // Set to keep track of processed tracking numbers
        Set<String> processedTrackingNumbers = new HashSet<>();

        // Initialize file writers
        deliveredWriter = new FileWriter("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\ULSS_Program\\delivered_tracking_numbers_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv");
        notDeliveredWriter = new FileWriter("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\ULSS_Program\\not_delivered_tracking_numbers_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv");
        uncheckedWriter = new FileWriter("C:\\Users\\jpradhan\\OneDrive - Williams-Sonoma Inc\\Documents\\workspace\\ULSS_Program\\unchecked_tracking_numbers_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv");

        // Read headers from input.csv
        BufferedReader headerReader = new BufferedReader(new FileReader(csvFile));
        String headerLine = headerReader.readLine(); // Read header line
        headerReader.close();
        
        List<String> headers = Arrays.asList(headerLine.split(cvsSplitBy));

        // Write headers to CSV files
        deliveredWriter.append(String.join(",", headers) + ",STATUS_DATE").append("\n");
        notDeliveredWriter.append(String.join(",", headers)).append("\n");
        uncheckedWriter.append(String.join(",", headers)).append("\n");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            br.readLine(); // Skip header line if present
            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);
                if (data.length >= 8) { 
                    String trackingNumber = data[6].trim(); 

                    // Check if tracking number has already been processed
                    if (processedTrackingNumbers.contains(trackingNumber)) {
                        continue; 
                    }

                    processedTrackingNumbers.add(trackingNumber); 

                    // Construct JSON input string
                    String jsonInputString = "{\"data\":{\"carrier\":\"UPS\",\"trackingNumber\":\"" + trackingNumber + "\"}}";

                    // Construct URL and make HTTP request
                    String urlString = "https://gateway-supplychain.services.wsgc.com/gateway/process/shipments/tracking/details";
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();
                    System.out.println("HTTP Response Code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        try (Scanner scanner = new Scanner(connection.getInputStream(), StandardCharsets.UTF_8.name())) {
                            String response = scanner.useDelimiter("\\A").next();
                            JSONObject jsonobj = new JSONObject(response);

                            // Convert JSON to XML
                            StringBuilder xmlString = new StringBuilder();
                            xmlString.append(XML.toString(jsonobj));
                            System.out.println("Response XML for tracking number " + trackingNumber + ":");
                            System.out.println(xmlString);

                            // Parse XML to get deliveryDate and activityTime from trackingDetails
                            String xml = xmlString.toString();
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            Document doc = builder.parse(new InputSource(new StringReader(xml)));

                            if (doc != null) {
                                NodeList nl = doc.getElementsByTagName("description");
                                if (nl != null && nl.getLength() > 0) {
                                    Element eleDescription = (Element) nl.item(0);
                                    String description = eleDescription.getTextContent().trim(); // Trim to handle whitespace

                                    if (description.isEmpty()) {
                                        // Handle case where description is empty
                                        System.out.println("Tracking Number: " + trackingNumber + " Status: Description is empty");
                                        writeNotDelivered(line); // Write to not delivered CSV
                                        notDeliveredCount++;
                                    } else {
                                        System.out.println("Tracking Number: " + trackingNumber + " Status: " + description);

                                        if (description.contains("DELIVERED")) {
                                            // Extract deliveryDate from trackingSummary
                                            NodeList deliveryDateNodeList = doc.getElementsByTagName("deliveryDate");
                                            String deliveryDate = (deliveryDateNodeList.getLength() > 0) ? deliveryDateNodeList.item(0).getTextContent().trim() : "";
                                            System.out.println("Delivery Date: " + deliveryDate);

                                            // Extract activityTime from the first trackingDetails
                                            NodeList activityTimeNodeList = doc.getElementsByTagName("activityTime");
                                            String activityTime = (activityTimeNodeList.getLength() > 0) ? activityTimeNodeList.item(0).getTextContent().trim() : "";
                                            System.out.println("Activity Time: " + activityTime);

                                            // Write to delivered CSV with activityTime appended
                                            writeDelivered(line + "," + activityTime); // Write to delivered CSV
                                            deliveredCount++;
                                        } else {
                                            // Handle case for NOT DELIVERED
                                            writeNotDelivered(line); // Write to not delivered CSV
                                            notDeliveredCount++;
                                        }
                                    }
                                } else {
                                    // Handle case where <description> element is missing
                                    System.out.println("No <description> element found in XML for tracking number " + trackingNumber);
                                    writeNotDelivered(line); // Write to not delivered CSV
                                    notDeliveredCount++;
                                }
                            } else {
                                System.out.println("Document object (doc) is null after parsing XML.");
                                writeNotDelivered(line); // Write to not delivered CSV
                                notDeliveredCount++;
                            }
                        }
                    } else {
                        System.out.println("HTTP request failed with error code: " + responseCode);
                        uncheckedWriter.write(line + "\n"); // Write the line to unchecked CSV
                        uncheckedCount++; // Increment count for unchecked tracking numbers
                    }

                    connection.disconnect();
                }
            }
            System.out.println("***************************************************************************************************");
            System.out.println("***************************************************************************************************");
            System.out.println("Number of DELIVERED statuses: " + deliveredCount);
            System.out.println("Number of statuses NOT DELIVERED: " + notDeliveredCount);
            System.out.println("Number of tracking numbers that could not be checked: " + uncheckedCount);
            System.out.println("List of orders delivered at UPS have been written to:: delivered_tracking_numbers_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv");
            System.out.println("List of orders not delivered at UPS have been written to:: not_delivered_tracking_numbers_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv");
            System.out.println("List of orders whose tracking numbers not available at UPS have been written to:: unchecked_tracking_numbers_" + new SimpleDateFormat("ddMMyyyy").format(new Date()) + ".csv");
            System.out.println("***************************************************************************************************");
            System.out.println("***************************************************************************************************");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            deliveredWriter.close();
            notDeliveredWriter.close();
            uncheckedWriter.close();
        }
    }

    // Method to write to delivered CSV
    private static void writeDelivered(String line) throws IOException {
        deliveredWriter.write(line + "\n");
    }

    // Method to write to not delivered CSV
    private static void writeNotDelivered(String line) throws IOException {
        notDeliveredWriter.write(line + "\n");
    }

    private static void doTrustToCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    return;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                    return;
                }
            }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier hv = (String urlHostName, SSLSession session) -> {
            return true;
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
}