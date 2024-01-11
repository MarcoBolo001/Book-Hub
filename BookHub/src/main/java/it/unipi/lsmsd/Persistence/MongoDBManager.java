package it.unipi.lsmsd.Persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import it.unipi.lsmsd.Model.Book;
import it.unipi.lsmsd.Model.Review;
import it.unipi.lsmsd.Model.User;
import org.bson.BsonDateTime;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Accumulators.first;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

public class MongoDBManager {
    public MongoDatabase db;
    private final MongoCollection<Document> userCollection;
    private final MongoCollection<Document> bookCollection;
    private final MongoCollection<Document> reviewCollection;
    private static final String reviewDeleted="Review deleted by admin because it doesn't follow the politeness";
    /**
     * Initializes a MongoDBManager with the provided MongoClient, connecting to the "bookHubMongo" database.
     *
     * @param client The MongoClient used to connect to the MongoDB server.
     */
    public MongoDBManager(MongoClient client){
        this.db= client.getDatabase("bookHubMongo");
        userCollection=db.getCollection("users");
        bookCollection=db.getCollection("book");
        reviewCollection=db.getCollection("reviews");
    }
    /**
     * Attempts to log in a user by verifying the provided profile name and password.
     *
     * @param profileName The user's profile name.
     * @param password The user's password.
     * @return The User object representing the logged-in user, or null if login fails.
     */
    public User login(String profileName, String password){
        Document result= userCollection.find(Filters.and(
                eq("profileName",profileName),
                eq("password",password))).first();
        Gson gson=new Gson();
        return gson.fromJson(gson.toJson(result), User.class);
    }
    /**
     * Checks the existence of a user with the given profile name in the user collection.
     *
     * @param profileName The profile name to check for existence.
     * @return The User object representing the existing user, or null if the user does not exist.
     */
    private User checkExistence(String profileName){
        Document result= userCollection.find(Filters.and(
                eq("profileName",profileName))).first();
        Gson gson=new Gson();
        return gson.fromJson(gson.toJson(result), User.class);
    }
    /**
     * Adds a new user to the user collection in the MongoDB database.
     *
     * @param user    The User object representing the user to be added.
     * @param session The MongoDB ClientSession to be used for the transaction.
     * @return true if the user is successfully added, false otherwise.
     */
    public boolean addUser(User user, ClientSession session){
        try{
            if(user.getprofileName().isEmpty()||user.getPassword().isEmpty()){
                System.out.println("Enter a good profileName and Password");
            }
            User exists=checkExistence(user.getprofileName());
            if(exists !=null){
                System.out.println("profileName already exists");
                return false;
            }
            Document document= new Document("profileName", user.getprofileName()).append("password", user.getPassword()).append("type",user.getType()).append("last_reviews",user.getLast_reviews());
            userCollection.insertOne(session,document);
            return true;
        }catch (Exception e){
            System.out.println("problems in insert in db of new user register");
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Deletes a user and related data from the MongoDB database.
     *
     * @param user    The User object representing the user to be deleted.
     * @param session The MongoDB ClientSession to be used for the transaction.
     * @return true if the user is successfully deleted, false otherwise.
     */

    public boolean deleteUser(User user,ClientSession session){
        try{
            if(checkExistence(user.getprofileName())==null){
                System.out.println("user doesn't exist");
                return false;
            }
            Bson find=eq("profileName",user.getprofileName());
            userCollection.deleteOne(session,find);
            Bson findB=eq("last_users_review.profileName",user.getprofileName());
            Bson updateB= Updates.pull("last_users_review",eq("profileName",user.getprofileName()));
            bookCollection.updateMany(session,findB,updateB);
            reviewCollection.deleteMany(session,find);
            return true;
        }catch (Exception e){
            System.out.println("problems with deleting the user");
            e.printStackTrace();
            return false;
        }
        //dipende
    }
    /**
     * Adds a new book to the MongoDB database.
     *
     * @param book    The Book object representing the book to be added.
     * @param session The MongoDB ClientSession to be used for the transaction.
     * @return true if the book is successfully added, false otherwise.
     */
    public boolean addBook(Book book,ClientSession session){
        Document result=bookCollection.find(eq("ISBN",book.getISBN())).first();
        if(result!=null){
            System.out.println("book already exists for the ISBN");
            return false;
        }
        if(book.getISBN().isEmpty()
                ||book.getISBN()==null
                ||book.getAuthors().isEmpty()
                ||book.getAuthors()==null
                ||book.getDescription().isEmpty()
                ||book.getDescription()==null
                ||book.getCategories().isEmpty()
                ||book.getCategories()==null
                ||book.getLast_users_review()==null
                ||book.getPublishedDate()==null
                ||book.getTitle().isEmpty()
                ||book.getTitle()==null){
            System.out.println("Give all parameters to the book");
            return false;
        }
        try{
            Document document=new Document("ISBN",book.getISBN())
                    .append("Title",book.getTitle())
                    .append("description",book.getDescription())
                    .append("authors",book.getAuthors())
                    .append("categories",book.getCategories())
                    .append("publishedDate",new java.util.Date(book.getPublishedDate().getTime()))
                    .append("last_users_review",book.getLast_users_review());
            bookCollection.insertOne(session,document);
            return true;
        }catch (Exception e){
            System.out.println("problems with add book");
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Updates the password of an existing user in the user collection.
     *
     * @param user The User object representing the user with the updated password.
     * @return true if the user's password is successfully updated, false otherwise.
     */
    public boolean updateUser(User user){
        try{
            if (user.getPassword().isEmpty()||checkExistence(user.getprofileName())==null) {
                System.out.println("password empty or user doesn't exist");
                return false;
            }
            Document document = new Document("password", user.getPassword());
            Bson update = new Document("$set", document);
            userCollection.updateOne(new Document("profileName", user.getprofileName()), update);
            return true;
        }catch (Exception e){
            System.out.println("problems with update user password");
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Retrieves a user from the user collection based on the provided profile name.
     *
     * @param profileName The profile name of the user to retrieve.
     * @return The User object representing the user with the specified profile name, or null if not found.
     */
    public User getUserByProfileName(String profileName){
        return checkExistence(profileName);
    }
    /**
     * Adds a new review to the review collection and updates related fields in the user and book collections.
     *
     * @param book The Book object representing the book being reviewed.
     * @param review The Review object representing the new review to be added.
     * @param session The MongoDB ClientSession to be used for the transaction.
     * @return true if the review is successfully added, false otherwise.
     */
    public boolean addReview(Book book, Review review, ClientSession session) {
        try {
            Document result = reviewCollection.find(Filters.and(
                    eq("profileName", review.getProfileName()),
                    eq("ISBN", review.getISBN()))).first();

            if (result != null) {
                System.out.println("Review for that user already exists");
                return false;
            }

            Date reviewDate = review.getTime();

            Document document_last_users_review = new Document("profileName", review.getProfileName())
                    .append("time", new java.util.Date(reviewDate.getTime())) // Convert to BSON date
                    .append("score", review.getScore())
                    .append("review", review.getReview());

            Document document_last_review = new Document("ISBN", review.getISBN())
                    .append("Title", review.getTitle())
                    .append("score", review.getScore())
                    .append("time", new java.util.Date(reviewDate.getTime())) // Convert to BSON date
                    .append("review", review.getReview());

            Document document_review = new Document("ISBN", review.getISBN())
                    .append("Title", review.getTitle())
                    .append("profileName", review.getProfileName())
                    .append("score", review.getScore())
                    .append("time", new java.util.Date(reviewDate.getTime())) // Convert to BSON date
                    .append("review", review.getReview())
                    .append("categories", review.getCategories())
                    .append("authors", review.getAuthors());

            reviewCollection.insertOne(session, document_review);

            Document filter_book = new Document("ISBN", book.getISBN());

            Document update_last_users_review = new Document("$push",
                    new Document("last_users_review",
                            new Document("$each", Arrays.asList(document_last_users_review))
                                    .append("$position", 0)
                                    .append("$slice", 5)));

            Document filter_user = new Document("profileName", review.getProfileName());
            Document update_last_review = new Document("$push",
                    new Document("last_reviews",
                            new Document("$each", Arrays.asList(document_last_review))
                                    .append("$position", 0)
                                    .append("$slice", 5)));

            userCollection.updateOne(session, filter_user, update_last_review);
            bookCollection.updateOne(session, filter_book, update_last_users_review);

            return true;
        } catch (Exception e) {
            System.out.println("Problems with insert of a comment");
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Deletes a review from the review collection and updates related fields in the user and book collections.
     *
     * @param book The Book object representing the book of the review to be deleted.
     * @param review The Review object representing the review to be deleted.
     */
    public void deleteReview(Book book,Review review){
        Document findB=new Document("ISBN",book.getISBN())
                .append("last_users_review",
                        new Document("$elemMatch",
                                new Document("profileName",review.getProfileName())
                                        ));
        Document findR=new Document("ISBN",book.getISBN())
                .append("profileName",review.getProfileName());
        Document findU=new Document("profileName",review.getProfileName())
                .append("last_reviews",
                        new Document("$elemMatch",
                                new Document("ISBN",book.getISBN())
                                        ));
        Document updateR=new Document("$set",
                new Document("review",reviewDeleted));
        UpdateResult updateResult=reviewCollection.updateOne(findR,updateR);
        if(updateResult.getModifiedCount()==0){
            System.out.println("no updated in review because not found review");
            return;
        }
        Document updateUReview=new Document("$set",
                new Document("last_reviews.$.review",reviewDeleted));
        updateResult=userCollection.updateOne(findU,updateUReview);
        if(updateResult.getModifiedCount()==0){
            System.out.println("user not modified maybe because in user there was no review in his last 5");
        }
        Document updateBReview=new Document("$set",
                new Document("last_users_review.$.review",reviewDeleted));
        updateResult=bookCollection.updateOne(findB,updateBReview);
        if(updateResult.getModifiedCount()==0){
            System.out.println("book has no that review maybe is not in the last 5 of that book");
        }
    }

    /**
     * Retrieves a book from the book collection based on the provided ISBN.
     *
     * @param ISBN The ISBN of the book to retrieve.
     * @return The Book object representing the book with the specified ISBN, or null if not found.
     */
    public Book getBookByISBN(String ISBN){
        try {
            if(ISBN.isEmpty()){
                System.out.println("ISBN empty");
                return null;
            }
            Book b;
            Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
            Document document= bookCollection.find(and(
                    eq("ISBN",ISBN))).first();
            b=gson.fromJson(gson.toJson(document), Book.class);
            return b;
        }catch (JsonSyntaxException e){
            System.out.println("problems with conversion in the getBookByISBN");
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Retrieves a review from the review collection based on the provided ISBN and profile name.
     *
     * @param ISBN        The ISBN of the book associated with the review.
     * @param profileName The profile name of the person who wrote the review.
     * @return The Review object representing the review with the specified ISBN and profile name,
     *         or null if not found or if ISBN or profileName is empty.
     */
    public Review getReviewByISBNAndProfileName(String ISBN,String profileName){
        try {
            if(ISBN.isEmpty()||profileName.isEmpty()){
                System.out.println("empty ISBN or profileName");
                return null;
            }
            Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss").create();
            Document document=new Document("ISBN",ISBN)
                    .append("profileName",profileName);
            Document result=reviewCollection.find(document).first();
            return gson.fromJson(gson.toJson(result), Review.class);
        }catch (JsonSyntaxException e){
            System.out.println("problem with parse json");
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Searches for books in the book collection based on specified parameters.
     *
     * @param title The title of the book (partial match).
     * @param authors The list of authors.
     * @param startDate The start date for filtering books by published date.
     * @param endDate The end date for filtering books by published date.
     * @param categories The list of categories.
     * @param skip The number of documents to skip in the result set.
     * @param limit The maximum number of documents to return.
     * @return A list of Book objects matching the specified parameters, or null if no matching books are found.
     */
    public List<Book> searchBooksByParameters(String title, List<String> authors, String startDate, String endDate, List<String> categories, int skip, int limit) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        List<Bson>pipeline=new ArrayList<>();
        if(title!=null&&!title.isEmpty()){
            Pattern pattern=Pattern.compile("^.*" + title + ".*$");
            pipeline.add(Aggregates.match(Filters.or
                    (Filters.regex("Title",pattern),
                            Filters.eq("Title",title))));
        }
        if (authors != null && !authors.isEmpty()) {
            pipeline.add(Aggregates.match(Filters.in("authors", authors)));
        }
        if (categories != null && !categories.isEmpty()) {
            pipeline.add(Aggregates.match(Filters.in("categories", categories)));
        }
        if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date start = dateFormat.parse(startDate);
                Date end = dateFormat.parse(endDate);
                pipeline.add(Aggregates.match(Filters.and(
                        Filters.gte("publishedDate",
                                new BsonDateTime(start.getTime())),
                        Filters.lte("publishedDate",
                                new BsonDateTime(end.getTime()))
                )));
            } catch (ParseException e) {
                System.out.println("problems with parse the date in findBooksByParameters");
                e.printStackTrace();
                return null;
            }
        }
        pipeline.add((sort(descending("publishedDate"))));
        pipeline.add(skip(skip));
        pipeline.add(limit(limit));
        List<Document> result = bookCollection.aggregate(pipeline).into(new ArrayList<>());
        if(result.isEmpty()){
            return null;
        }
        return gson.fromJson(gson.toJson(result),new TypeToken<List<Book>>(){}.getType());
    }
    /**
     * Searches for users in the user collection based on a keyword in the profile name.
     *
     * @param keyword The keyword to search for in profile names (case-insensitive, partial match).
     * @param admin True if searching for admin users, false for all users.
     * @param next The page number for paginated results (starting from 0).
     * @return A list of User objects matching the specified keyword and criteria, or an empty list if no matching users are found.
     */
    public List<User> getUserByKeyword(String keyword,boolean admin,int next){
        List<User> results=new ArrayList<>();
        Gson gson= new GsonBuilder().serializeSpecialFloatingPointValues().create();
        Consumer<Document> converter=document -> {
            User user=gson.fromJson(gson.toJson(document), User.class);
            results.add(user);
        };
        Pattern pattern=Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
        Bson filter=Aggregates.match(
                Filters.or(
                        Filters.regex("profileName",pattern),
                        Filters.eq("profileName",keyword)));
        if(admin){
            userCollection.aggregate(Arrays.asList(filter,match(eq("type",1)),skip(next*5),limit(5))).forEach(converter);
        }else{
            userCollection.aggregate(Arrays.asList(filter,skip(next*5),limit(5))).forEach(converter);
        }
        return results;
    }
    /**
     * Retrieves a list of users who have posted reviews marked as "deleted."
     *
     * @param skip The number of documents to skip in the result set.
     * @param limit The maximum number of documents to return.
     * @return A list of User objects representing users with deleted reviews, or an empty list if no matching users are found.
     */
    public List<User>getBadUsers(int skip,int limit){
        List<User> results=new ArrayList<>();
        ArrayList<Document>pipeline=new ArrayList<>();
        pipeline.add(new Document("$match",
                new Document("review",reviewDeleted)));
        pipeline.add(new Document("$group",
                new Document("_id","profileName").append("count",
                        new Document("$sum",1))));
        pipeline.add(new Document("$sort",
                new Document("count",-1)));
        pipeline.add(new Document("$project",
                new Document("profileName","$_id").append("count",1).append("_id",0)));
        pipeline.add(new Document("$skip",skip));
        pipeline.add(new Document("$limit",limit));
        Iterable<Document>result=reviewCollection.aggregate(pipeline);
        for(Document document:result){
            User user=getUserByProfileName(document.getString("profileName"));
            if(user!=null){
                results.add(user);
            }
        }
        return results;
    }
    /**
     * Retrieves a list of top-rated books based on the specified criteria.
     *
     * @param numReview The minimum number of reviews a book must have to be considered.
     * @param categories The list of categories to filter books.
     * @param limit The maximum number of books to return.
     * @param skip The number of books to skip in the result set.
     * @param scores An ArrayList to store the average scores corresponding to each retrieved book.
     * @return A list of Book objects representing top-rated books, or an empty list if no matching books are found.
     */
    public List<Book> getTopBooks(int numReview,List<String> categories,int limit,int skip,ArrayList<Double>scores){
        List<Book> results=new ArrayList<>();
        List<Document>pipeline=new ArrayList<>();
        if(categories!=null&&categories.isEmpty()){
            pipeline.add(new Document("$match",
                    new Document("categories",
                            new Document("$in",categories))));
        }
        pipeline.addAll(Arrays.asList(
                new Document("$group",
                        new Document("_id","$ISBN")
                                .append("averageScore",
                                        new Document("$avg","$score"))
                                .append("totalReviews",
                                        new Document("$sum",1))),
                new Document("$match",
                        new Document("totalReviews",
                                new Document("$gte",numReview))),
                new Document("$sort",
                        new Document("averageScore",-1)),
                new Document("$project",
                        new Document("ISBN","$_id")
                                .append("averageScore",1)
                                .append("totalReviews",1)
                                .append("_id",0)),
                new Document("$skip",skip),
                new Document("$limit",limit)
        ));
        AggregateIterable<Document> documentAggregateIterable  =reviewCollection.aggregate(pipeline);
        for(Document document:documentAggregateIterable){
            results.add(getBookByISBN(document.getString("ISBN")));
            scores.add(document.getDouble("averageScore"));
        }
        return results;
    }
    /**
     * Retrieves a list of the most versatile users based on the number of unique book categories they have reviewed.
     *
     * @param skip The number of users to skip in the result set.
     * @param limit The maximum number of users to return.
     * @return A list of User objects representing the most versatile users, or an empty list if no matching users are found.
     */
    public List<User> getMostVersatileUsers(int skip, int limit){
        List<Document> pipeline=Arrays.asList(
                new Document("$group",
                        new Document("_id","$profileName")
                                .append("uniqueCategories",
                                        new Document("$addToSet","$categories"))),
                new Document("$project",
                        new Document("profileName","$_id")
                                .append("uniqueCategories","$uniqueCategories")
                                .append("numUniqueCategories",
                                        new Document("$size","$uniqueCategories"))),
                new Document("$sort",
                        new Document("numUniqueCategories",-1)),
                new Document("$skip",skip),
                new Document("$limit",limit)
        );
        AggregateIterable<Document> documentAggregateIterable=reviewCollection.aggregate(pipeline);
        ArrayList<User> result=new ArrayList<>();
        for(Document document:documentAggregateIterable){
            result.add(getUserByProfileName(document.getString("profileName")));
        }
        return result;
    }
    /**
     * Retrieves a list of the top book categories based on the number of books published in each category.
     *
     * @param skip The number of categories to skip in the result set.
     * @param limit The maximum number of categories to return.
     * @return A list of strings representing the top book categories, or an empty list if no matching categories are found.
     */
    public List<String> getTopCategoriesOfNumOfBookPublished(int skip,int limit){
        List<Document> pipeline=Arrays.asList(
                new Document("$unwind","$categories"),
                new Document("$group",
                        new Document("_id","$categories")
                                .append("count",
                                        new Document("$sum",1))),
                new Document("$sort",
                        new Document("count",-1)),
                new Document("$skip",skip),
                new Document("$limit",limit)
        );
        AggregateIterable<Document> documentAggregateIterable= bookCollection.aggregate(pipeline);
        List<String> results=new ArrayList<>();
        for(Document document:documentAggregateIterable){
            results.add(document.getString("_id"));
        }
        return results;

    }
    /**
     * Retrieves a list of the most active users based on the number of reviews posted within a specified date range.
     *
     * @param startDate The start date of the review period.
     * @param endDate The end date of the review period.
     * @param skip The number of users to skip in the result set.
     * @param limit The maximum number of users to return.
     * @param counts An ArrayList to store the review counts corresponding to each retrieved user.
     * @return A list of strings representing the most active users, or null if an error occurs.
     */
    public List<String> getMostActiveUsers(String startDate,String endDate,int skip,int limit,ArrayList<Integer> counts){

        List<Document> pipeline;

        if (startDate != null && endDate != null && !startDate.isEmpty() && !endDate.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date start = dateFormat.parse(startDate);
                Date end = dateFormat.parse(endDate);
                pipeline=Arrays.asList(
                        new Document("$match",
                                new Document("time",
                                        new Document("$gte",start)
                                                .append("$lte",end))),
                        new Document("$group",
                                new Document("_id","$profileName")
                                        .append("count",
                                                new Document("$sum",1))),
                        new Document("$sort",
                                new Document("count",-1)),
                        new Document("$project",
                                new Document("_id",0)
                                        .append("profileName","$_id")
                                        .append("reviewCount","$count")),
                        new Document("$skip",skip),
                        new Document("$limit",limit)
                );
            } catch (ParseException e) {
                System.out.println("problems with parse the date in getMostActiveUsers");
                e.printStackTrace();
                return null;
            }
            AggregateIterable<Document> results=reviewCollection.aggregate(pipeline);
            List<String> topReviewersName=new ArrayList<>();
            for (Document document:results){
                String profileName=document.getString("profileName");
                topReviewersName.add(profileName);
                counts.add(document.getInteger("reviewCount"));
            }
            return topReviewersName;
        }else{
            System.out.println("enter a good start and end date");
            return null;
        }
    }
    /**
     * Retrieves a list of the most rated authors based on the average score and minimum number of reviews for their books.
     *
     * @param skip The number of authors to skip in the result set.
     * @param limit The maximum number of authors to return.
     * @param numReviews The minimum number of reviews required for an author's book to be considered.
     * @param score An ArrayList to store the average scores corresponding to each retrieved author.
     * @return A list of strings representing the most rated authors, or an empty list if no matching authors are found.
     */
    public List<String> getMostRatedAuthors(int skip,int limit,int numReviews,ArrayList<Double>score){
        List<String> results= new ArrayList<>();
        List<Document> pipeline=Arrays.asList(
                new Document("$unwind","$authors"),
                new Document("$group",
                        new Document("_id","$authors")
                                .append("totalReviews",
                                        new Document("$sum",1))
                                .append("avgScore",
                                        new Document("$avg","$score"))),
                new Document("$match",
                        new Document("totalReviews",
                                new Document("$gte",numReviews))),
                new Document("$sort",
                        new Document("avgScore",-1)),
                new Document("$project",
                        new Document("author","$_id")
                                .append("totalReviews",1)
                                .append("avgScore",1)
                                .append("_id",0)),
                new Document("$skip",skip),
                new Document("$limit",limit)
        );
        AggregateIterable<Document> documentAggregateIterable= reviewCollection.aggregate(pipeline);
        for (Document document:documentAggregateIterable){
            results.add(document.getString("author"));
            score.add(document.getDouble("avgScore"));
        }
        return results;
    }
    /**
     * Retrieves a sorted list of unique book categories from the collection.
     *
     * @return A list of strings representing unique book categories in sorted order, or an empty list if no categories are found.
     */
    public List<String> getCategories(){
        List<String> categories=new ArrayList<>();
        List<Bson> pipeline = Arrays.asList(
                unwind("$categories"),
                group("$categories", first("dummyField", "$categories")),  // Adding a dummy field
                project(Projections.fields(Projections.exclude("dummyField")))  // Project to remove the dummy field
        );
        for (Document result : bookCollection.aggregate(pipeline)) {
            categories.add(result.getString("_id"));
        }
        Collections.sort(categories);
        return categories;
    }
}
