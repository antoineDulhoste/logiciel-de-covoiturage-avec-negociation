/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covoiturage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author user
 */
public class Discussion {

    private Utilisateur voyageur;
    private String nomvoyageur;
    private Utilisateur conducteur;
    private String nomconducteur;
    private Voyage voyage;
    private boolean estReserve;    
    private int prix;
            
    
    public Discussion(Utilisateur user1,Utilisateur user2, Voyage voyage){
        this.voyageur=user1;
        this.nomvoyageur=this.voyageur.nom;
        this.conducteur=user2;
        this.nomconducteur=this.conducteur.nom;
        this.voyage=voyage;        
        this.prix = this.voyage.getPrix();
        this.estReserve=recupReservation();
    }
   
    
    private boolean recupReservation() {
        String path = this.voyageur.id+"-"+this.conducteur.id+"-"+this.voyage.getId()+".txt";
        
        Scanner scanner;
        try {
            scanner = new Scanner(new FileReader("messages\\" + path));
            String scan = scanner.nextLine();
            String[] information = scan.split("#");
            return Boolean.parseBoolean(information[4]);
        } catch (FileNotFoundException ex) {
            return false;
        }
    }
    
    public void enregisterConversation(ArrayList<String> messages) throws IOException{
        String ajout=this.prix+"#"+this.voyage.getVilleArrivee()+"#"+this.voyage.getVilleDepart()+"#"+this.voyage.getDate()+"#"+this.estReserve+"#\n";
        for(String s : messages){
            ajout+=s+";\n";
        }
        
        String path = "messages/"+this.getVoyageur().id+"-"+this.getConducteur().id+"-"+this.voyage.getId()+".txt";
        
        FileWriter fw = new FileWriter(path,true);
        fw.write(ajout);
        fw.close();
    }
    
