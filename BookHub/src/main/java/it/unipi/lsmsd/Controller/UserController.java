package it.unipi.lsmsd.Controller;

import com.mongodb.client.ClientSession;
import it.unipi.lsmsd.Model.*;
import it.unipi.lsmsd.Persistence.MongoDBDriver;
import it.unipi.lsmsd.Persistence.MongoDBManager;
import it.unipi.lsmsd.Persistence.Neo4jDBDriver;
import it.unipi.lsmsd.Persistence.Neo4jDBManager;

import java.util.ArrayList;
import java.util.List;

public class UserController {
    private MongoDBManager mongoDBManager;
    private Neo4jDBManager neo4jDBManager;
    private List<String>categoriesList;
    /**
     * Initializes the MongoDB and Neo4j database managers, and retrieves the list of categories.
     */
    public void initialize(){
        mongoDBManager= new MongoDBManager(MongoDBDriver.getInstance().openConnection());
        neo4jDBManager= new Neo4jDBManager(Neo4jDBDriver.getInstance().openConnection());
        categoriesList=mongoDBManager.getCategories();
    }
    public void close(){
        Neo4jDBDriver.getInstance().closeConnection();
        MongoDBDriver.getInstance().closeConnection();
    }

    /**
     * Displays the user profile information including profileName, password, type, follows, following, and last reviews.
     *
     * @param user The user whose profile is to be displayed.
     */
    public void showProfile(User user) {
        System.out.println("=== User Profile ===");
        System.out.println("Profile Name: " + user.getprofileName());
        System.out.println("Password: " + user.getPassword());
        System.out.println("Type: " + (user.getType() == 1 ? "Admin" : "Normal User"));
        System.out.println("====================");

        if (user.getType() == 0) {
            System.out.println("=== Followers/Following ===");
            System.out.println("Follows: " + neo4jDBManager.getNumFollowingUser(user));
            System.out.println("Following: " + neo4jDBManager.getNumFollowersUser(user));
            System.out.println("============================");

            List<LastBookReviews> lastBookReviews = user.getLast_reviews();
            System.out.println("=== Last Reviews ===");

            if (lastBookReviews == null || lastBookReviews.isEmpty()) {
                System.out.println("No last reviews available.");
            } else {
                for (LastBookReviews review : lastBookReviews) {
                    System.out.println("---- Review ----");
                    System.out.println("ISBN: " + review.getISBN());
                    System.out.println("Title: " + review.getTitle());
                    System.out.println("Score: " + review.getScore());
                    System.out.println("Time: " + review.getTime());
                    System.out.println("Review: " + review.getReview());
                    System.out.println("---------------");
                }
            }
            System.out.println("====================");
        }
    }


    /**
     * Displays user profile information without showing the password.
     *
     * @param user The user whose profile is to be displayed.
     */
    public void showProfilewithNoPass(User user) {
        System.out.println("=== User Profile ===");
        System.out.println("Profile Name: " + user.getprofileName());
        System.out.println("Type: " + (user.getType() == 1 ? "Admin" : "Normal User"));
        System.out.println("====================");

        if (user.getType() == 0) {
            System.out.println("=== Followers/Following ===");
            System.out.println("Follows: " + neo4jDBManager.getNumFollowingUser(user));
            System.out.println("Following: " + neo4jDBManager.getNumFollowersUser(user));
            System.out.println("============================");

            List<LastBookReviews> lastBookReviews = user.getLast_reviews();
            System.out.println("=== Last Reviews ===");

            if (lastBookReviews.isEmpty()) {
                System.out.println("No last reviews available.");
            } else {
                for (LastBookReviews review : lastBookReviews) {
                    System.out.println("---- Review ----");
                    System.out.println("ISBN: " + review.getISBN());
                    System.out.println("Title: " + review.getTitle());
                    System.out.println("Score: " + review.getScore());
                    System.out.println("Time: " + review.getTime());
                    System.out.println("Review: " + review.getReview());
                    System.out.println("---------------");
                }
            }
            System.out.println("====================");
        }
    }

