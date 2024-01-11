package it.unipi.lsmsd.Model;

import java.util.Date;
import java.util.List;

public class Review {
    private String ISBN;
    private String title;
    private String profileName;
    private float score;
    private Date time;
    private String review;
    List<String> categories;
    List<String> authors;

    public Review(String ISBN, String title, String profileName, float score, Date time,  String review, List<String> categories, List<String> authors) {
        this.ISBN = ISBN;
        this.title = title;
        this.profileName = profileName;
        this.score = score;
        this.time = time;
        this.review = review;
        this.categories = categories;
        this.authors = authors;
    }

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

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }


    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "Review{" +
                "ISBN='" + ISBN + '\'' +
                ", title='" + title + '\'' +
                ", profileName='" + profileName + '\'' +
                ", score=" + score +
                ", time=" + time +
                ", review='" + review + '\'' +
                ", categories=" + categories +
                ", authors=" + authors +
                '}';
    }
}
