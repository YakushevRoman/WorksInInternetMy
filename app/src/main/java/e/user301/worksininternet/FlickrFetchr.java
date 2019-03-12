package e.user301.worksininternet;

import android.net.Uri;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

public class FlickrFetchr {
    public static final String TAG = "photo";
    public static final String API_KEY = "a1fa9eeaea69d4b914b1887bbcb5391f";
    public static final String FETCH_RECENTS_METHOD =  "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_s")
            .build();

    public byte[] getUrlBytes (String urlSpec) throws IOException{
        // создаем url на базе строки
        URL url = new URL(urlSpec);
        // создаем обьект для подключения к заданному url
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        try{
            // то куда будем писать данные
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();// для гет / для POST -
            //проверяем http ok
            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK){
                throw new IOException(connection.getResponseMessage() + ": with" + urlSpec);
            }
            // для чтения
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0 , bytesRead);
            }
            //возвращием все что записали
            return out.toByteArray();
        }finally {
            // разрываем соедениние после всего
            connection.disconnect();
        }

    }

    public String getUrlString (String urlSpec) throws IOException{
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItems> fetchRecentPhotos(){
        String url = builURL(FETCH_RECENTS_METHOD, null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItems> seachPhotos (String query){
        String url = builURL(SEARCH_METHOD, query);
        return downloadGalleryItems(url);
    }

    public List<GalleryItems> downloadGalleryItems(String url){
        List <GalleryItems> items = new ArrayList<>();

        try {
            String jsonString = getUrlString(url);
            // разбор json
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);
            Log.d(TAG, "fetchItems: " + jsonString);
        } catch (IOException e) {
            Log.d(TAG, "fetchItems: " + e);
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "fetchItems: " + items.size());
        return items;
    }
    // метод для потсроения url
    private String builURL (String method, String query){
        Uri.Builder builder = ENDPOINT.buildUpon().appendQueryParameter("method", method);
        if (method.equals(SEARCH_METHOD)){
            builder.appendQueryParameter("text", query);
        }
        return builder.build().toString();
    }

    private void parseItems (List<GalleryItems> items, JSONObject jsonObject) throws JSONException {
        JSONObject photosJsonObject = jsonObject.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        Log.d(TAG, "fetchItems: " + photoJsonArray.length());
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            GalleryItems galleryItem = new GalleryItems();

            galleryItem.setrId(photoJsonObject.getString("id"));
            galleryItem.setrCaption(photoJsonObject.getString("title"));
            /*if (!photosJsonObject.has("url_s")){
                continue;
            }*/
            galleryItem.setrUrl(photoJsonObject.getString("url_s"));
            Log.d(TAG, "parseItems: " + photoJsonObject.getString("title"));
            items.add(galleryItem);
        }
    }
}
