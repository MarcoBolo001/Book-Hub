package it.unipi.lsmsd.Model;


import java.util.List;

public class User {
    private String profileName;
    private String password;
    private int type;
    List<LastBookReviews>last_reviews;

    public User(String profileName, String password, int type, List<LastBookReviews> last_reviews) {
        this.profileName = profileName;
        this.password = password;
        this.type = type;
        this.last_reviews = last_reviews;
    }

    public String getprofileName() {
        return profileName;
    }

    public void setprofileName(String profileName) {
        this.profileName = profileName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<LastBookReviews> getLast_reviews() {
        return last_reviews;
    }

    public void setLast_reviews(List<LastBookReviews> last_reviews) {
        this.last_reviews = last_reviews;
    }

    @Override
    public String toString() {
        return "User{" +
                "profileName='" + profileName + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                ", last_reviews=" + last_reviews +
                '}';
    }
}
