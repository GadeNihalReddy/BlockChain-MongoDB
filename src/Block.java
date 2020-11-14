import com.google.gson.GsonBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Block {
    //Block data
    private int data;
    private int index;
    private String previousHash;
    private BigInteger nonce;
    private String sender;
    private String recipient;


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

    public Block(int data, String previousHash, BigInteger nonce, String sender, String recipient) {
        this.data = data;
        this.previousHash = previousHash;
        this.nonce = nonce;
        this.sender = sender;
        this.recipient = recipient;
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
        try {
            jsn.put("data",block.getData());
            jsn.put("previousHash",block.getPreviousHash());
            jsn.put("nonce",block.getNonce());
            jsn.put("sender",block.getSender());
            jsn.put("recipient",block.getRecipient());
            return jsn.toString();
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static Block getBlock(String block){
        GsonBuilder gsonBuilder=new GsonBuilder();
        return gsonBuilder.create().fromJson(block,Block.class);
    }
}
