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

public class BlockchainMain {

    public static List<String> lis = new LinkedList<>();
    public static List<Integer> diffclt = new LinkedList<>();
    public static String chainHsh = "";
    public static int difficulty_imported;

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            selection();

            int sel = sc.nextInt();
            switch (sel) {
                case 1:
                    cases(sel);
                    break;
                case 2:
                    cases(sel);
                    break;
                case 3:
                    cases(sel);
                    break;
            }
        }
    }

    private static void options() {
        System.out.println("Welcome to the Block Chain Algorithm Application");
        System.out.println("1. Add Transaction");
        System.out.println("2. Verify BlockChain");
        System.out.println("3. View BlockChain");
        System.out.println("4. Corrupt A Block");
        System.out.println("5. Fix Corruption");
        System.out.println("6. Export BlockChain");
        System.out.println("7. Compare Running Block chain with Imported BlockChain from any text file");
        System.out.println("8. Adjust Difficulty");
        System.out.println("9.Terminate");
    }

    private static void cases(int i) {
        BlockService service = new BlockService();
        if (i == 1) {
            service.block_genesis();
        } else if (i == 2) {
            fileImport();
            service.list = lis;
            service.difficulties = diffclt;
            service.currentHash = chainHsh;
            service.chainHash = chainHsh;
            service.difficulty = difficulty_imported;
            service.verifyBlock();
            if (service.corrupt) {
                System.out.println("Creating a New BlockChain from Scratch");
                cases(1);
            }
        } else if (i == 3) {
            MongoDB mongoDb=new MongoDB();


        }
        Scanner sc = new Scanner(System.in);
        while (true) {
            options();
            int opt = sc.nextInt();
            switch (opt) {
                case 1:
                    service.addTransaction();
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
                        System.out.println("Chain Hash is: " + service.chainHash);
                        System.out.println("Current Difficulty is:" + service.difficulty);
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
                    service.adjustDifficulty();
                    continue;
                case 9:
                    System.exit(0);
                    break;
            }
        }
    }

    private static void selection() {
        System.out.println("Select One from Below Options");
        System.out.println("1. I wanna Create a New BlockChain");
        System.out.println("2. Import a Block Chain from a Text File");
        System.out.println("3. Connect to Mongo");
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

//    public static void mongoConnect() {
//        try {
//            System.out.println("Enter File name to connect with (Extension Not Required)");
//            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//            String fileNm = br.readLine() + ".txt";
//            JSONObject jo = new JSONObject(new JSONTokener(new FileReader(fileNm)));
//            System.out.println("Found the file " + fileNm + " and Connecting to Database");
//            String db = (String) jo.get("dbconnect");
//            System.out.println(db);
//            MongoClientURI uri = new MongoClientURI(db);
//            MongoClient mongoClient = new MongoClient(uri);
//            MongoDatabase database = mongoClient.getDatabase(((String)jo.get("collection")));
//            System.out.println(database);
//            MongoCollection<Document> collection = database.getCollection(((String)jo.get("collection")));
//            System.out.println(collection + "I am perfect till here");
//
//            Document doc = new Document("FirstName", "MiddleName")
//                    .append("type", "database1")
//                    .append("count", 1);
//            collection.insertOne(doc);
//            System.out.println(collection + "here");
//            mongoClient.close();
//        } catch (IOException | JSONException e) {
//            System.out.println(e.getMessage());
//        }
//    }

}