import java.net.*;
import java.util.*;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import com.google.gson.Gson;

public class BingWebSearch {

    // TODO: remove key before uploading to github
    static String subscriptionKey = "301e444517084ed891918d2a31c17f3a";
    static String host = "https://westus2.api.cognitive.microsoft.com/";
    static String path = "/bing/v7.0/search";
    static String searchTerm = "fact about tree";

    public static void main (String[] args) {
        // Confirm the subscriptionKey is valid.
        if (subscriptionKey.length() != 32) {
            System.out.println("Invalid Bing Search API subscription key!");
            System.out.println("Please paste yours into the source code.");
            System.exit(1);
        }

        SearchResults webSearchResults = null;
        try {
            webSearchResults = SearchWeb(searchTerm);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }

        Map json = new Gson().fromJson(webSearchResults.jsonResponse, Map.class);
        Map webPages = (Map)json.get("webPages");
        ArrayList value = (ArrayList)webPages.get("value");
        Map result1 = (Map)value.get(0);
        if ((boolean)result1.get("isFamilyFriendly"))
            System.out.println("Did you know?\n" + result1.get("snippet") + "\n\nFind out more at: " + result1.get("url"));
        else
            System.out.println("Sorry the results were innapropriate.");
    }

    private static SearchResults SearchWeb (String searchQuery) throws Exception {
        // Construct the URL.
        URL url = new URL(host + path + "?q=" +  URLEncoder.encode(searchQuery, "UTF-8"));

        // Open the connection.
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

        // Receive the JSON response body.
        InputStream stream = connection.getInputStream();
        String response = new Scanner(stream).useDelimiter("\\A").next();

        // Construct the result object.
        SearchResults results = new SearchResults(new HashMap<String, String>(), response);

        // Extract Bing-related HTTP headers.
        Map<String, List<String>> headers = connection.getHeaderFields();
        for (String header : headers.keySet()) {
            if (header == null) continue;      // may have null key
            if (header.startsWith("BingAPIs-") || header.startsWith("X-MSEdge-")){
                results.relevantHeaders.put(header, headers.get(header).get(0));
            }
        }
        stream.close();
        return results;
    }

    private static class SearchResults{
        HashMap<String, String> relevantHeaders;
        String jsonResponse;
        SearchResults(HashMap<String, String> headers, String json) {
            relevantHeaders = headers;
            jsonResponse = json;
        }
    }
}