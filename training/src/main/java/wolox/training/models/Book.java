package wolox.training.models;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import org.aspectj.lang.annotation.RequiredTypes;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.RequestAttribute;

@Entity @Embeddable
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    public Book() {
        this.id = null;
    }


    public Book(String author, String image, String title, String subtitle, String publisher,
        String year, Integer pages, String isbn) {
        //this.id = id;
        this.author = author;
        this.image = image;
        this.title = title;
        this.subtitle = subtitle;
        this.publisher = publisher;
        this.year = year;
        this.pages = pages;
        this.isbn = isbn;
    }

    @Column @NotNull
    private String genre;
    @Column @NotNull
    private String author;
    @Column @NotNull
    private String image;
    @Column @NotNull
    private String title;
    @Column @NotNull
    private String subtitle;
    @Column @NotNull
    private String publisher;
    @Column @NotNull
    private String year;
    @Column @NotNull
    private Integer pages;
    @Column @NotNull
    private String isbn;

    public Long getId() { return id; }

    public void setId(Long id){this.id = id;}

    public String getAuthor() {
        return author;
    }


    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean anyArgumentNull() {
        return this.author=="" || this.genre=="" || this.image=="" || this.isbn=="" || this.pages==null || this.publisher=="" || this.subtitle=="" || this.title==""|| this.year=="";
    }
}

