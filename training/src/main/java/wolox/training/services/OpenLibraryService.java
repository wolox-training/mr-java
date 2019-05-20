package wolox.training.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.sun.deploy.net.HttpResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;
import javax.persistence.criteria.Predicate.BooleanOperator;
import org.json.JSONException;
import org.json.JSONObject;
import wolox.training.models.Book;
import wolox.training.models.BookDTO;

public class OpenLibraryService {


    public Book bookInfo(/*String isbn*/) throws IOException, JSONException {

        Book book;
        BookDTO bookDTO;
        Gson googleJson = new Gson();

        String isbn = "0385472579";

        URL url = new URL("https://openlibrary.org/api/books?bibkeys=ISBN:"+isbn+"&format=json&jscmd=data");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        if(con.getResponseCode()!=HttpURLConnection.HTTP_OK){
            //tirar excep
        }

        String response = getResponseString(con.getInputStream());

        JSONObject jo = new JSONObject(response);
        jo = (JSONObject) jo.get("ISBN:"+isbn);

        bookDTO = createDTO(jo, isbn);

        book = getBookFromDTO(bookDTO);

        return book;
    }

    private Book getBookFromDTO(BookDTO bookDTO) {
        Book book = new Book();

        return book;
    }

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

    private BookDTO createDTO(JSONObject jo, String isbn) throws JSONException {
        Gson gson = new Gson();
        BookDTO bookDTO = new BookDTO();

        bookDTO.setISBN(isbn);
        bookDTO.setTitle(jo.getString("title"));
        bookDTO.setSubtitle(jo.getString("subtitle"));
        bookDTO.setPublishers(gson.fromJson(String.valueOf(jo.getJSONArray("publishers")), ArrayList.class));
        bookDTO.setNumberOfPages(jo.getInt("number_of_pages"));
        bookDTO.setPublishDate(jo.getString("publish_date"));
        bookDTO.setAuthors(gson.fromJson(String.valueOf(jo.getJSONArray("authors")), ArrayList.class));

        return bookDTO;
    }

}
