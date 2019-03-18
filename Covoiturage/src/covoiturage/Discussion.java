/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covoiturage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 *
 * @author user
 */
public class Discussion {
    public Utilisateur user1;
    public Utilisateur user2;
    public int prix;
    public String voyage;
    public String date;
            
    public Discussion(Utilisateur user1,Utilisateur user2, int prix,String voyage,String date){
        this.user1=user1;
        this.user2=user2;
        this.prix=prix;
        this.voyage=voyage;
        this.date=date;
    }
   
    public void enregisterConversation(ArrayList<String> messages) throws IOException{
        String ajout=this.prix+"|"+this.voyage+"|"+this.date+"|\n";
        for(String s : messages){
            ajout+=s+";\n";
        }
 
        String dateTime = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        String path = "messages/"+this.user1.id+"-"+this.user2.id+"_"+dateTime+".txt";
        
        FileWriter fw = new FileWriter(path,true);
        fw.write(ajout);
        fw.close();
    }
    
    public ArrayList<String> recupererConversation() throws IOException{
        String path = "messages/"+this.user1.id+"-"+this.user2.id+".txt";
        File f = new File(path);
        if(f.exists() && !f.isDirectory()) { 
            ArrayList<String> str=new ArrayList();
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()){
                String temp = sc.nextLine();
                if(!"|".equals(temp.substring(temp.length() - 1))){
                    temp=temp.substring(0,temp.length() - 1);
                    str.add(temp);
                }                    
            }
            return str;
        }
        return null;
    }
    
    public void conversation() throws IOException{
        ArrayList messages = new ArrayList<>();
        //utilisateur commence à négocier
        int nbreNegocie = negociationPrix(0,this.prix);
        int quiParle = 0;
        while (nbreNegocie!= 0 || nbreNegocie!= this.prix ){
            String newMessage;
            //On teste le nouveau prix
            if (nbreNegocie==0){
                newMessage="pas ok";
            } else if(nbreNegocie==this.prix){
                newMessage="ok";
            }else {
                newMessage="nouveau prix "+this.prix;
            }
            messages.add(newMessage);
            //On regénère un nouveau prix
            if (quiParle%2==0){
                //Utilisateur veut négocier
                nbreNegocie = negociationPrix(0,this.prix);
            } else{
                //Conducteur veut négocier
                nbreNegocie = negociationPrix((this.prix/2),this.prix);
            }
            
        }
        enregisterConversation(messages);
    }
    
    //typePersonne == 0 : Utilisateur (veut descendre le prix
    //typePersonne == 1 : Conducteur (ne veut pas trop descendre le prix)
    public int negociationPrix(int min,int prix){
        int rndNumber = min + (int)(Math.random() * ((prix - min) + 1));
        if (rndNumber == prix){
                return prix;
            } else if(rndNumber<=0){
                return 0;
            } else {
                return rndNumber;
            }
    }
}
