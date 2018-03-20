package Thread;

import arenesolo.MonJoueur2;
import jeu.Plateau;

import java.awt.*;

import static arenesolo.MonJoueur.DonnePointObjectifPlusProche;

public class Recherche extends Thread {
    private MonJoueur2 mj;
    private String nom;
    private Plateau p;
    private int distanceMax;
    static private Point destination;

    public Recherche(MonJoueur2 mj, String nom, Plateau p, int distanceMax) {
        this.mj = mj;
        this.nom = nom;
        this.p = p;
        this.distanceMax = distanceMax;
    }

    public static Point getDestination() {
        return destination;
    }

    @Override
    public void run(){
        System.out.println("Début du thread : " + this.getName());

    }

}
