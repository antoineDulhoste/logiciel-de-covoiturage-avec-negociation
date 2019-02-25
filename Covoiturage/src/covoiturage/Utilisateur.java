/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package covoiturage;

import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author antoine dulhoste
 */
public class Utilisateur {
    public int id;
    public String nom;
    public String prenom;
    public String mdp;
    public boolean estAdmin; //0:utilisteur 1:admin
    public String email;
    
    public Utilisateur(int id,String nom,String prenom,String mdp,boolean estAdmin,String email){
        this.id=id;
        this.nom=nom;
        this.email=email;
        this.estAdmin=estAdmin;
        this.prenom=prenom;
        this.mdp=mdp;
    }
    public void creerUtil() throws IOException{
        String ajout=this.id+" "+this.nom+" "+this.prenom+" "+this.mdp+" "+this.estAdmin+" "+this.email+";";
        FileWriter fw = new FileWriter("utilisateur.txt",true);
        fw.write(ajout);
        fw.close();
    }
    @Override
    public String toString(){
        return "id:"+this.id+" nom:"+this.nom+" prenom:"+this.prenom+" mdp:"+this.mdp+" estAdmin"+this.estAdmin+" email:"+this.email;
    }
    
}
