package it.unipi.lsmsd.Model;

public class Session {
    private static Session instance=null;
    private User loggedUser;
    public static Session getInstance() {
        if(instance==null){
            instance=new Session();
        }
        return instance;
    }
    public static void resetInstance() {
        instance = null;
    }
    private Session(){
    }
    public void setLoggedUser(User user) {
        instance.loggedUser = user;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

}
