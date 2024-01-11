package it.unipi.lsmsd.Persistence;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadPreference;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import it.unipi.lsmsd.Utils.Utils;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import java.util.Objects;
import java.util.Properties;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
public class MongoDBDriver {
    private static MongoDBDriver instance;
    private MongoClient client=null;
    private CodecRegistry codecRegistry;
    public String mongoUsername;
    public String mongoPassword;
    public String mongoFirstIp;
    public int mongoFirstPort;
    public String mongoSecondIp;
    public int mongoSecondPort;
    public String mongoThirdIp;
    public int mongoThirdPort;
    public String mongodbName;
     private MongoDBDriver(Properties configurationParameters){
         this.mongoUsername = configurationParameters.getProperty("mongoUsername");
         this.mongoPassword = configurationParameters.getProperty("mongoPassword");
         this.mongoFirstIp = configurationParameters.getProperty("mongoFirstIp");
         this.mongoFirstPort = Integer.parseInt(configurationParameters.getProperty("mongoFirstPort"));
         this.mongoSecondIp = configurationParameters.getProperty("mongoSecondIp");
         this.mongoSecondPort = Integer.parseInt(configurationParameters.getProperty("mongoSecondPort"));
         this.mongoThirdIp = configurationParameters.getProperty("mongoThirdIp");
         this.mongoThirdPort = Integer.parseInt(configurationParameters.getProperty("mongoThirdPort"));
         this.mongodbName = configurationParameters.getProperty("mongoDbName");
     }
    public static MongoDBDriver getInstance() {
        if (instance == null)
            instance = new MongoDBDriver(Objects.requireNonNull(Utils.readConfigurationParameters()));
        return instance;
    }
    public void closeConnection() {
        if (client != null){
            System.out.println("Connection closed ...");
            client.close();
        }
    }
    public MongoClient openConnection(){
         if(client!=null){
             return client;
         }
         try{
             String uri="mongodb://"+mongoFirstIp+":"+mongoFirstPort+","+mongoSecondIp+":"+mongoSecondPort+","+mongoThirdIp+":"+mongoThirdPort;
             ConnectionString connectionString=new ConnectionString(uri);
             codecRegistry=fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),fromProviders(PojoCodecProvider.builder().automatic(true).build()));
             MongoClientSettings clientSettings=MongoClientSettings.builder().applyConnectionString(connectionString).readPreference(ReadPreference.primaryPreferred()).retryWrites(true).writeConcern(WriteConcern.W1).codecRegistry(codecRegistry).build();
             client= MongoClients.create(clientSettings);
             System.out.println("Connection to mongodb");
             return client;
         }catch (Exception e){
             System.out.println("problems with connection to mongodb");
             e.printStackTrace();
             return null;
         }

    }
}
