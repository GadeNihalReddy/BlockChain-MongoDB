import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.LinkedHashMap;

public class Block {
    //Block data
    private int data;
    private int index;
    private String previousHash;
    private BigInteger nonce;
    private String sender;
    private String recipient;
    private int difficulty;


    //External entities used for block service
    private String currentHash;
    private String hash;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public void setCurrentHash(String currentHash) {
        this.currentHash = currentHash;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public Block(int data,String previousHash, BigInteger nonce, String sender, String recipient, int difficulty,int index) {
        this.data = data;
        this.previousHash = previousHash;
        this.nonce = nonce;
        this.sender = sender;
        this.recipient = recipient;
        this.difficulty = difficulty;
        this.index = index;
    }

    public Block() {
    }

    public String str_to_ComputeHash(Block block){
        String str=block.getData()+block.getPreviousHash().toString()+block.getNonce().toString()+block.getSender().toString()+
                block.getRecipient().toString();
        return  str;
    }

    public static String computeHash(String str){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(str.getBytes("UTF-8"));
            StringBuffer sb=new StringBuffer();//hexadecimal value for string
            for(int i=0;i<encodedHash.length;i++){
                String hex_val=Integer.toHexString(0xff &encodedHash[i]);
                if(hex_val.length()==1)
                    sb.append('0');
                sb.append(hex_val);
            }
            return sb.toString();
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    //Mining Block till valid hashing with difficulty
    public String chain_MineBlock(Block block,int difficulty){
        //string with difficulty valued zeros
        String validity=new String(new char[difficulty]).replace('\0','0');
        hash=computeHash(str_to_ComputeHash(block));
        while(!hash.substring(0,difficulty).equals(validity)){
            BigInteger non=block.getNonce().add(BigInteger.ONE);
            block.setNonce(non);
            hash=computeHash(str_to_ComputeHash(block));
        }
        return hash;
    }

    public static String block_as_JSON(Block block){
        JSONObject jsn=new JSONObject();
        //MongoDB mongo = new MongoDB();
        try {
            Field changeMap =jsn.getClass().getDeclaredField("map");
            changeMap.setAccessible(true);
            changeMap.set(jsn, new LinkedHashMap<>());
            changeMap.setAccessible(false);
            jsn.put("data",block.getData());
            jsn.put("previousHash",block.getPreviousHash());
            jsn.put("nonce",block.getNonce());
            jsn.put("sender",block.getSender());
            jsn.put("recipient",block.getRecipient());
            jsn.put("difficulty",block.getDifficulty());
            jsn.put("index",block.getIndex());
           // mongo.collection.insertOne(Document.parse(jsn.toString()));
            return jsn.toString();
        } catch (JSONException | NoSuchFieldException | IllegalAccessException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static Block getBlock(String block){
        GsonBuilder gsonBuilder=new GsonBuilder();
        return gsonBuilder.create().fromJson(block,Block.class);
    }
}
