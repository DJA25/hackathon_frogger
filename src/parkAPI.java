import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.imageio.ImageIO;

public class parkAPI {
        static String apiKey = "9r9lbXH3nfNVb6ULvavhTsflTjpwrH435SvVFqtB"; //this is the API key

        public static ArrayList getParkList () throws IOException, ParseException {
            URL url = new URL("https://developer.nps.gov/api/v1/parks?limit=700&api_key="+apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() == 404) {
                return null;
            } else if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            StringBuilder totalJson= new StringBuilder();
            while ((output = br.readLine()) != null) {
                totalJson.append(output);
            }
            conn.disconnect();
            JSONParser parser = new JSONParser();
            ArrayList parkList = new ArrayList();
            org.json.simple.JSONObject allParkJSON = (org.json.simple.JSONObject) parser.parse(totalJson.toString());
            org.json.simple.JSONArray parkJSON = (org.json.simple.JSONArray) allParkJSON.get("data");
            for (Object p : parkJSON) {
                org.json.simple.JSONObject thisParkJSON = (org.json.simple.JSONObject) p;
                NationalPark thisPark = new NationalPark();
                thisPark.parkName = (String)thisParkJSON.get("fullName");
                thisPark.parkCode = (String)thisParkJSON.get("parkCode");
                thisPark.description = (String)thisParkJSON.get("description");
                parkList.add(thisPark);
            }
            return parkList;
        }

        public static NationalPark getParkInfo (String parkCode) throws IOException, ParseException {
            NationalPark thisPark = new NationalPark();
            thisPark.parkCode=parkCode;
            URL url = new URL("https://developer.nps.gov/api/v1/parks?parkcode="+parkCode+"&limit=700&api_key="+apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() == 404) {
                return null;
            } else if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            StringBuilder totalJson= new StringBuilder();
            while ((output = br.readLine()) != null) {
                totalJson.append(output);
            }
            conn.disconnect();
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject parkJSON = (org.json.simple.JSONObject) parser.parse(totalJson.toString());
            org.json.simple.JSONArray thisJSON = (org.json.simple.JSONArray)parkJSON.get("data");
            thisPark.parkName=(String)((org.json.simple.JSONObject)thisJSON.get(0)).get("fullName");
            thisPark.description=(String)((org.json.simple.JSONObject)thisJSON.get(0)).get("description");

            return getParkInfo(thisPark);
        }

        public static NationalPark getParkInfo(NationalPark park) throws IOException, ParseException{

            NationalPark nationalParkData = new NationalPark();
            nationalParkData.parkCode = park.parkCode;
            nationalParkData.parkName = park.parkName;
            nationalParkData.description = park.description;

            /// load alerts
//            URL url = new URL("https://developer.nps.gov/api/v1/alerts?parkCode="+nationalParkData.parkCode+"&api_key="+apiKey);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/json");
//            if (conn.getResponseCode() == 404) {
//                return null;
//            } else if (conn.getResponseCode() != 200) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + conn.getResponseCode());
//            }
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    (conn.getInputStream())));
//            String output;
//            StringBuilder totalJson= new StringBuilder();
//            while ((output = br.readLine()) != null) {
//                totalJson.append(output);
//            }
//            conn.disconnect();
//            JSONParser parser = new JSONParser();
//            ArrayList alerts = new ArrayList();
//            org.json.simple.JSONObject allAlertJSON = (org.json.simple.JSONObject) parser.parse(totalJson.toString());
//            org.json.simple.JSONArray alertJSON = (org.json.simple.JSONArray) allAlertJSON.get("data");
//            for (Object a : alertJSON) {
//                org.json.simple.JSONObject thisAlert = (org.json.simple.JSONObject) a;
//                Alert alert = new Alert();
//                alert.title = (String)thisAlert.get("title");
//                alert.link = (String)thisAlert.get("url");
//                alert.description = (String)thisAlert.get("description");
//                alerts.add(alert);
//            }
//            nationalParkData.alerts=(ArrayList)alerts.clone();

            // get galleries
            URL url = new URL("https://developer.nps.gov/api/v1/multimedia/galleries?parkCode="+nationalParkData.parkCode+"&api_key="+apiKey);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() == 404) {
                return null;
            } else if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output = "";
            StringBuilder totalJson= new StringBuilder();
            while ((output = br.readLine()) != null) {
                totalJson.append(output);
            }
            conn.disconnect();
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject allGalleryJSON = (org.json.simple.JSONObject) parser.parse(totalJson.toString());
            org.json.simple.JSONArray galleryJSON = (org.json.simple.JSONArray) allGalleryJSON.get("data");

            ArrayList animalGalleries = new ArrayList<>();
            ArrayList plantGalleries = new ArrayList<>();
            ArrayList backgroundGalleries = new ArrayList<>();


            for (Object g: galleryJSON){
                org.json.simple.JSONObject thisGallery = (org.json.simple.JSONObject) g;
                ArrayList tags = (ArrayList) thisGallery.get("tags");
                String title = (String)thisGallery.get("title");
                if(tags.contains("animals")||tags.contains("wildlife")||title.contains("wildlife")||title.contains("animal")||title.contains("bicycling")||title.contains("mammal")){
                    animalGalleries.add((String)thisGallery.get("id"));
                }
                if(tags.contains("plant")||tags.contains("plants")||title.contains("plant")||title.contains("plants")||title.contains("tree")){
                    plantGalleries.add((String)thisGallery.get("id"));
                }
                if(tags.contains("scenery")||tags.contains("wilderness")||title.contains("scenery")||title.contains("wilderness")){
                    backgroundGalleries.add((String)thisGallery.get("id"));
                }
            }

            // get plant images
