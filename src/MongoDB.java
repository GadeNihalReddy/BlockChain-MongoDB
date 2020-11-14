import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MongoDB {

    public MongoClientURI uri;
    public MongoClient mongoClient;
    public MongoDatabase database;
    public MongoCollection<Document> collection;

    public void connectDB() {
        try {
            System.out.println("Enter File name to connect with (Extension Not Required)");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String fileNm = br.readLine() + ".txt";
            JSONObject jo = new JSONObject(new JSONTokener(new FileReader(fileNm)));
            System.out.println("Found the file " + fileNm + " and Connecting to Database");
            String db_string = (String) jo.get("dbconnect");
            uri = new MongoClientURI(db_string);
            mongoClient = new MongoClient(uri);
            database = mongoClient.getDatabase(((String) jo.get("collection")));
            collection = database.getCollection(((String) jo.get("collection")));
//            Document doc = new Document("FirstName", "MiddleName")
//                    .append("type", "database1")
//                    .append("count", 1);
//            collection.insertOne(doc);

        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    public void closeDB(){
        mongoClient.close();
    }
}
