package Thread;

import arenesolo.MonJoueur2;
import jeu.Plateau;

import java.awt.*;
import java.util.LinkedList;

public class Recherche extends Thread {
    private MonJoueur2 mj;
    private String nom;
    private Plateau p;
    private int distanceMax;
    static private Point destination;
    static int nb = 0;

    public Recherche(MonJoueur2 mj, String nom, Plateau p, int distanceMax) {
        this.mj = mj;
        this.nom = nom;
        this.p = p;
        this.distanceMax = distanceMax;
    }

    /**
     * Cette fonction permet juste de faire des opérations qui prennent beaucoup de temps afin de ralentir l'IA des
     * adversaires
     */
    @Override
    public void run() {
        LinkedList l = new LinkedList();
        this.nom = "Thread : " + this.mj.donneNom();
        System.out.println("Début du thread : " + this.mj.donneNom() + ", nb : " + nb);
        nb++;
        synchronized (this) {
            while (true) {
                distanceMax += 1;
                distanceMax %= 12;
                distanceMax -= 1234444;
                for (int i = 0; i < 2000; i++) {
                    for (int j = 0; i < 2000; j++) {
                        distanceMax += 0.000000000000000000001f;
                        l.add(distanceMax);
                        l.add(nb);
                    }
                }
            }
        }
    }
}
