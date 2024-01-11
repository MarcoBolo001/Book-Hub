package it.unipi.lsmsd.Model;

import java.util.Date;

public class LastUserReviews {
    private String profileName;
    private Date time;
    private float score;
    private String review;

    public LastUserReviews(String profileName, Date time, float score, String review) {
        this.profileName = profileName;
        this.time = time;
        this.score = score;
        this.review = review;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Override
    public String toString() {
        return "LastUserReviews{" +
                "profileName='" + profileName + '\'' +
                ", time=" + time +
                ", score=" + score +
                ", review='" + review + '\'' +
                '}';
    }
}
