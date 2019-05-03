package wolox.training.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.postgresql.shaded.com.ongres.scram.common.util.Preconditions;

@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    public Book() {

    }


    public Book(String author, String image, String title, String subtitle, String publisher,
        String year, Integer pages, String isbn) {
        setId(id);
        setAuthor(author);
        setImage(image);
        setTitle(title);
        setSubtitle(subtitle);
        setPublisher(publisher);
        setYear(year);
        setPages(pages);
        setIsbn(isbn);
    }

    private String genre;

    private String author;

    private String image;

    private String title;

    private String subtitle;

    private String publisher;

    private String year;

    private Integer pages;

    private String isbn;

    public Long getId() { return id; }

    private void setId(Long id) {
        Preconditions.checkArgument(id!=null && id >0, "The id must be greater than 0");
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }


    public void setAuthor(String author) {
        Preconditions.checkNotNull(author, "The author cannot be null");
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        Preconditions.checkNotNull(image, "The image cannot be null");
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        Preconditions.checkNotNull(title, "The title cannot be null");
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        Preconditions.checkNotNull(subtitle, "The subtitle cannot be null");
        this.subtitle = subtitle;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        Preconditions.checkNotNull(publisher, "The publisher cannot be null");
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        Preconditions.checkArgument(year!=null && Integer.parseInt(year) > 0, "Please enter a valid year");
        this.year = year;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        Preconditions.checkArgument(pages!=null && pages > 1, "The book must have at least 1 page");
        this.pages = pages;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        Preconditions.checkNotNull(title, "The title cannot be null");
        this.isbn = isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        Preconditions.checkNotNull(genre, "The genre cannot be null");
        this.genre = genre;
    }

}

