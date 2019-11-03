package helloworld;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.*;
import com.google.maps.model.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Tester
{
    public static String search;
    public static int radius;
    private static final double LATITUDE = 36.6501;
    private static final double LONGITUDE = -121.7941;


    public static void main(String[] args) {
        try {
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyCHJS3KQGJ_EV1xoC7F6onWssLhaVVEbuk")
                    .build();
            LatLng l = new LatLng();
            PlacesApi.nearbySearchQuery(context,l);
            search = "CSUMB";
            GeocodingResult[] results =  GeocodingApi.geocode(context,
                    search).await();
            radius = 3000;
            LatLng origin = new LatLng(LATITUDE,LONGITUDE);
            getResult(context, radius, results[0].geometry.location.lat, results[0].geometry.location.lng, origin);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getResult(GeoApiContext context, int radius, double latt, double longi, LatLng origin) throws Exception {
        NearbySearchRequest req = PlacesApi.nearbySearchQuery(context, new com.google.maps.model.LatLng(latt,longi));
        List<Place> places = new ArrayList<>();
        HashMap<String,List<Place>> data = new HashMap<>();
        try {
            PlacesSearchResponse resp = req
                    .type(PlaceType.PARKING)
                    .radius(radius)
                    .await();
            if (resp.results != null && resp.results.length > 0) {
                for (PlacesSearchResult r : resp.results) {
                    PlaceDetails details = PlacesApi.placeDetails(context,r.placeId).await();

                    int len = details.addressComponents.length;
                    LatLng destination = details.geometry.location;
                    //double distance = computeDistance(origin,destination);
                   // System.out.println(distance + " from centerpoint");

                    double distance = computeDistance(origin,destination);
                    String name = details.name;
                    String address = details.formattedAddress;
                    URL icon = details.icon;
                    double lat = details.geometry.location.lat;
                    double lng = details.geometry.location.lng;
                    String placeId = details.placeId;

                    //System.out.println(name+address+icon+lat+lng+"..."+placeId+"...");
                    //System.out.println(distance + " from centerpoint");
                    places.add(new Place(name,distance,address,icon,lat,lng,placeId));
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            }
        data.put("lots",places);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(data);
        System.out.println(json);
    }

    private static double computeDistance(LatLng origin, LatLng destination) {
        int earth_radius = 6378137;
        double dLat = toRad(destination.lat - origin.lat);
        double dLon = toRad(destination.lng - origin.lng);
        double x = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(toRad(origin.lat))*Math.cos(toRad(destination.lat))*Math.sin(dLon/2)*Math.sin(dLon/2);
        return earth_radius*2 * Math.atan2(Math.sqrt(x),Math.sqrt(1-x));

    }

    private static double toRad(double val) {
        return val * (Math.PI/180);
    }

}
