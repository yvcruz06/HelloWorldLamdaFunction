package helloworld;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.*;
import com.google.maps.model.*;

import java.net.URL;


public class Tester
{

    private static final int RADIUS = 3000;
    private static final double LATITUDE = 36.6501;
    private static final double LONGITUDE = -121.7941;

    public static void main(String[] args)
    {
        try
        {
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyCHJS3KQGJ_EV1xoC7F6onWssLhaVVEbuk")
                    .build();

            GeocodingResult[] results =  GeocodingApi.geocode(context,
                    "CSUMB").await();
            LatLng location = results[0].geometry.location;
            getResult(context,LATITUDE,LONGITUDE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private static void getResult(GeoApiContext context,double latitude, double longitude)
            throws Exception
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        NearbySearchRequest req = PlacesApi.nearbySearchQuery(context,
                new com.google.maps.model.LatLng(latitude,longitude));
        LatLng origin = new LatLng(latitude,longitude);


        try {
            PlacesSearchResponse resp = req.type(PlaceType.PARKING)
                    .radius(RADIUS)
                    .await();
            if (resp.results != null && resp.results.length > 0) {
                for (PlacesSearchResult r : resp.results)
                {
                    PlaceDetails details = PlacesApi.placeDetails(context, r.placeId).await();
                    int len = details.addressComponents.length;
                    LatLng destination = details.geometry.location;
                    double distance = computeDistance(origin,destination);
                    System.out.println(distance + " from centerpoint");

                    System.out.println(details.name);
                    System.out.println(details.formattedAddress );
                    for (int i = 0; i < len;i++)
                    {

//                        System.out.println(details.placeId);
//                        System.out.println(details.geometry.location);
//                        System.out.println(gson.toJson(details.addressComponents));
                    }

                }

            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static double computeDistance(LatLng origin, LatLng destination)
    {
        int earth_radius = 6378137;
        double dLat = toRad(destination.lat - origin.lat);
        double dLon = toRad(destination.lng - origin.lng);
        double x = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(toRad(origin.lat))*Math.cos(toRad(destination.lat))*Math.sin(dLon/2)*Math.sin(dLon/2);
        return earth_radius*2 * Math.atan2(Math.sqrt(x),Math.sqrt(1-x));

    }

    private static double toRad(double val)
    {
        return val * (Math.PI/180);
    }
}
