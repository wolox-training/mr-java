package wolox.training.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.IntNode;
import com.google.common.base.Joiner;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class BookDTO {

    private String ISBN;
    private String title;
    private String subtitle;
    private List<String> publishers;
    private String publishDate;
    private Integer numberOfPages;
    private List<String> authors;

    private String image;

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public void setPublishers(List<String> publishers) {
        this.publishers = publishers;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public String getAuthorsAsString(){
        String response = Joiner.on(" - ").join(this.getAuthors());
        return response;
    }

    public String getPublishersAsString(){
        String response = Joiner.on(" - ").join(this.getPublishers());
        return response;
    }


    public Book toBook(){
        Book book = new Book();
        book.setPublisher(this.getPublishersAsString());
        book.setAuthor(this.getAuthorsAsString());
        book.setTitle(this.getTitle());
        book.setIsbn(this.getISBN());
        book.setPages(this.getNumberOfPages());
        book.setSubtitle(this.getSubtitle());
        book.setImage(this.getImage());
        book.setYear(this.getPublishDate());
        return book;
    }
}