    /**
     * Displays the list of users that the current user is following and those who are following the current user.
     *
     * @param user The user for whom to display the followings and followers.
     */
    public void showFollowings(User user) {
        System.out.println("=== Follows ===");
        List<User> followingUsers = neo4jDBManager.getFollowingUsers(user);

        if (followingUsers == null || followingUsers.isEmpty()) {
            System.out.println("You are not following any users.");
        } else {
            for (User followingUser : followingUsers) {
                showProfilewithNoPass(followingUser);
            }
        }

        System.out.println("=== Following ===");
        List<User> followers = neo4jDBManager.getFollowers(user);

        if (followers == null || followers.isEmpty()) {
            System.out.println("You have no followers.");
        } else {
            for (User follower : followers) {
                showProfilewithNoPass(follower);
            }
        }
    }
    /**
     * Changes the password of the current user.
     *
     * @param user     The user whose password is to be changed.
     * @param password The new password.
     * @return True if the password is changed successfully, false otherwise.
     */

    public boolean changePassword(User user,String password){
        if(password.isEmpty()||password.equals(user.getPassword())){
            return false;
        }
        user.setPassword(password);
        boolean ret=mongoDBManager.updateUser(user);
        Session.getInstance().setLoggedUser(user);
        return ret;
    }
    /**
     * Follows a user.
     *
     * @param user The user to follow.
     * @return True if the follow relationship is created successfully, false otherwise.
     */
    public boolean followUser(User user){
        return neo4jDBManager.createFollowRelationship(Session.getInstance().getLoggedUser(), user);
    }

    /**
     * Unfollows a user.
     *
     * @param user The user to unfollow.
     * @return True if the follow relationship is deleted successfully, false otherwise.
     */
    public boolean unfollowUser(User user){
        return neo4jDBManager.deleteFollowRelationship(Session.getInstance().getLoggedUser(), user);
    }
    /**
     * Follows an author.
     *
     * @param name The name of the author to follow.
     * @return True if the user likes the author successfully, false otherwise.
     */
    public boolean followAuthor(String name){
        return neo4jDBManager.userLikesAuthor(Session.getInstance().getLoggedUser(), name);
    }

    /**
     * Unfollows an author.
     *
     * @param name The name of the author to unfollow.
     * @return True if the user dislikes the author successfully, false otherwise.
     */
    public boolean unfollowAuthor(String name){
        return neo4jDBManager.userDisLikesAuthor(Session.getInstance().getLoggedUser(), name);
    }
    /**
     * Sets the preferred genre for the logged-in user.
     *
     * @param genre The preferred genre to be set.
     * @return True if the preferred genre is set successfully, false otherwise.
     */
    public boolean setPreferredGenre(String genre){
        if(!categoriesList.contains(genre)){
            System.out.println("Choose a genre that exists");
            System.out.println(categoriesList);
        }
        return neo4jDBManager.userPrefersGenre(Session.getInstance().getLoggedUser(), genre);
    }
    /**
     * Retrieves a list of most rated authors based on the provided criteria.
     *
     * @param skip       The number of records to skip.
     * @param limit      The maximum number of records to retrieve.
     * @param numReviews The minimum number of reviews for an author to be considered.
     * @param score      The list of scores for filtering authors.
     * @return A list of most rated authors.
     */
    public List<String> getMostRatedAuthors(int skip, int limit, int numReviews, ArrayList<Double> score){
        return mongoDBManager.getMostRatedAuthors(skip,limit,numReviews,score);
    }
    /**
     * Retrieves a list of most active users based on the provided criteria.
     *
     * @param startDate The start date for the activity range.
     * @param endDate   The end date for the activity range.
     * @param skip      The number of records to skip.
     * @param limit     The maximum number of records to retrieve.
     * @param counts    The list of counts for filtering active users.
     * @return A list of most active users.
     */
    public List<String> getMostActiveUsers(String startDate,String endDate,int skip,int limit,ArrayList<Integer> counts){
        return mongoDBManager.getMostActiveUsers(startDate,endDate,skip,limit,counts);
    }

