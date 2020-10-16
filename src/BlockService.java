import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class BlockService {

    public boolean corrupt=false;
    BigInteger i= BigInteger.ONE;
    public String chainHash;
    public String currentHash="";
    public int difficulty;
    public List<Integer> difficulties=new LinkedList<>();
    public List<String> list=new LinkedList<>();
    //Genesis Block
   public void block_genesis() {
        Block genesis = new Block("Genesis Block", "", i, "", "");
        difficulty=2;//default for genesis block
        currentHash=genesis.chain_MineBlock(genesis,difficulty);
        chainHash=currentHash;
        genesis.setCurrentHash(currentHash);
        list.add(genesis.block_as_JSON(genesis));
        difficulties.add(difficulty);
    }

    public void addTransaction(){
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("Data");
            String data1=br.readLine();
            System.out.println("sender");
            String sender1=br.readLine();
            System.out.println("Recipient");
            String recipient1=br.readLine();
            i=i.add(BigInteger.ONE);
            String previousHash=currentHash;
            Block blk=new Block(data1,previousHash,i,sender1,recipient1);
            currentHash=blk.chain_MineBlock(blk,difficulty);
            blk.setCurrentHash(currentHash);
            chainHash=currentHash;
            list.add(blk.block_as_JSON(blk));
            difficulties.add(difficulty);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public JSONObject viewBlock(){
        JSONObject jsn=new JSONObject();
        try {
            Field changeMap =jsn.getClass().getDeclaredField("map");
            changeMap.setAccessible(true);
            changeMap.set(jsn, new LinkedHashMap<>());
            changeMap.setAccessible(false);
            jsn.put("chainHash",chainHash);
            jsn.put("difficulty",difficulties);
            jsn.put("blockchain", list);
            return jsn;
            //return jsn.toString();
        } catch (JSONException | NoSuchFieldException | IllegalAccessException e) {
            System.out.println("There is an Error in JSON values, they may be not in proper JSON format");
            System.out.println(e.getMessage());
        }
        return jsn;
    }

    public void verifyBlock() {
        //Current and previous block hashes check & for last block check with chain hash
        boolean isCorrupt=false;
        Block previous;
        Block current;
        Block last=Block.getBlock(list.get(list.size()-1));
        String h2="";//current block current hash
        String str22="";
        for(int i=1;i<list.size();i++){
            previous=Block.getBlock(list.get(i-1));
            current=Block.getBlock(list.get(i));
            String h1=previous.chain_MineBlock(previous,difficulties.get(i-1));//previous block current hash
            h2=current.chain_MineBlock(current,difficulties.get(i));
            //Previous Block Current Hash compared with current Block previous Hash
            if(!h1.equals(current.getPreviousHash())){
                str22="Block "+String.valueOf(i-1);
                isCorrupt=true;
                corrupt=true;
                break;
            }
        }
        if(!chainHash.equals(last.chain_MineBlock(last,difficulties.get(list.size()-1)))){
            str22=str22+" Final Block";
            isCorrupt = true;
            corrupt=true;
        }
        if(isCorrupt){
            System.out.println("Block Chain is Invalid & Corrupted at "+str22+ " (Blocks indexed as Block 0, Block 1,..Final Block)");
        }
        else{
            System.out.println("Block Chain is Valid and Not Corrupted");
        }

    }

    public void corruptBlock() {
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        try {
            System.out.println("Enter block number to corrupt(Indexed in [0-"+(list.size()-1)+"])");
            int blk_id=Integer.parseInt(br.readLine());
            if(!(blk_id <list.size())){
                System.out.println("Enter correct block number to corrupt(Indexed in [0-"+(list.size()-1)+"])");
            }
            else {
                System.out.println("Enter new Data(If Empty.. data will not be Changed)");
                String data2 = br.readLine();
                System.out.println("Enter new Sender(If Empty.. sender will not be Changed)");
                String sender2 = br.readLine();
                System.out.println("Enter new Recipient(If Empty.. recipient will not be Changed)");
                String recipient2 = br.readLine();

                Block blk_curpt=Block.getBlock(list.get(blk_id));

                if(!is_Value_Empty(data2)){
                    blk_curpt.setData(data2);
                }
                if(!is_Value_Empty(sender2)){
                    blk_curpt.setSender(sender2);
                }
                if(!is_Value_Empty(recipient2)){
                    blk_curpt.setRecipient(recipient2);
                }
                currentHash=blk_curpt.chain_MineBlock(blk_curpt,difficulties.get(blk_id));
                blk_curpt.setCurrentHash(currentHash);
                list.set(blk_id,blk_curpt.block_as_JSON(blk_curpt));
                System.out.println("Block "+(blk_id)+ " is Successfully Corrupted (Blocks Indexed as Block 0,Block 1,...etc)");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fixCorruption() {
        System.out.println("Recomputing Hashes to Fix Corruption");
        Block current_blk=null;
        for(int i=0;i<list.size();i++){
            current_blk=current_blk.getBlock(list.get(i));
            if(!current_blk.getPreviousHash().equals("")){
                current_blk.setPreviousHash(currentHash);
            }
            currentHash=current_blk.chain_MineBlock(current_blk,difficulties.get(i));
            chainHash=currentHash;
            current_blk.setCurrentHash(currentHash);
            list.set(i,current_blk.block_as_JSON(current_blk));
        }
        System.out.println("Fixed the block chain and No Corruption is present now");
    }

    public void exportBlk() {
        try {
            System.out.println("Enter File name to export BlockChain(Eextension Not Required)");
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String fileNm=br.readLine()+".txt";
            PrintWriter out= new PrintWriter(fileNm);
            out.println(viewBlock());
            out.close();
            System.out.println("Block Chain Exported Successfully to "+fileNm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void validity() {
       //text file data is imported to loacl fields and compared with jvm values
       System.out.println("Enter the text file name to import(Ignore file extension)");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            JSONObject jo=new JSONObject(new JSONTokener(new FileReader(br.readLine()+".txt")));
            JSONArray array =(JSONArray)jo.get("blockchain");
            String chnHsh=(String)jo.get("chainHash");
            List<String> bloc = new LinkedList<>();
            for(int i=0; i<array.length();i++) {
                bloc.add(array.get(i).toString());
            }
            if(bloc.size()!=list.size()){
                System.out.println("Block Chains are of Different size");
            }
            else{
                boolean isValid =true;
                for(int i=0;i<bloc.size();i++){
                    if(!bloc.get(i).equals(list.get(i))){
                        isValid=false;
                        System.out.println("Block Chains Vary at index:"+i);
                        break;
                    }
                }
                if(!chnHsh.equals(chainHash)){
                    isValid=false;
                    System.out.println("Blocks differ at Final Block ");
                }
                if(isValid){
                    System.out.println("Block chains are Equivalent or Similar");
                }
            }
        } catch (IOException e) {
            System.out.println("File Not Found");
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void adjustDifficulty() {
       //stating current difficulty
        System.out.println("Current Difficulty is : "+difficulty);
        System.out.println("Enter a difficulty from [1-7]");
        Scanner sc = new Scanner(System.in);
        int dif=sc.nextInt();
        while(dif<1 || dif>7){
            System.out.println("Enter difficulty range from [1-7] only");
            dif=sc.nextInt();
        }
            difficulty=dif;
            System.out.println("Current Difficulty is: "+difficulty);
    }

    public boolean is_Value_Empty(String str){
        return str == null || str.trim().length()<1;
    }
}
