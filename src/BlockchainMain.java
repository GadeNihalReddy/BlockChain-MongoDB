import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

public class BlockchainMain {

    public static List<String> lis = new LinkedList<>();
    public static List<Integer> diffclt = new LinkedList<>();
    public static String chainHsh = "";
    public static int difficulty_imported;
    public static MongoClientURI uri;
    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection<Document> collection;
    public static Document doc=new Document();

    public static void main(String args[]) {
        java.util.logging.Logger.getLogger("org.mongodb.driver").setLevel(Level.SEVERE);
        mongoConnect();
        cases(1);
    }

    private static void options() {
        System.out.println("Welcome to the Block Chain Algorithm Application");
        System.out.println("1. Add Transaction");
        System.out.println("2. Verify BlockChain");
        System.out.println("3. View BlockChain");
        System.out.println("4. Corrupt A Block");
        System.out.println("5. Fix Corruption");
        System.out.println("6. Export BlockChain");
        System.out.println("7. Performing 472 Queries");
        System.out.println("8. Performing 572 Queries");
        System.out.println("9.Terminate");
    }

    private static void cases(int i) {
        BlockService service = new BlockService();
        //Block blk=new Block();
        if (i == 1) {
            doc=Document.parse(service.block_genesis());
            collection.insertOne(doc);
        }
        Scanner sc = new Scanner(System.in);
        while (true) {
            options();
            int opt = sc.nextInt();
            switch (opt) {
                case 1:
                    doc=Document.parse(service.addTransaction());
                    collection.insertOne(doc);
                    break;
                case 2:
                    service.verifyBlock();
                    break;
                case 3:
                    try {
                        JSONObject jo = service.viewBlock();
                        JSONArray array = (JSONArray) jo.get("blockchain");
                        for (int j = 0; j < array.length(); j++) {
                            System.out.println("Block-" + j + ": " + array.get(j).toString());
                        }
                        //System.out.println(service.viewBlock().get("blockchain"));
                        //System.out.println("Chain Hash is: " + service.chainHash);
                        //System.out.println("Current Difficulty is:" + service.difficulty);
                    } catch (JSONException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 4:
                    service.corruptBlock();
                    break;
                case 5:
                    service.fixCorruption();
                    break;
                case 6:
                    service.exportBlk();
                    break;
                case 7:
                    //service.validity();
                    break;
                case 8:
                   // service.adjustDifficulty();
                    continue;
                case 9:
                    mongoClient.close();
                    System.exit(0);
                    break;
            }
        }
    }

    public static void fileImport() {

        try {
            System.out.println("Enter File name to Import BlockChain(Extension Not Required)");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String fileNm = br.readLine() + ".txt";
            JSONObject jo = new JSONObject(new JSONTokener(new FileReader(fileNm)));
            System.out.println("Found the file " + fileNm + " and loading blockchain...");
            chainHsh = (String) jo.get("chainHash");
            JSONArray arra = (JSONArray) jo.get("difficulty");
            for (int i = 0; i < arra.length(); i++) {
                diffclt.add((Integer) arra.get(i));
                difficulty_imported = (Integer) arra.get(i);
            }
            JSONArray array = (JSONArray) jo.get("blockchain");
            for (int i = 0; i < array.length(); i++) {
                lis.add(array.get(i).toString());
            }
        } catch (JSONException | IOException e) {
            System.out.println("File not found, proceeding to create blockchain from start");
            cases(1);
        }

    }

    public static void mongoConnect() {

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

        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }
    }

}