    /**
     * Retrieves a list of top categories based on the number of books published.
     *
     * @param skip  The number of records to skip.
     * @param limit The maximum number of records to retrieve.
     * @return A list of top categories.
     */
    public List<String> getTopCategoriesOfNumOfBookPublished(int skip,int limit){
        return mongoDBManager.getTopCategoriesOfNumOfBookPublished(skip,limit);
    }
    /**
     * Retrieves a list of most versatile users based on the provided criteria.
     *
     * @param skip  The number of records to skip.
     * @param limit The maximum number of records to retrieve.
     * @return A list of most versatile users.
     */
    public List<User> getMostVersatileUsers(int skip, int limit){
        if(skip<0||limit<=0){
            System.out.println("error number");
            return null;
        }
        return mongoDBManager.getMostVersatileUsers(skip,limit);
    }
    /**
     * Retrieves a list of top books based on the provided criteria.
     *
     * @param numReview   The minimum number of reviews for a book to be considered.
     * @param categories  The list of categories to filter books.
     * @param limit       The maximum number of records to retrieve.
     * @param skip        The number of records to skip.
     * @param scores      The list of scores for filtering books.
     * @return A list of top books.
     */
    public List<Book> getTopBooks(int numReview, List<String> categories, int limit, int skip, ArrayList<Double>scores){
        for (String cat:categoriesList){
            if(!categories.contains(cat)){
                System.out.println("some categories doesnt exists");
                return null;
            }
        }
        if(skip<0||limit<=0){
            System.out.println("error number");
            return null;
        }
        return mongoDBManager.getTopBooks(numReview,categories,limit,skip,scores);
    }
    /**
     * Retrieves a list of users with negative behavior based on the provided criteria.
     *
     * @param skip  The number of records to skip.
     * @param limit The maximum number of records to retrieve.
     * @return A list of users with negative behavior.
     */
    public List<User>getBadUsers(int skip,int limit){
        if(skip<0||limit<=0){
            System.out.println("error number");
            return null;
        }
        return mongoDBManager.getBadUsers(skip,limit);
    }
    /**
     * Retrieves a list of users based on the provided keyword and criteria.
     *
     * @param keyword The keyword to search for in user profiles.
     * @param admin   Indicates if the search is limited to admin users.
     * @param next    The starting point for pagination.
     * @return A list of users matching the search criteria.
     */
    public List<User> getUserByKeyword(String keyword,boolean admin,int next){
        return mongoDBManager.getUserByKeyword(keyword,admin,next);
    }
    /**
     * Retrieves a user by profileName and displays the profile information.
     *
     * @param profileName The profileName of the user to retrieve.
     * @return The retrieved user.
     */
    public User getUserByProfileName(String profileName){
        User user=mongoDBManager.getUserByProfileName(profileName);
        if(user==null){
            System.out.println("user doesnt exist");
            return null;
        }
        return user;
    }

    /**
     * Searches for books based on provided parameters and criteria.
     *
     * @param title      The title of the book to search for.
     * @param authors    The list of authors to filter books.
     * @param startDate  The start date for the publication date range.
     * @param endDate    The end date for the publication date range.
     * @param categories The list of categories to filter books.
     * @param skip       The number of records to skip.
     * @param limit      The maximum number of records to retrieve.
     * @return A list of books matching the search criteria.
     */
    public List<Book> searchBooksByParameters(String title, List<String> authors, String startDate, String endDate, List<String> categories, int skip, int limit) {
        if(skip<0||limit<=0){
            System.out.println("error number");
            return null;
        }
        if(categories!=null&&!categories.isEmpty()){
            for (String cat:categoriesList){
                if(!categories.contains(cat)){
                    System.out.println("some categories doesnt exists");
                    return null;
                }
            }
        }
        return mongoDBManager.searchBooksByParameters(title,authors,startDate,endDate,categories,skip,limit);
    }

