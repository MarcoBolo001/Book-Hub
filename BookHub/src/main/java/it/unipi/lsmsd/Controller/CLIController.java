package it.unipi.lsmsd.Controller;

import it.unipi.lsmsd.Model.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class CLIController {
    private static final LoginController loginController = new LoginController();
    private static final  RegisterController registerController = new RegisterController();
    private static final UserController userController = new UserController();
    private static final BookController bookController = new BookController();
    private static final Scanner scanner=new Scanner(System.in);
    /**
     * Initializes the application by setting up controllers.
     * Called at the start of the application.
     */
    private static void init(){

        loginController.initalize();
        registerController.initialize();
        userController.initialize();
        bookController.initialize();
        System.out.println("********************************************");
        System.out.println("*            Welcome to BOOK-HUB           *");
        System.out.println("*   Your Ultimate Destination for Reading  *");
        System.out.println("*                                          *");
        System.out.println("* Discover, Explore, and Immerse Yourself  *");
        System.out.println("*    in the Enchanting World of Books      *");
        System.out.println("*                                          *");
        System.out.println("********************************************");
        System.out.println("Hello, Reader! Ready to dive into the world of books?");
        System.out.println("Use the menu options to explore different features.");
        System.out.println("Enter the corresponding number to make your choice.");
        System.out.println();
    }
    /**
     * Starts the main application loop.
     * Handles menus based on the user's login status and type.
     */
    public static void startApplication(){
        init();
        boolean finish=false;
        do {
            if (Session.getInstance().getLoggedUser() == null) {
                finish=menuUnreg();
                continue;
            }
            switch (Session.getInstance().getLoggedUser().getType()) {
                case 0: {
                    System.out.println("Welcome, Dear user:"+Session.getInstance().getLoggedUser().getprofileName()+"!");
                    menuReg();
                    break;
                }
                case 1: {
                    System.out.println("Welcome, Dear admin:"+Session.getInstance().getLoggedUser().getprofileName()+"!");
                    menuAdmin();
                    break;
                }
                default:
                    System.out.println("Unknown user type");
                    break;
            }
        } while (!finish);
    }
    /**
     * Displays a menu for unregistered users, providing options such as login, sign up,
     * book search, and exit. Executes the selected option until the user chooses to exit.
     *
     * @return A boolean indicating whether the user chose to exit (true) or not (false).
     */
    private static boolean menuUnreg(){
        while (true){
            System.out.println("1-Login");
            System.out.println("2-Sign Up");
            System.out.println("3-Search for Books");
            System.out.println("4-Exit");
            System.out.println("------------");
            System.out.print("Please enter your choice: ");

            switch (Integer.parseInt(scanner.nextLine())){
                case 1:{
                    System.out.print("username:");
                    String name=scanner.nextLine();
                    System.out.print("password:");
                    String pass=scanner.nextLine();
                    if(loginController.checkCredentials(name,pass)){
                        return false;
                    }
                    break;
                }
                case 2:{
                    System.out.print("username:");
                    String name=scanner.nextLine();
                    System.out.print("password:");
                    String pass=scanner.nextLine();
                    if(registerController.signUp(name,pass)){
                        System.out.println("Now you are registered");
                    }else{
                        System.out.println("Sign up failed");
                    }
                    break;
                }
                case 3:{
                    System.out.print("Enter book title to find:");
                    String bookTitleToFind = scanner.nextLine();
                    System.out.print("From date:");
                    String startDate = scanner.nextLine();
                    System.out.print("To Date:");
                    String endDate = scanner.nextLine();
                    System.out.print("List of Authors(Separated by ,):");
                    List<String> authors;
                    String author=scanner.nextLine();
                    if(author.isEmpty()){
                        authors=null;
                    }else {
                        authors= Arrays.asList(author.split(","));
                    }
                    System.out.print("List of categories(Separated by ,):");
                    List<String> categories;
                    String category=scanner.nextLine();
                    if(category.isEmpty()){
                        categories=null;
                    }else {
                        categories= Arrays.asList(category.split(","));
                    }
                    List<Book> books = userController.searchBooksByParameters(bookTitleToFind, authors, startDate, endDate, categories, 0, 5);
                    if (books == null || books.isEmpty()) {
                        System.out.println("No book in the DB using these parameters");
                        break;
                    }
                    for (Book book : books) {
                        System.out.println("=== Book ===");
                        System.out.println("ISBN: " + book.getISBN());
                        System.out.println("Title: " + book.getTitle());
                        System.out.println("Description: " + book.getDescription());
                        System.out.println("Authors: " + book.getAuthors());
                        System.out.println("Categories: " + book.getCategories());
                        System.out.println("Published Date: " + book.getPublishedDate());

                        List<LastUserReviews> lastUserReviews = book.getLast_users_review();
                        System.out.println("=== Last User Reviews ===");

                        if (lastUserReviews == null || lastUserReviews.isEmpty()) {
                            System.out.println("No last user reviews available.");
                        } else {
                            for (LastUserReviews userReview : lastUserReviews) {
                                System.out.println("---- User Review ----");
                                System.out.println("Profile Name: " + userReview.getProfileName());
                                System.out.println("Time: " + userReview.getTime());
                                System.out.println("Score: " + userReview.getScore());
                                System.out.println("Review: " + userReview.getReview());
                                System.out.println("----------------------");
                            }
                        }

                        System.out.println("====================");
                    }
                    break;
                }

                case 4: {
                    System.out.println("********************************************");
                    System.out.println("*          Thank you for visiting          *");
                    System.out.println("*               BOOK-HUB                   *");
                    System.out.println("*     We hope you had an amazing time      *");
                    System.out.println("*      exploring the world of books!       *");
                    System.out.println("*                                          *");
                    System.out.println("*         Keep reading and come back       *");
                    System.out.println("*              soon. Goodbye!              *");
                    System.out.println("********************************************");
                    userController.close();
                    return true;
                }
                default:{
                    System.out.println("Invalid choice. Please try again.");
                    break;
                }
            }
        }
    }
    /**
     * Displays the menu for a registered user.
     * Provides various options such as searching for books, users, adding reviews,
     * changing password, managing preferences, following/unfollowing authors and users,
     * and getting book recommendations based on friends' activities.
     */
    private static void menuReg(){
        while (true){
            System.out.println("0-Show following and/or Followers");
            System.out.println("1-Search book");
            System.out.println("2-Search user");
            System.out.println("3-Show profile");
            System.out.println("4-Add review");
            System.out.println("5-Change Password");
            System.out.println("6-Add Your Preferred genre");
            System.out.println("7-Follow an Author");
            System.out.println("8-Follow a User");
            System.out.println("9-Unfollow an Author");
            System.out.println("10-Unfollow a User");
            System.out.println("11-Recommendation of Books Based On Friend");
            System.out.println("12-Recommendation of Books Based On Friend and Preferred Genre");
            System.out.println("13-Recommendation of Books Based On The Preferred Genre");
            System.out.println("14-Recommendation of Books Based On Best Authors");
            System.out.println("15-Recommendation of Users");
            System.out.println("16-LogOut");
            System.out.println("------------");
            System.out.print("Enter your choice: ");

            switch (Integer.parseInt(scanner.nextLine())){
                case 0:{
                    userController.showFollowings(Session.getInstance().getLoggedUser());
                    break;
                }
                case 1:{
                    System.out.print("Enter book title to find:");
                    String bookTitleToFind = scanner.nextLine();
                    System.out.print("From date:");
                    String startDate = scanner.nextLine();
                    System.out.print("To Date:");
                    String endDate = scanner.nextLine();
                    System.out.print("List of Authors(Separated by ,):");
                    List<String> authors;
                    String author=scanner.nextLine();
                    if(author.isEmpty()){
                        authors=null;
                    }else {
                        authors= Arrays.asList(author.split(","));
                    }
                    System.out.print("List of categories(Separated by ,):");
                    List<String> categories;
                    String category=scanner.nextLine();
                    if(category.isEmpty()){
                        categories=null;
                    }else {
                        categories= Arrays.asList(category.split(","));
                    }
                    List<Book> books = userController.searchBooksByParameters(bookTitleToFind, authors, startDate, endDate, categories, 0, 5);
                    if (books == null || books.isEmpty()) {
                        System.out.println("No book in the DB using these parameters");
                        break;
                    }
                    for (Book book : books) {
                        System.out.println("=== Book ===");
                        System.out.println("ISBN: " + book.getISBN());
                        System.out.println("Title: " + book.getTitle());
                        System.out.println("Description: " + book.getDescription());
                        System.out.println("Authors: " + book.getAuthors());
                        System.out.println("Categories: " + book.getCategories());
                        System.out.println("Published Date: " + book.getPublishedDate());

                        List<LastUserReviews> lastUserReviews = book.getLast_users_review();
                        System.out.println("=== Last User Reviews ===");

                        if (lastUserReviews == null || lastUserReviews.isEmpty()) {
                            System.out.println("No last user reviews available.");
                        } else {
                            for (LastUserReviews userReview : lastUserReviews) {
                                System.out.println("---- User Review ----");
                                System.out.println("Profile Name: " + userReview.getProfileName());
                                System.out.println("Time: " + userReview.getTime());
                                System.out.println("Score: " + userReview.getScore());
                                System.out.println("Review: " + userReview.getReview());
                                System.out.println("----------------------");
                            }
                        }

                        System.out.println("====================");
                    }
                    break;
                }
                case 2: {
                    System.out.print("Enter username keyword to find:");
                    String usernameToFind = scanner.nextLine();
                    List<User>users=userController.getUserByKeyword(usernameToFind,false,0);
                    for(User user:users){
                       if(user.getType()==0){
                           userController.showProfilewithNoPass(user);
                       }
                    }
                    break;
                }
                case 3:{
                    userController.showProfile(Session.getInstance().getLoggedUser());
                    break;
                }
                case 4:{
                    System.out.println("To add a Review please enter before the ISBN then if the book is what you are looking for, then add a review on it");
                    System.out.print("ISBN:");
                    String isbn=scanner.nextLine();
                    Book book=bookController.getBookByISBN(isbn);
                    if(book==null){
                        System.out.println("The book doesnt exist so maybe try to find the book with other parameters before then return here to add the review");
                        break;
                    }
                    System.out.println("=== Book ===");
                    System.out.println("ISBN: " + book.getISBN());
                    System.out.println("Title: " + book.getTitle());
                    System.out.println("Description: " + book.getDescription());
                    System.out.println("Authors: " + book.getAuthors());
                    System.out.println("Categories: " + book.getCategories());
                    System.out.println("Published Date: " + book.getPublishedDate());

                    List<LastUserReviews> lastUserReviews = book.getLast_users_review();
                    System.out.println("=== Last User Reviews ===");

                    if (lastUserReviews == null || lastUserReviews.isEmpty()) {
                        System.out.println("No last user reviews available.");
                    } else {
                        for (LastUserReviews userReview : lastUserReviews) {
                            System.out.println("---- User Review ----");
                            System.out.println("Profile Name: " + userReview.getProfileName());
                            System.out.println("Time: " + userReview.getTime());
                            System.out.println("Score: " + userReview.getScore());
                            System.out.println("Review: " + userReview.getReview());
                            System.out.println("----------------------");
                        }
                    }

                    System.out.println("====================");
                    System.out.print("Is this the book you are looking for?(Y/n):");
                    if(scanner.nextLine().equalsIgnoreCase("y")){
                        System.out.print("Review:");
                        String review=scanner.nextLine();
                        System.out.print("Score(1,2,3,4,5):");
                        Review review1=new Review(book.getISBN(),book.getTitle(),Session.getInstance().getLoggedUser().getprofileName(),Integer.parseInt(scanner.nextLine()),new Date(),review,book.getCategories(),book.getAuthors());
                        bookController.addReview(book,review1);
                        System.out.println("Review Added");
                    }
                    break;
                }
                case 5:{
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();
                    boolean passwordChanged = userController.changePassword(Session.getInstance().getLoggedUser(), newPassword);
                    if (passwordChanged) {
                        System.out.println("Password changed successfully.");
                    } else {
                        System.out.println("Password change failed.");
                    }
                    break;
                }
                case 6:{
                    System.out.print("Your Preferred Genre is:");
                    if(userController.setPreferredGenre(scanner.nextLine())){
                        System.out.println("Your preferred genre now set");
                    }
                    break;
                }
                case 7:{
                    System.out.print("Name of the Author:");
                    if(userController.followAuthor(scanner.nextLine())){
                        System.out.println("Now you are following the author");
                    }else {
                        System.out.println("Author does not exist or the name you provided is malformed so maybe search for a book that he/she written and get the name then");
                    }
                    break;
                }
                case 8:{
                    System.out.print("Name of the user:");
                    String profileName=scanner.nextLine();
                    User user= userController.getUserByProfileName(profileName);
                    if(user==null){
                        System.out.println("User doesn't exist please enter a valid name");
                        break;
                    }
                    userController.showProfilewithNoPass(user);
                    if(userController.followUser(user)){
                        System.out.println("Now you are following the user");
                    }
                    break;
                }
                case 9:{
                    System.out.print("Name of the Author:");
                    if(userController.unfollowAuthor(scanner.nextLine())){
                        System.out.println("Now you are not following the author anymore");
                    }else {
                        System.out.println("Author does not exist or the name you provided is malformed so maybe search for a book that he/she written and get the name then");
                    }
                    break;
                }
                case 10:{
                    System.out.print("Name of the user:");
                    String profileName=scanner.nextLine();
                    User user= userController.getUserByProfileName(profileName);
                    if(user==null){
                        System.out.println("User doesn't exist please enter a valid name");
                        break;
                    }
                    userController.showProfilewithNoPass(user);
                    if(userController.unfollowUser(user)){
                        System.out.println("Now you are not following the user anymore");
                    }
                    break;
                }
                case 11:{
                    List<String> ISBNS=userController.recommendBooksBasedOnFriendsComments(5);
                    if(ISBNS==null){
                        System.out.println("No recommendation because your friend is not reviewing any books");
                        break;
                    }
                    System.out.println("Top 5 books that your friends read too");
                    for (String isbn : ISBNS) {
                        Book book = bookController.getBookByISBN(isbn);
                        if (book != null) {
                            System.out.println("=== Book ===");
                            System.out.println("ISBN: " + book.getISBN());
                            System.out.println("Title: " + book.getTitle());
                            System.out.println("Description: " + book.getDescription());
                            System.out.println("Authors: " + book.getAuthors());
                            System.out.println("Categories: " + book.getCategories());
                            System.out.println("Published Date: " + book.getPublishedDate());

                            List<LastUserReviews> lastUserReviews = book.getLast_users_review();
                            System.out.println("=== Last User Reviews ===");

                            if (lastUserReviews == null || lastUserReviews.isEmpty()) {
                                System.out.println("No last user reviews available.");
                            } else {
                                for (LastUserReviews userReview : lastUserReviews) {
                                    System.out.println("---- User Review ----");
                                    System.out.println("Profile Name: " + userReview.getProfileName());
                                    System.out.println("Time: " + userReview.getTime());
                                    System.out.println("Score: " + userReview.getScore());
                                    System.out.println("Review: " + userReview.getReview());
                                    System.out.println("----------------------");
                                }
                            }

                            System.out.println("====================");
                        } else {
                            System.out.println("No book found for ISBN: " + isbn);
                        }
                    }

                    break;
                }
                case 12:{
                    List<String> ISBNS=userController.recommendBooksBasedOnFriendsCommentsAndPreferredGenre(5);
                    if(ISBNS==null){
                        System.out.println("No recommendation because your friend is not reviewing any books or you have a preferred genres that doesn't coincide with your friends");
                        break;
                    }
                    System.out.println("Top 5 books that your friends read too and it is based on your preferred genre");
                    for (String isbn : ISBNS) {
                        Book book = bookController.getBookByISBN(isbn);
                        if (book != null) {
                            System.out.println("=== Book ===");
                            System.out.println("ISBN: " + book.getISBN());
                            System.out.println("Title: " + book.getTitle());
                            System.out.println("Description: " + book.getDescription());
                            System.out.println("Authors: " + book.getAuthors());
                            System.out.println("Categories: " + book.getCategories());
                            System.out.println("Published Date: " + book.getPublishedDate());

                            List<LastUserReviews> lastUserReviews = book.getLast_users_review();
                            System.out.println("=== Last User Reviews ===");

                            if (lastUserReviews == null || lastUserReviews.isEmpty()) {
                                System.out.println("No last user reviews available.");
                            } else {
                                for (LastUserReviews userReview : lastUserReviews) {
                                    System.out.println("---- User Review ----");
                                    System.out.println("Profile Name: " + userReview.getProfileName());
                                    System.out.println("Time: " + userReview.getTime());
                                    System.out.println("Score: " + userReview.getScore());
                                    System.out.println("Review: " + userReview.getReview());
                                    System.out.println("----------------------");
                                }
                            }

                            System.out.println("====================");
                        } else {
                            System.out.println("No book found for ISBN: " + isbn);
                        }
                    }

                    break;
                }
                case 13:{
                    System.out.print("Min Num of reviews:");
                    List<String> ISBNS=userController.recommendPopularBooksByGenre(5,Integer.parseInt(scanner.nextLine()));
                    if(ISBNS==null){
                        System.out.println("No recommendation because there is not a book about your preferred genre or you have not a preferred genre");
                        break;
                    }
                    System.out.println("Top 5 books that is based on your preferred genre");
                    for (String isbn : ISBNS) {
                        Book book = bookController.getBookByISBN(isbn);
                        if (book != null) {
                            System.out.println("=== Book ===");
                            System.out.println("ISBN: " + book.getISBN());
                            System.out.println("Title: " + book.getTitle());
                            System.out.println("Description: " + book.getDescription());
                            System.out.println("Authors: " + book.getAuthors());
                            System.out.println("Categories: " + book.getCategories());
                            System.out.println("Published Date: " + book.getPublishedDate());

                            List<LastUserReviews> lastUserReviews = book.getLast_users_review();
                            System.out.println("=== Last User Reviews ===");

                            if (lastUserReviews == null || lastUserReviews.isEmpty()) {
                                System.out.println("No last user reviews available.");
                            } else {
                                for (LastUserReviews userReview : lastUserReviews) {
                                    System.out.println("---- User Review ----");
                                    System.out.println("Profile Name: " + userReview.getProfileName());
                                    System.out.println("Time: " + userReview.getTime());
                                    System.out.println("Score: " + userReview.getScore());
                                    System.out.println("Review: " + userReview.getReview());
                                    System.out.println("----------------------");
                                }
                            }

                            System.out.println("====================");
                        } else {
                            System.out.println("No book found for ISBN: " + isbn);
                        }
                    }

                    break;
                }
                case 14:{
                    List<String> ISBNS=userController.recommendationBasedOnAuthorsLiked(5);
                    if(ISBNS==null){
                        System.out.println("No recommendation because your are not reviewing any books or you have a preferred Authors");
                        break;
                    }
                    System.out.println("Top 5 books that is based on your preferred Authors");
                    for (String isbn : ISBNS) {
                        Book book = bookController.getBookByISBN(isbn);
                        if (book != null) {
                            System.out.println("=== Book ===");
                            System.out.println("ISBN: " + book.getISBN());
                            System.out.println("Title: " + book.getTitle());
                            System.out.println("Description: " + book.getDescription());
                            System.out.println("Authors: " + book.getAuthors());
                            System.out.println("Categories: " + book.getCategories());
                            System.out.println("Published Date: " + book.getPublishedDate());

                            List<LastUserReviews> lastUserReviews = book.getLast_users_review();
                            System.out.println("=== Last User Reviews ===");

                            if (lastUserReviews == null || lastUserReviews.isEmpty()) {
                                System.out.println("No last user reviews available.");
                            } else {
                                for (LastUserReviews userReview : lastUserReviews) {
                                    System.out.println("---- User Review ----");
                                    System.out.println("Profile Name: " + userReview.getProfileName());
                                    System.out.println("Time: " + userReview.getTime());
                                    System.out.println("Score: " + userReview.getScore());
                                    System.out.println("Review: " + userReview.getReview());
                                    System.out.println("----------------------");
                                }
                            }

                            System.out.println("====================");
                        } else {
                            System.out.println("No book found for ISBN: " + isbn);
                        }
                    }

                    break;
                }
                case 15:{
                    List<User>users=userController.recommendUserWithMostFollowersOfFollowings(5);
                    if(users==null){
                        System.out.println("You don't have any friends");
                        break;
                    }
                    System.out.println("Top 5 friends that are friends of your friends ");
                    for(User user:users){
                        userController.showProfilewithNoPass(userController.getUserByProfileName(user.getprofileName()));
                    }
                    break;
                }
                case 16:{
                    System.out.println("Logging out...");
                    Session.resetInstance();
                    System.out.println("Logout successful. Goodbye, Reader!");
                    return;
                }
                default:{
                    System.out.println("Invalid choice. Please try again.");
                    break;
                }
            }

        }
    }
    /**
     * Displays the menu for an administrator user.
     * Provides various options such as showing profile, finding users, changing passwords,
     * finding books, deleting reviews, adding books, banning users, and generating statistics.
     */
    private static void menuAdmin(){
        while (true){
            System.out.println("1-Show profile");
            System.out.println("2-Find User");
            System.out.println("3-Change Password");
            System.out.println("4-Find Book");
            System.out.println("5-Delete Review");
            System.out.println("6-Add Book");
            System.out.println("7-Ban User");
            System.out.println("8-User Statistics");
            System.out.println("9-Book Statistics");
            System.out.println("10-LogOut");
            System.out.println("------------");
            System.out.print("Enter your choice: ");
            switch (Integer.parseInt(scanner.nextLine())){
                case 1: {
                    userController.showProfile(Session.getInstance().getLoggedUser());
                    break;
                }
                case 2: {
                    System.out.print("Enter username keyword to find:");
                    String usernameToFind = scanner.nextLine();
                    List<User>users=userController.getUserByKeyword(usernameToFind,false,0);
                    for(User user:users){
                        userController.showProfile(user);
                    }
                    break;
                }
                case 3: {
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();
                    boolean passwordChanged = userController.changePassword(Session.getInstance().getLoggedUser(), newPassword);
                    if (passwordChanged) {
                        System.out.println("Password changed successfully.");
                    } else {
                        System.out.println("Password change failed.");
                    }
                    break;
                }
                case 4: {
                    System.out.print("Enter book title to find:");
                    String bookTitleToFind = scanner.nextLine();
                    System.out.print("From date:");
                    String startDate = scanner.nextLine();
                    System.out.print("To Date:");
                    String endDate = scanner.nextLine();
                    System.out.print("List of Authors(Separated by ,):");
                    List<String> authors;
                    String author=scanner.nextLine();
                    if(author.isEmpty()){
                        authors=null;
                    }else {
                        authors= Arrays.asList(author.split(","));
                    }
                    System.out.print("List of categories(Separated by ,):");
                    List<String> categories;
                    String category=scanner.nextLine();
                    if(category.isEmpty()){
                        categories=null;
                    }else {
                        categories= Arrays.asList(category.split(","));
                    }
                    List<Book> books = userController.searchBooksByParameters(bookTitleToFind, authors, startDate, endDate, categories, 0, 5);
                    if (books == null || books.isEmpty()) {
                        System.out.println("No book in the DB using these parameters");
                        break;
                    }
                    for (Book book : books) {
                        System.out.println("=== Book ===");
                        System.out.println("ISBN: " + book.getISBN());
                        System.out.println("Title: " + book.getTitle());
                        System.out.println("Description: " + book.getDescription());
                        System.out.println("Authors: " + book.getAuthors());
                        System.out.println("Categories: " + book.getCategories());
                        System.out.println("Published Date: " + book.getPublishedDate());

                        List<LastUserReviews> lastUserReviews = book.getLast_users_review();
                        System.out.println("=== Last User Reviews ===");

                        if (lastUserReviews == null || lastUserReviews.isEmpty()) {
                            System.out.println("No last user reviews available.");
                        } else {
                            for (LastUserReviews userReview : lastUserReviews) {
                                System.out.println("---- User Review ----");
                                System.out.println("Profile Name: " + userReview.getProfileName());
                                System.out.println("Time: " + userReview.getTime());
                                System.out.println("Score: " + userReview.getScore());
                                System.out.println("Review: " + userReview.getReview());
                                System.out.println("----------------------");
                            }
                        }

                        System.out.println("====================");
                    }
                    break;
                }
                case 5: {
                    System.out.println("To Delete a review First of all Enter a ISBN then enter the profileName of the User:");
                    System.out.print("ISBN:");
                    String isbn = scanner.nextLine();
                    System.out.print("profileName:");
                    String profileName = scanner.nextLine();
                    Book book = bookController.getBookByISBN(isbn);
                    User user = userController.getUserByProfileName(profileName);
                    if (book != null && user != null) {
                        Review review = userController.getReviewByISBNAndProfileName(book.getISBN(),user.getprofileName());
                        System.out.println("---- Review ----");
                        System.out.println("ISBN: " + review.getISBN());
                        System.out.println("Title: " + review.getTitle());
                        System.out.println("Score: " + review.getScore());
                        System.out.println("Time: " + review.getTime());
                        System.out.println("Review: " + review.getReview());
                        System.out.println("---------------");
                        System.out.print("Do you want to delete the review?(Y/n)");
                        if (scanner.nextLine().equalsIgnoreCase("y")) {
                            bookController.deleteReview(book, review);
                            System.out.println("Review deleted");
                        }
                    }else {
                        System.out.println("book doesnt exists or user doesnt exists");
                    }
                    break;
                }
                case 6: {
                    System.out.println("All information on the book must be well formatted and is all required");
                    System.out.print("ISBN:");
                    String isbn = scanner.nextLine();
                    System.out.print("Title:");
                    String title = scanner.nextLine();
                    System.out.print("Authors(Separated by ,):");
                    List<String> authors = Arrays.asList(scanner.nextLine().split(","));
                    System.out.print("Categories(Separated by ,):");
                    List<String> categories = Arrays.asList(scanner.nextLine().split(","));
                    System.out.print("Description:");
                    String description = scanner.nextLine();
                    System.out.print("PublishedDate(in format yyyy-mm-dd ):");
                    Date date;
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        date = dateFormat.parse(scanner.nextLine());
                    } catch (Exception e) {
                        System.out.println("error in parsing the date");
                        e.printStackTrace();
                        return;
                    }
                    Book book = new Book(isbn, title, description, authors, categories, date, new ArrayList<>());
                    userController.addBook(book);
                    break;
                }
                case 7: {
                    System.out.print("Enter username find:");
                    String usernameToFind = scanner.nextLine();
                    User user = userController.getUserByProfileName(usernameToFind);
                    if (user == null) {
                        break;
                    }
                    userController.showProfilewithNoPass(user);
                    System.out.print("Do you want to ban Him/Her?(Y/n)");
                    if (scanner.nextLine().equalsIgnoreCase("y")) {
                        userController.deleteUser(user);
                    }
                    break;
                }
                case 8: {
                    System.out.println("Top 5 versatile Users");
                    List<User>users=userController.getMostVersatileUsers(0, 5);
                    if(users!=null||!users.isEmpty()){
                        for(User user:users){
                            userController.showProfilewithNoPass(user);
                        }
                    }else{
                        System.out.println("No users are present");
                    }
                    System.out.println("Top 5 Most Followed Users -> Possible Influencers");
                    List<String>influencers=userController.getUsersWithMostFollowers(5);
                    if(influencers!=null||!influencers.isEmpty()){
                        for(String influencer:influencers){
                            userController.showProfilewithNoPass(userController.getUserByProfileName(influencer));
                        }
                    }else{
                        System.out.println("No users are present");
                    }
                    System.out.print("To get Most Rated Authors please enter a Num Min of Review that He/She has->");
                    ArrayList<Double> scores = new ArrayList<>();
                    System.out.println(userController.getMostRatedAuthors(0, 5, Integer.parseInt(scanner.nextLine()), scores));
                    System.out.println(scores);
                    System.out.println("Top 5 bad Users");
                    users=userController.getBadUsers(0, 5);
                    if(users!=null||!users.isEmpty()){
                        for(User user:users){
                            userController.showProfilewithNoPass(user);
                        }
                    }else{
                        System.out.println("No users are present");
                    }
                    System.out.println("To get Most Active User Please Specify start and end date in format yyyy-mm-dd ");
                    System.out.print("Start:");
                    String start = scanner.nextLine();
                    System.out.print("End:");
                    String end = scanner.nextLine();
                    ArrayList<Integer> counts = new ArrayList<>();
                    List<String>activers=userController.getMostActiveUsers(start, end, 0, 5, counts);
                    if(activers!=null||!activers.isEmpty()){
                        for(String activer:activers){
                            userController.showProfilewithNoPass(userController.getUserByProfileName(activer));
                        }
                    }else{
                        System.out.println("No users are present");
                    }
                    System.out.println("Number of Reviews in that period:"+counts);
                    break;
                }
                case 9: {
                    System.out.println("Top 5 categories by num of book published");
                    System.out.println(userController.getTopCategoriesOfNumOfBookPublished(0, 5));
                    System.out.println("To get Top 5 books please enter a Min num of reviews and a list of categories separated by ,");
                    System.out.print("Categories(Separated by ,):");
                    List<String> categories;
                    String category=scanner.nextLine();
                    if(category.isEmpty()){
                        categories=null;
                    }else {
                        categories= Arrays.asList(category.split(","));
                    }
                    System.out.print("Min Num:");
                    ArrayList<Double> scores = new ArrayList<>();
                    List<Book>bookList=userController.getTopBooks(Integer.parseInt(scanner.nextLine()), categories, 5, 0, scores);
                    if (bookList == null || bookList.isEmpty()) {
                        System.out.println("No books found.");
                    } else {
                        for (Book book : bookList) {
                            System.out.println("=== Book ===");
                            System.out.println("ISBN: " + book.getISBN());
                            System.out.println("Title: " + book.getTitle());
                            System.out.println("Description: " + book.getDescription());
                            System.out.println("Authors: " + book.getAuthors());
                            System.out.println("Categories: " + book.getCategories());
                            System.out.println("Published Date: " + book.getPublishedDate());

                            List<LastUserReviews> lastUserReviews = book.getLast_users_review();
                            System.out.println("=== Last User Reviews ===");

                            if (lastUserReviews == null || lastUserReviews.isEmpty()) {
                                System.out.println("No last user reviews available.");
                            } else {
                                for (LastUserReviews userReview : lastUserReviews) {
                                    System.out.println("---- User Review ----");
                                    System.out.println("Profile Name: " + userReview.getProfileName());
                                    System.out.println("Time: " + userReview.getTime());
                                    System.out.println("Score: " + userReview.getScore());
                                    System.out.println("Review: " + userReview.getReview());
                                    System.out.println("----------------------");
                                }
                            }

                            System.out.println("====================");
                        }
                    }
                    System.out.println("Book Scores: " + scores);
                    break;
                }
                case 10: {
                    System.out.println("Logging out...");
                    Session.resetInstance();
                    System.out.println("Logout successful. Goodbye, Admin!");
                    return;
                }
                default: {
                    System.out.println("Invalid choice. Please try again.");
                    break;
                }
            }
        }
    }
}
