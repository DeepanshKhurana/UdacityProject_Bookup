package in.thepolymath.bookup;

/**
 * This class defines methods that will help us process the Query
 */

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static in.thepolymath.bookup.BookActivity.LOG_TAG;

/**
 * Helper methods related to requesting and receiving Book data from Google Books API.
 */
public final class QueryParse {
    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    public static List<Book> extractFeatureFromJson(String BookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(BookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding Books to
        List<Book> Books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(BookJSON);

            // Extract the JSONArray associated with the key called "items",
            JSONArray itemsJSON = baseJsonResponse.getJSONArray("items");
            for (int i = 0; i < itemsJSON.length(); i++) {
                JSONObject volumeJSON = itemsJSON.getJSONObject(i).getJSONObject("volumeInfo");
                String description, title, link, infoURL, publishedDate, amount, currencyCode="", authorString = "", categoryString = "";
                if (volumeJSON.has("title")) {
                    title = volumeJSON.getString("title");
                } else title = "";
                if (volumeJSON.has("authors")) {
                    JSONArray authors = volumeJSON.getJSONArray("authors");
                    int size = authors.length();
                    if (size == 1) {
                        authorString = authors.getString(0);
                    } else {
                        for (int a = 0; a < size; a++) {
                            if (a == (size - 1)) {
                                authorString = authorString + authors.getString(a);
                            } else authorString = authorString + authors.getString(a) + ", ";
                        }
                    }
                } else authorString = "";

                if (volumeJSON.has("publishedDate")) {
                    publishedDate = volumeJSON.getString("publishedDate");
                } else publishedDate = "";
                if (volumeJSON.has("description")) {
                    description = volumeJSON.getString("description");
                } else description = "";
                if (volumeJSON.has("categories")) {
                    JSONArray categories = volumeJSON.getJSONArray("categories");
                    int size = categories.length();
                    if (size == 1) {
                        categoryString = categories.getString(0);
                    } else {
                        for (int a = 0; a < size; a++) {
                            if (a == (size - 1)) {
                                categoryString = categoryString + categories.getString(a);
                            } else categoryString = categoryString + categories.getString(a) + ", ";
                        }
                    }
                } else authorString = "";

                if (volumeJSON.has("imageLinks")) {
                    JSONObject imageLink = volumeJSON.optJSONObject("imageLinks");
                    link = imageLink.getString("thumbnail");
                } else link = "";
                if (volumeJSON.has("infoLink")) {
                    infoURL = volumeJSON.getString("infoLink");
                } else infoURL = "";
                JSONObject saleInfo = itemsJSON.getJSONObject(i).getJSONObject("saleInfo");
                if (saleInfo.has("retailPrice")) {
                    JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");
                    if (retailPrice.has("amount")) {
                        amount = retailPrice.getString("amount");
                    } else {
                        amount = "";
                    }
                    if (retailPrice.has("currencyCode")) {
                        currencyCode = retailPrice.getString("currencyCode");
                    } else  currencyCode="";
                } else amount = "";
                String costString = currencyCode + " " + amount;

                Books.add(new Book(title, authorString, infoURL, description, link, publishedDate, costString, categoryString));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryParse", "Problem parsing the Book JSON results", e);
        }

        // Return the list of Books
        return Books;
    }

    /**
     * Query the Google Books API dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> Books = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Book}s
        return Books;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}