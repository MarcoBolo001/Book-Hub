package it.unipi.lsmsd.Model;

import java.util.Date;

public class LastBookReviews {
    private String ISBN;
    private String Title;
    private float score;
    private Date time;
    private String review;

    public LastBookReviews(String ISBN, String title, float score, Date time, String review) {
        this.ISBN = ISBN;
        this.Title = title;
        this.score = score;
        this.time = time;
        this.review = review;
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
        this.Title = title;
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

    @Override
    public String toString() {
        return "LastBookReviews{" +
                "ISBN='" + ISBN + '\'' +
                ", Title='" + Title + '\'' +
                ", score=" + score +
                ", time=" + time +
                ", review='" + review + '\'' +
                '}';
    }
}
