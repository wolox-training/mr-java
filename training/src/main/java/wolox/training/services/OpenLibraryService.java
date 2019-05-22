package wolox.training.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wolox.training.exceptions.BookNotFoundException;
import wolox.training.exceptions.ConnectionFailedException;
import wolox.training.models.Book;
import wolox.training.models.BookDTO;

public class OpenLibraryService {


    public BookDTO bookInfo(String isbn)
        throws IOException, JSONException, ConnectionFailedException, BookNotFoundException {

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

    private BookDTO createBookDTO(JSONObject jo, String isbn) throws JSONException {
        BookDTO bookDTO = new BookDTO();

        bookDTO.setISBN(isbn);
        bookDTO.setTitle(jo.getString("title"));
        try {
            bookDTO.setSubtitle(jo.getString("subtitle"));
        }catch (Exception ex){
            bookDTO.setSubtitle("-");
        }
        bookDTO.setPublishers(fromJsonArrayToNamesList(jo.getJSONArray("publishers"), "name"));
        bookDTO.setNumberOfPages(jo.getInt("number_of_pages"));

        Pattern pattern = Pattern.compile(".*(\\d{4}).*");
        Matcher matcher = pattern.matcher(jo.getString("publish_date"));
        if(matcher.matches()) {
            bookDTO.setPublishDate(matcher.group(1));
        }
        bookDTO.setAuthors(fromJsonArrayToNamesList(jo.getJSONArray("authors"), "name"));
        bookDTO.setImage(jo.getJSONObject("cover").getString("small"));

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