    public ArrayList<String> recupererConversation() throws IOException{
        String path = this.voyageur.id+"-"+this.conducteur.id+"-"+this.voyage.getId()+".txt";
        
        File f = new File("messages/"+path);
        String msg="";
        ArrayList<String> str=new ArrayList();
        if(f.exists() && !f.isDirectory()) { 
            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()){
                String temp = sc.nextLine();
                msg+="\n"+temp;                   
            }
        }
        boolean premierIteration=true;
        String [] parts=msg.split(";");
        for(String s:parts){
            if(premierIteration)premierIteration=false;
            else{
                s=s.substring(1,s.length());
                str.add(s);
            }
        }
        return str;
    }
    
    public ArrayList<String> recupererPreference(boolean estConducteur) throws FileNotFoundException{
        String pattern;
        if (estConducteur){
            pattern = this.conducteur.id+"";
        } else {
            pattern = this.voyageur.id+"";
        }
        ArrayList<String> str2=new ArrayList();
        Scanner scanner = new Scanner(new FileReader("preference/"+pattern+".txt"));
        while (scanner.hasNextLine()) {
            String utili=scanner.nextLine();
            String[] parts = utili.split(";");
            for(String x:parts){
                str2.add(x);
            }
        }
        return str2;
    }
    
    public void conversation() throws IOException{
        ArrayList messages = new ArrayList<>();
        boolean estUtilisateur = false;
        //Tableaux des pr??f??rences de l'utilisateur et du conducteur
        ArrayList <String> tabPrefUtilisateur;
        ArrayList <String> tabPrefConducteur;
        
        tabPrefUtilisateur = recupererPreference(false);
        tabPrefConducteur = recupererPreference(true);
        
        int prixUtilActuel=(this.prix)-(this.prix*60/100);
        //Prix max utilisateur = random entre 0 et 20% du prix de base en moins
        int pourcentageRnd = (int)(Math.random() * (21));
        int prixMaxUtilisateur = (this.prix)-(this.prix*pourcentageRnd/100);
        //Prix minimum conducteur = random entre 0 et 25% du prix de base en moins
        pourcentageRnd = (int)(Math.random() * (26));
        int prixMinConducteur = (this.prix)-(this.prix*pourcentageRnd/100);
        int nbreUtil = negociationPrix(prixUtilActuel,prixMaxUtilisateur);
        String newMessage;
        newMessage="L'utilisateur souhaite voyager";
        messages.add(newMessage);
        boolean CEstFumeur = false;
        boolean UEstFumeur = false;
        boolean CEstAnimaux = false;
        boolean CEstEnfants = false;
        boolean UEstAnimaux = false;
        boolean UEstEnfants = false;
        boolean discussionFini = false;
        boolean tombentDaccord = false;
        boolean poserQuestion = false;
        boolean QFumer = false;
        boolean virg = false;
        boolean aLaLigne = false;
        //
        //Affichage des pr??f??rences Conducteur
        //
        if (tabPrefConducteur.size()>0){
            newMessage = "Le conducteur indique que son voyage ";
            for (int i=0;i<tabPrefConducteur.size();i++){
                if (i>0){
                    newMessage+=  ",";
                }
                if(tabPrefConducteur.get(i).equals("Fumeur")){
                    newMessage+=  "est Fumeur";
                    CEstFumeur = true;
                } else {
                    if(!aLaLigne)aLaLigne = true;
                    else newMessage+="\n";
                    if(tabPrefConducteur.get(i).equals("Enfants")){
                        CEstEnfants = true;
                    }
                    if(tabPrefConducteur.get(i).equals("Animaux")){
                        CEstAnimaux = true;
                    }
                    newMessage+=  "accepte les "+tabPrefConducteur.get(i);
                }
            }
            messages.add(newMessage);
        }else{
            newMessage = "Le conducteur demande si l'utilisateur n'a pas de preference pour son voyage ";
            messages.add(newMessage);
        }
        
        //
        //Affichage des pr??f??rences Utilisateur
        //
        aLaLigne = false;
        if(tabPrefUtilisateur.size()>0){
            newMessage="L'utilisateur";
             for (int i=0;i<tabPrefUtilisateur.size();i++){
                if(tabPrefUtilisateur.get(i).equals("Fumeur")){
                    UEstFumeur = true;
                    //Si conducteur est non fumeur
                    if (!CEstFumeur){
                        if(virg) newMessage+=  ",";
                        else virg=true;
                        if(!aLaLigne)aLaLigne = true;
                        else newMessage+="\n";
                        newMessage+=  " indique qu'il est Fumeur";
                        //Random pour savoir si il accepte de voyager avec un coducteur qui ne fume pas
                        int rndAccepte = negociationPrix(0,10);
                        //Accepte de voyager
                        if (rndAccepte > 4){
                            newMessage+=  " et accepte de voyager dans un voyage non fumeur";
                        //N'accepte pas
                        } else if (rndAccepte<=4 && rndAccepte>1 ){
                            newMessage+=  " et demande si il pourra fumer pendant le voyage";
                            QFumer = true;
                            poserQuestion = true;
                        }else{
                            newMessage+=  " et n'accepte pas de voyager dans un voyage non fumeur";
                            discussionFini = true;
                        }
                    }
                } else if(tabPrefUtilisateur.get(i).equals("Animaux")){
                    if(virg) newMessage+=  ",";
                    else virg=true;     
                    if(!aLaLigne)aLaLigne = true;
                        else newMessage+="\n";
                    UEstAnimaux = true;
                    if (!CEstAnimaux){
                        newMessage+=" demande au conducteur si il accepte les animaux";
                        poserQuestion = true;
                    }
                //Enfants
                } else {
                    if(virg) newMessage+=  ",";
                    else virg=true;
                    if(!aLaLigne)aLaLigne = true;
                        else newMessage+="\n";
                    UEstEnfants = true;
                    if (!CEstEnfants){
                        newMessage+=" demande au conducteur si il accepte les enfants";
                        poserQuestion = true;
                    }
                }
            }
            if (!UEstFumeur && CEstFumeur ){
                if(virg) newMessage+=  ",";
                else virg=true;
                if(!aLaLigne)aLaLigne = true;
                    else newMessage+="\n";
                newMessage+=  " n'est pas Fumeur";
                int rndAccepte = negociationPrix(0,10);
                //Accepte de voyager
                if (rndAccepte >= 2){
                    newMessage+=  " et accepte de voyager dans un voyage fumeur";
                //N'accepte pas
                } else {
                    newMessage+=  " et n'accepte pas de voyager dans un voyage fumeur";
                    discussionFini = true;
                }
            }
            if (CEstAnimaux && !UEstAnimaux){
                if(virg) newMessage+=  ",";
                else virg=true;
                if(!aLaLigne)aLaLigne = true;
                    else newMessage+="\n";
                int rndAccepte = negociationPrix(0,10);
                //Accepte de voyager
                if (rndAccepte>= 2){
                    newMessage+=  " accepte un voyage avec des animaux";
                //N'accepte pas
                } else {
                    newMessage+=  " n'accepte pas de voyage avec des animaux";
                    discussionFini = true;
                }
            }
            if (CEstEnfants && !UEstEnfants){
                if(virg) newMessage+=  ",";
                else virg=true;
                if(!aLaLigne)aLaLigne = true;
                        else newMessage+="\n";
                int rndAccepte = negociationPrix(0,10);
                //Accepte de voyager
                if (rndAccepte>= 2){
                    newMessage+=  " accepte un voyage avec des enfants";
                //N'accepte pas
                } else {
                    newMessage+=  " n'accepte pas de voyage avec des enfants";
                    discussionFini = true;
                }
            }
            if(CEstAnimaux==UEstAnimaux&&CEstEnfants==UEstAnimaux&&CEstFumeur==UEstFumeur)
                newMessage+=  " accepte les conditions du voyage";
            if (poserQuestion||discussionFini)
            messages.add(newMessage);
        }
        virg=false;
        aLaLigne=false;
        if (!discussionFini && poserQuestion){
            newMessage="Le conducteur";
            if(UEstEnfants && !CEstEnfants){
                virg = true;
                if(!aLaLigne)aLaLigne = true;
                        else newMessage+="\n";
                int rndAccepte = negociationPrix(0,10);
                //Accepte de voyager
                if (rndAccepte>= 2){
                    newMessage+=  " tol??re les enfants";
                //N'accepte pas
                } else {
                    newMessage+=  " n'accepte pas de voyager avec des enfants";
                    discussionFini = true;
                }
            }
            if(UEstAnimaux && !CEstAnimaux){
                if(virg) newMessage+=  ",";
                else virg=true;
                if(!aLaLigne)aLaLigne = true;
                        else newMessage+="\n";
                int rndAccepte = negociationPrix(0,10);
                //Accepte de voyager
                if (rndAccepte>= 2){
                    newMessage+=  " tol??re les animaux";
                //N'accepte pas
                } else {
                    newMessage+=  " n'accepte pas de voyager avec des animaux";
                    discussionFini = true;
                }
            }
            if(QFumer){
                if(virg) newMessage+=  ",";
                else virg=true;
                if(!aLaLigne)aLaLigne = true;
                        else newMessage+="\n";
                int rndAccepte = negociationPrix(0,10);
                //Accepte de voyager
                if (rndAccepte>= 2){
                    newMessage+=  " accepte le fumeur";
                //N'accepte pas
                } else {
                    newMessage+=  " n'accepte pas de fumeur dans son voyage";
                    discussionFini = true;
                }
            }
            messages.add(newMessage);
        }
        
        //Premi??re proposition
        if (!discussionFini){
            newMessage = "L'utilisateur propose "+nbreUtil+"???";
            messages.add(newMessage);
        }
        
        
        boolean premierIteration = true;
        //D??but N??gociation
        int nbreNegocie=0;
        int compteur =1;
        int prixC = 0;
        int prixUtil = 0;
        //
        //N??gociation du prix
        //
        while (!discussionFini){
            //Utilisateur
            if (estUtilisateur){
                if (prixUtil==1){
                    newMessage="Utilisateur ne montera pas au dessus de "+nbreUtil+"???";
                    discussionFini = true;
                }else if (nbreNegocie<=prixMaxUtilisateur){
                    newMessage="Utilisateur ok pour "+nbreNegocie+"???";
                    discussionFini = true;
                    tombentDaccord = true;
                } else {
                    if (nbreUtil == prixMaxUtilisateur){
                        prixUtil++;
                    } else {
                        nbreUtil = negociationPrix(nbreUtil,prixMaxUtilisateur);
                    }
                    newMessage = "Utilisateur propose "+ nbreUtil +"???";
                    estUtilisateur=false;
                }
            //conducteur
            } else {
                if (prixC==1){
                    newMessage="Conducteur ne descendra pas en dessous de "+nbreNegocie+"???";
                    discussionFini = true;
                }else if (nbreUtil>=prixMinConducteur){
                    newMessage="Conducteur ok pour "+nbreUtil+"???";
                    discussionFini = true;
                    tombentDaccord = true;
                } else {
                    //1??re it??ration
                    if (premierIteration){
                        nbreNegocie = this.prix;
                        premierIteration = false;
                    }
                    if (nbreNegocie == prixMinConducteur){
                        prixC++;
                        
                    } else {
                        nbreNegocie=negociationPrix(prixMinConducteur,nbreNegocie);
                    }
                    newMessage = "Conducteur propose "+ nbreNegocie +"???";
                    estUtilisateur=true;
                }
            }
            compteur++;
            if (compteur==10){
                newMessage="Conducteur abandonne car n??gociation trop longue";
                discussionFini = true;
            }
            messages.add(newMessage);
        }
        if(tombentDaccord){
            this.estReserve=true;
            this.voyage.ajouterPassager();
            if(UEstEnfants)this.voyage.ajouterPassager();
        }
        enregisterConversation(messages);        
    }
        
    public int negociationPrix(int min,int max){
        int rndNumber = min + (int)(Math.random() * ((max - min) + 1));
        if(rndNumber<=0){
            return 0;
        } else {
            return rndNumber;
        }
    }
    
    public int getPrix() {
        return prix;
    }
    
    public String getVoyage() {
        return this.voyage.getVoyage();
    }
    
    public String getDate() {
        return this.voyage.getDate();
    }

    public Utilisateur getVoyageur() {
        return voyageur;
    }

    public Utilisateur getConducteur() {
        return conducteur;
    }

    public String getNomvoyageur() {
        return nomvoyageur;
    }

    public String getNomconducteur() {
        return nomconducteur;
    }
    
    public boolean getEstReserve(){
        return this.estReserve;
    }

}
