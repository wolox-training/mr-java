package wolox.training.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.exceptions.ConnectionFailedException;
import wolox.training.exceptions.CouldNotReadBookFromAPI;
import wolox.training.models.BookDTO;

@Service
public class OpenLibraryService {

    public BookDTO bookInfo(String isbn)
        throws IOException, JSONException, ConnectionFailedException, BookNotFoundException, CouldNotReadBookFromAPI {

        BookDTO bookDTO;

        URL url = new URL("https://openlibrary.org/api/books?bibkeys=ISBN:"+isbn+"&format=json&jscmd=data");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        if(con.getResponseCode()!=HttpURLConnection.HTTP_OK){
            throw new ConnectionFailedException(con.getResponseCode(), con.getResponseMessage());
        }

        String response = getResponseString(con.getInputStream());

        JSONObject jo = new JSONObject(response);

        if(jo.length()<1) {
            throw new BookNotFoundException();
        }

        jo = (JSONObject) jo.get("ISBN:" + isbn);

        bookDTO = createBookDTO(jo, isbn);

        return bookDTO;
    }

    //region private  methods
    private String getResponseString(InputStream is) throws IOException {

        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer response = new StringBuffer();

        while((line = br.readLine()) !=null ) {
            response.append(line);
        }

        br.close();

        return response.toString();
    }

    private BookDTO createBookDTO(JSONObject jo, String isbn) throws CouldNotReadBookFromAPI {
        BookDTO bookDTO = new BookDTO();

        try {
            bookDTO.setISBN(isbn);
            bookDTO.setTitle(jo.getString("title"));
            bookDTO.setSubtitle(jo.getString("subtitle"));
            bookDTO.setPublishers(fromJsonArrayToNamesList(jo.getJSONArray("publishers"), "name"));
            bookDTO.setNumberOfPages(jo.getInt("number_of_pages"));
            bookDTO.setPublishDate(jo.getString("publish_date"));
            bookDTO.setAuthors(fromJsonArrayToNamesList(jo.getJSONArray("authors"), "name"));
            bookDTO.setImage(jo.getJSONObject("cover").getString("small"));
        }catch (Exception ex){
            throw new CouldNotReadBookFromAPI(ex.getMessage());
        }

        return bookDTO;
    }

    /**
     * given a property name and a JsonArray with objects on it, returns a list of string values for the given property
     * @param jsonArray
     * @param propertyName
     * @return list of string values for the given property
     */
    private List<String> fromJsonArrayToNamesList(JSONArray jsonArray, String propertyName)
        throws JSONException {
        List<String> list = new ArrayList<>();
        for(int i = 0; i<jsonArray.length(); i++){
            JSONObject jo = jsonArray.getJSONObject(0);
            list.add(jo.getString(propertyName));
        }

        return list;
    }
    //endregion

}
