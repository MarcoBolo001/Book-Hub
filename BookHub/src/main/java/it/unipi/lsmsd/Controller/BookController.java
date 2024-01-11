package it.unipi.lsmsd.Controller;

import com.mongodb.client.ClientSession;
import it.unipi.lsmsd.Model.Book;
import it.unipi.lsmsd.Model.Review;
import it.unipi.lsmsd.Persistence.MongoDBDriver;
import it.unipi.lsmsd.Persistence.MongoDBManager;
import it.unipi.lsmsd.Persistence.Neo4jDBDriver;
import it.unipi.lsmsd.Persistence.Neo4jDBManager;

import java.util.List;

public class BookController {
    private MongoDBManager mongoDBManager;
    private Neo4jDBManager neo4jDBManager;
    /**
     * Initializes the MongoDB and Neo4j database managers.
     */
    public void initialize(){
        mongoDBManager= new MongoDBManager(MongoDBDriver.getInstance().openConnection());
        neo4jDBManager= new Neo4jDBManager(Neo4jDBDriver.getInstance().openConnection());
    }
    /**
     * Adds a review to both MongoDB and Neo4j databases.
     *
     * @param book   The book for which the review is being added.
     * @param review The review to be added.
     * @return True if the review is successfully added to both databases, false otherwise.
     */
    public boolean addReview(Book book, Review review) {
        try{
            ClientSession session=MongoDBDriver.getInstance().openConnection().startSession();
            session.startTransaction();
            if(mongoDBManager.addReview(book,review,session)){
                if(neo4jDBManager.createUserBookReview(review)){
                    session.commitTransaction();
                    return true;
                }else{
                    session.abortTransaction();
                    return false;
                }
            }else {
                session.abortTransaction();
                return false;
            }
        }catch (Exception e){
            System.out.println("error in add review controller");
            e.printStackTrace();
            return false;
        }
    }
    /**
     * Deletes a review from MongoDB.
     *
     * @param book   The book for which the review is being deleted.
     * @param review The review to be deleted.
     */
    public void deleteReview(Book book,Review review){
        mongoDBManager.deleteReview(book,review);
    }
    /**
     * Retrieves a book from MongoDB based on its ISBN.
     *
     * @param ISBN The ISBN of the book to retrieve.
     * @return The Book object representing the book with the specified ISBN, or null if not found.
     */
    public Book getBookByISBN(String ISBN){
        return mongoDBManager.getBookByISBN(ISBN);
    }


}
