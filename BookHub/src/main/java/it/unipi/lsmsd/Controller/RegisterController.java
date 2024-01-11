package it.unipi.lsmsd.Controller;

import com.mongodb.client.ClientSession;
import it.unipi.lsmsd.Model.User;
import it.unipi.lsmsd.Persistence.MongoDBDriver;
import it.unipi.lsmsd.Persistence.MongoDBManager;
import it.unipi.lsmsd.Persistence.Neo4jDBDriver;
import it.unipi.lsmsd.Persistence.Neo4jDBManager;

import java.util.ArrayList;

public class RegisterController {
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
     * Registers a new user by adding them to both MongoDB and Neo4j databases.
     *
     * @param profileName The profile name of the new user.
     * @param password    The password for the new user.
     * @return True if the user is successfully registered in both databases, false otherwise.
     */
    public boolean signUp(String profileName,String password){
        if(password.isEmpty()||profileName.isEmpty()){
            System.out.println("empty profileName or password");
            return false;
        }
        User user=mongoDBManager.getUserByProfileName(profileName);
        if(user!=null){
            System.out.println("profileName already exist");
            return false;
        }
        User adder=new User(profileName,password,0,new ArrayList<>());
        try(ClientSession session= MongoDBDriver.getInstance().openConnection().startSession()){
            session.startTransaction();
            if(mongoDBManager.addUser(adder,session)){
                if(neo4jDBManager.addUser(adder)){
                    session.commitTransaction();
                    session.close();
                    return true;
                }else{
                    session.abortTransaction();
                    session.close();
                    return false;
                }
            }else{
                session.abortTransaction();
                session.close();
                return false;
            }
        }catch (Exception e){
            System.out.println("error in sign up");
            e.printStackTrace();
            return false;
        }

    }

}