//            ArrayList plantImageArray = new ArrayList<>();
//            if(plantGalleries.size()>0) {
//                for (Object o : plantGalleries) {
//                    url = new URL("https://developer.nps.gov/api/v1/multimedia/galleries/assets?galleryId=" + (String) o + "parkCode=" + nationalParkData.parkCode + "&api_key=" + apiKey);
//                    conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setRequestProperty("Accept", "application/json");
//                    if (conn.getResponseCode() == 404) {
//                        return null;
//                    } else if (conn.getResponseCode() != 200) {
//                        throw new RuntimeException("Failed : HTTP error code : "
//                                + conn.getResponseCode());
//                    }
//                    br = new BufferedReader(new InputStreamReader(
//                            (conn.getInputStream())));
//                    output = "";
//                    totalJson = new StringBuilder();
//                    while ((output = br.readLine()) != null) {
//                        totalJson.append(output);
//                    }
//                    conn.disconnect();
//                    parser = new JSONParser();
//
//                    org.json.simple.JSONObject allImagesJSON = (org.json.simple.JSONObject) parser.parse(totalJson.toString());
//                    org.json.simple.JSONArray imageJSON = (org.json.simple.JSONArray) allImagesJSON.get("data");
//                    for (Object a:imageJSON) {
//                        org.json.simple.JSONObject thisImage = (org.json.simple.JSONObject)a;
//                        plantImageArray.add((String) ((org.json.simple.JSONObject) thisImage.get("fileInfo")).get("url"));
//                    }
//                }
//            }else{
//                plantImageArray.add("https://upload.wikimedia.org/wikipedia/en/0/00/National_Park_Service_sign.jpg");
//                System.out.println("THIS NATIONAL PARK DOESNT HAVE ASSOCIATED PLANT IMAGES");
//            }
//            nationalParkData.plantImages = (ArrayList) plantImageArray.clone();

            // get background images
            ArrayList backgroundImageArray = new ArrayList<>();
            if(backgroundGalleries.size()>0) {
                for (Object o : backgroundGalleries) {
                    url = new URL("https://developer.nps.gov/api/v1/multimedia/galleries/assets?galleryId=" + (String) o + "parkCode=" + nationalParkData.parkCode + "&api_key=" + apiKey);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    if (conn.getResponseCode() == 404) {
                        return null;
                    } else if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }
                    br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));
                    output = "";
                    totalJson = new StringBuilder();
                    while ((output = br.readLine()) != null) {
                        totalJson.append(output);
                    }
                    conn.disconnect();
                    parser = new JSONParser();

                    org.json.simple.JSONObject allImagesJSON = (org.json.simple.JSONObject) parser.parse(totalJson.toString());
                    org.json.simple.JSONArray imageJSON = (org.json.simple.JSONArray) allImagesJSON.get("data");
                    for (Object a:imageJSON) {
                        org.json.simple.JSONObject thisImage = (org.json.simple.JSONObject)a;
                        backgroundImageArray.add((String) ((org.json.simple.JSONObject) thisImage.get("fileInfo")).get("url"));
                    }
                }
            }else{
                backgroundImageArray.add("https://i.pinimg.com/originals/16/fc/f6/16fcf6b8b200bbbc6bc2f90a589b63cd.jpg");
                System.out.println("THIS NATIONAL PARK DOESNT HAVE ASSOCIATED BACKGROUND IMAGES");
            }
            nationalParkData.backgroundImages = (ArrayList) backgroundImageArray.clone();

            // get animal images
            ArrayList animalImageArray = new ArrayList<>();
            if(animalGalleries.size()>0) {
                for (Object o : animalGalleries) {
                    url = new URL("https://developer.nps.gov/api/v1/multimedia/galleries/assets?galleryId=" + (String) o + "parkCode=" + nationalParkData.parkCode + "&api_key=" + apiKey);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    if (conn.getResponseCode() == 404) {
                        return null;
                    } else if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }
                    br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));
                    output = "";
                    totalJson = new StringBuilder();
                    while ((output = br.readLine()) != null) {
                        totalJson.append(output);
                    }
                    conn.disconnect();
                    parser = new JSONParser();

                    org.json.simple.JSONObject allImagesJSON = (org.json.simple.JSONObject) parser.parse(totalJson.toString());
                    org.json.simple.JSONArray imageJSON = (org.json.simple.JSONArray) allImagesJSON.get("data");
                    for (Object a:imageJSON) {
                        org.json.simple.JSONObject thisImage = (org.json.simple.JSONObject)a;
                        animalImageArray.add((String) ((org.json.simple.JSONObject) thisImage.get("fileInfo")).get("url"));
                    }
                }
            }else{
                animalImageArray.add("https://i.pinimg.com/originals/d0/34/82/d03482f782943a6863bf822bd0ce1434.jpg");
                System.out.println("THIS NATIONAL PARK DOESNT HAVE ASSOCIATED ANIMAL IMAGES");
            }
            nationalParkData.animalImages = (ArrayList) animalImageArray.clone();




            return nationalParkData;
        }
}