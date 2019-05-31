package wolox.training.models;

import com.google.common.base.Joiner;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public String getYear(){
        Pattern pattern = Pattern.compile(".*(\\d{4}).*");
        Matcher matcher = pattern.matcher(getPublishDate());
        if(!matcher.matches()) {
            throw new IllegalStateException("Could not get year from publish date");
        }
        return matcher.group(1);
    }

}