    /**
     * Adds a book to the system.
     *
     * @param book The book to be added.
     * @return True if the book is added successfully, false otherwise.
     */
    public boolean addBook(Book book){
        try {
            ClientSession session=MongoDBDriver.getInstance().openConnection().startSession();
            session.startTransaction();
            if(mongoDBManager.addBook(book,session)){
                if(neo4jDBManager.addBook(book)){
                    session.commitTransaction();
                    session.close();
                    return true;
                }else {
                    session.abortTransaction();
                    session.close();
                    return false;
                }
            }else {
                session.abortTransaction();
                session.close();
                return false;
            }
        }catch (Exception e){
            System.out.println("error in add book controller");
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Deletes a user from the system.
     *
     * @param user The user to be deleted.
     * @return True if the user is deleted successfully, false otherwise.
     */
    public boolean deleteUser(User user){
        try {
            ClientSession session=MongoDBDriver.getInstance().openConnection().startSession();
            session.startTransaction();
            if(mongoDBManager.deleteUser(user,session)){
                if(neo4jDBManager.deleteUser(user)){
                    session.commitTransaction();
                    session.close();
                    return true;
                }else {
                    session.abortTransaction();
                    session.close();
                    return false;
                }
            }else {
                session.abortTransaction();
                session.close();
                return false;
            }
        }catch (Exception e){
            System.out.println("error in delete user controller");
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Retrieves a list of users with the most followers.
     *
     * @param limit The maximum number of users to retrieve.
     * @return A list of users with the most followers.
     */
    public List<String> getUsersWithMostFollowers(int limit) {
        if(limit<=0){
            System.out.println("limit must be positive");
            return null;
        }
        return neo4jDBManager.getUsersWithMostFollowers(limit);
    }
    /**
     * Generates recommendations based on authors liked by the logged-in user.
     *
     * @param limit The maximum number of recommendations to generate.
     * @return A list of recommended authors.
     */
    public List<String> recommendationBasedOnAuthorsLiked(int limit) {
        if(limit<=0){
            System.out.println("limit must be positive");
            return null;
        }
        return neo4jDBManager.recommendationBasedOnAuthorsLiked(Session.getInstance().getLoggedUser(), limit);
    }
    /**
     * Generates recommendations for users with the most followers among the logged-in user's followings.
     *
     * @param limit The maximum number of recommendations to generate.
     * @return A list of recommended users.
     */
    public List<User> recommendUserWithMostFollowersOfFollowings(int limit) {
        if(limit<=0){
            System.out.println("limit must be positive");
            return null;
        }
        return neo4jDBManager.recommendUserWithMostFollowersOfFollowings(Session.getInstance().getLoggedUser(), limit);
    }
    /**
     * Generates recommendations for books based on friends' comments and the preferred genre of the logged-in user.
     *
     * @param limit The maximum number of recommendations to generate.
     * @return A list of recommended books.
     */
    public List<String> recommendBooksBasedOnFriendsCommentsAndPreferredGenre(int limit) {
        if(limit<=0){
            System.out.println("limit must be positive");
            return null;
        }
        return neo4jDBManager.recommendBooksBasedOnFriendsCommentsAndPreferredGenre(Session.getInstance().getLoggedUser(), limit);
    }
    /**
     * Generates recommendations for books based on friends' comments.
     *
     * @param limit The maximum number of recommendations to generate.
     * @return A list of recommended books.
     */
    public List<String> recommendBooksBasedOnFriendsComments(int limit) {
        if(limit<=0){
            System.out.println("limit must be positive");
            return null;
        }
        return neo4jDBManager.recommendBooksBasedOnFriendsComments(Session.getInstance().getLoggedUser(), limit);
    }
    /**
     * Retrieves a review by ISBN and profileName.
     *
     * @param ISBN        The ISBN of the book.
     * @param profileName The profileName of the user.
     * @return The review matching the given ISBN and profileName.
     */
    public Review getReviewByISBNAndProfileName(String ISBN,String profileName){
        return mongoDBManager.getReviewByISBNAndProfileName(ISBN,profileName);

    }
    /**
     * Generates recommendations for popular books based on the user's preferred genre.
     *
     * @param limit     The maximum number of recommendations to generate.
     * @param numReviews The minimum number of reviews for a book to be considered popular.
     * @return A list of recommended popular books.
     */
    public List<String> recommendPopularBooksByGenre(int limit,int numReviews) {
        String genre=neo4jDBManager.getUserPreferredGenre(Session.getInstance().getLoggedUser());
        if(genre==null||genre.isEmpty()){
            return null;
        }
        return neo4jDBManager.recommendPopularBooksByGenre(genre,limit,numReviews );
    }

}
