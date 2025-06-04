package Config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConnectionManager {

    private static final String URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "gitletDB";

    private static MongoClient mongoClient;

    // Singleton pattern to avoid multiple connections
    public static MongoDatabase getDatabase() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(URI);
        }
        return mongoClient.getDatabase(DATABASE_NAME);
    }

    // Optional: close the connection when done
    public static void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}
