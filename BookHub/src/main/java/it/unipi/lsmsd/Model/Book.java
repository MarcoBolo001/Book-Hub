package it.unipi.lsmsd.Model;

import java.util.Date;
import java.util.List;

public class Book {
    private String ISBN;
    private String Title;
    private String description;
    List<String>authors;
    List<String>categories;
    private Date publishedDate;
    List<LastUserReviews> last_users_review;

    public Book(String ISBN, String title, String description, List<String> authors, List<String> categories, Date publishedDate, List<LastUserReviews> reviews) {
        this.ISBN = ISBN;
        this.Title = title;
        this.description = description;
        this.authors = authors;
        this.categories = categories;
        this.publishedDate = publishedDate;
        this.last_users_review = reviews;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public List<LastUserReviews> getLast_users_review() {
        return last_users_review;
    }

    public void setLast_users_review(List<LastUserReviews> last_users_review) {
        this.last_users_review = last_users_review;
    }

    @Override
    public String toString() {
        return "Book{" +
                "ISBN='" + ISBN + '\'' +
                ", Title='" + Title + '\'' +
                ", description='" + description + '\'' +
                ", authors=" + authors +
                ", categories=" + categories +
                ", publishedDate=" + publishedDate +
                ", last_users_review=" + last_users_review +
                '}';
    }
}
