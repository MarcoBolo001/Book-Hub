package it.unipi.lsmsd.Controller;

import it.unipi.lsmsd.Model.Session;
import it.unipi.lsmsd.Model.User;
import it.unipi.lsmsd.Persistence.MongoDBDriver;
import it.unipi.lsmsd.Persistence.MongoDBManager;

public class LoginController {
    private MongoDBManager mongoDBManager;
    /**
     * Initializes the MongoDB manager.
     */
    public void initalize(){
        mongoDBManager=new MongoDBManager(MongoDBDriver.getInstance().openConnection());
    }
    /**
     * Checks user credentials for login.
     *
     * @param profileName The profile name of the user.
     * @param password    The password for authentication.
     * @return True if the credentials are valid and the user is logged in, false otherwise.
     */
    public boolean checkCredentials(String profileName,String password){
        if(profileName.isEmpty()||password.isEmpty()){
            System.out.println("profileName or password are empty");
            return false;
        }
        User user=mongoDBManager.login(profileName,password);
        if(user==null){
            System.out.println("user doesn't exist, sign up first or password is not correct");
            return false;
        }
        Session.getInstance().setLoggedUser(user);
        return true;
    }
}
