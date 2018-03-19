/*
 * Code minimal pour crÃ©er son propre joueur des Arenes.
 */
package arenesolo;

import jeu.Plateau;

import java.awt.*;

public class MonJoueur extends jeu.Joueur {

    public MonJoueur(String nom) { super(nom); }

    @Override
    protected void debutDePartie(int couleur) {
        System.out.println("La partie commence, je suis le joueur " + couleur + ".");
        // initialisation des algorithms, etc...
    }

    // action
    @Override
    public Action faitUneAction(Plateau etatDuJeu) {
        chercheProche(etatDuJeu, this.donnePosition());
        return Action;
        //return super.faitUneAction(etatDuJeu);

    }
    //commentaries
    @Override
    protected void finDePartie(String lePlateau) {
        System.out.println("La partie est finie.");
        // destructions des variables, nettoyage, enregistrement des logs et statistiques, etc...
    }

    public void chercheProche(Plateau etatDuJeu, Point position){

    }
}