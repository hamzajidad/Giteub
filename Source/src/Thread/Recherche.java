package Thread;

import arenesolo.MonJoueur;
import jeu.Plateau;

import java.awt.*;

import static arenesolo.MonJoueur.DonnePointObjectifPlusProche;

public class Recherche extends Thread {
    private MonJoueur mj;
    private String nom;
    private Plateau p;
    private int distanceMax;
    private int Obj;
    static private Point destination;

    public Recherche(MonJoueur mj, String nom, Plateau p, int distanceMax, int obj) {
        this.mj = mj;
        this.nom = nom;
        this.p = p;
        this.distanceMax = distanceMax;
        Obj = obj;
    }

    public static Point getDestination() {
        return destination;
    }

    @Override
    public void run(){
        Point dest = DonnePointObjectifPlusProche(this.p,this.mj.donnePosition(),this.distanceMax, this.Obj);
    }

}
