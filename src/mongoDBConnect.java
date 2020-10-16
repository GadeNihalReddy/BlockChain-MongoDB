import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public class mongoDBConnect {

    public static void main(String[] args) {
        System.out.println("Hello world!");

//        String dbstring = "mongodb+srv://test:test@mycluster.yrox3.mongodb.net/Mycluster?retryWrites=true&w=majority";
//
//
//        MongoClientURI uri=new MongoClientURI(dbstring);
//        MongoClient mongoClient=new MongoClient(uri);
//        MongoDatabase database = mongoClient.getDatabase("test");
//
//        MongoCollection<Document> collection= database.getCollection("test1");
//        System.out.println(collection);
//
//        Document doc = new Document("FirstName", "MiddleName")
//                .append("type", "database1")
//                .append("count", 1);
//
//        collection.insertOne(doc);
//        System.out.println(collection);
        //mongoClient.close();

        System.out.println("Attempting to connect");


        MongoClientURI uri = new MongoClientURI(
                "mongodb+srv://bob:bob@mycluster.kw2rx.mongodb.net/Mycluster?retryWrites=true&w=majority");

        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("shark");


        MongoCollection<Document> collection = database.getCollection("pumpkintime");
        System.out.println(collection);

        Document doc = new Document("FirstName", "Nihal")
                .append("Language", "Java")
                .append("I Need more", "Coffee");

        collection.insertOne(doc);


    }

}
