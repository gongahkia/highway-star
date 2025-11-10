package highwaystar.services;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class GeoLocationService {
    private static GeoLocationService instance;
    private DatabaseReader reader;

    public static class Location {
        private final double latitude;
        private final double longitude;
        private final String city;
        private final String country;

        public Location(double latitude, double longitude, String city, String country) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.city = city;
            this.country = country;
        }

        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getCity() { return city; }
        public String getCountry() { return country; }

        @Override
        public String toString() {
            return city + ", " + country + " (" + latitude + ", " + longitude + ")";
        }
    }

    private GeoLocationService() {
        initializeDatabase();
    }

    public static GeoLocationService getInstance() {
        if (instance == null) {
            instance = new GeoLocationService();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            // Try to load GeoLite2-City database from resources
            InputStream dbStream = getClass().getClassLoader().getResourceAsStream("GeoLite2-City.mmdb");
            if (dbStream != null) {
                reader = new DatabaseReader.Builder(dbStream).build();
                System.out.println("GeoIP database loaded successfully");
            } else {
                System.err.println("GeoLite2-City.mmdb not found in resources. IP geolocation will not work.");
                System.err.println("Download from: https://dev.maxmind.com/geoip/geoip2/geolite2/");
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize GeoIP database: " + e.getMessage());
        }
    }

    public CompletableFuture<Location> getCurrentLocation() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get public IP address
                String ip = getPublicIP();
                if (ip == null || ip.isEmpty()) {
                    return getDefaultLocation();
                }

                // Look up IP in GeoIP database
                return getLocationByIP(ip);

            } catch (Exception e) {
                System.err.println("Error getting current location: " + e.getMessage());
                return getDefaultLocation();
            }
        });
    }

    public Location getLocationByIP(String ipAddress) {
        if (reader == null) {
            System.err.println("GeoIP database not initialized");
            return getDefaultLocation();
        }

        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            CityResponse response = reader.city(inetAddress);

            double lat = response.getLocation().getLatitude();
            double lon = response.getLocation().getLongitude();
            String city = response.getCity().getName();
            String country = response.getCountry().getName();

            return new Location(lat, lon, city != null ? city : "Unknown", country != null ? country : "Unknown");

        } catch (IOException | GeoIp2Exception e) {
            System.err.println("Error looking up IP: " + e.getMessage());
            return getDefaultLocation();
        }
    }

    private String getPublicIP() {
        try {
            // Use multiple services as fallback
            String[] services = {
                "https://api.ipify.org",
                "https://checkip.amazonaws.com",
                "https://icanhazip.com"
            };

            for (String service : services) {
                try {
                    URL url = new URL(service);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String ip = reader.readLine().trim();
                    reader.close();

                    if (ip != null && !ip.isEmpty() && isValidIP(ip)) {
                        return ip;
                    }
                } catch (Exception e) {
                    // Try next service
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting public IP: " + e.getMessage());
        }

        return null;
    }

    private boolean isValidIP(String ip) {
        // Basic IP validation
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;

        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Location getDefaultLocation() {
        // Default to Singapore (as in original code)
        return new Location(1.3521, 103.8198, "Singapore", "Singapore");
    }

    public void close() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                System.err.println("Error closing GeoIP database: " + e.getMessage());
            }
        }
    }
}
