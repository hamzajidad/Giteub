/*
 * Code minimal pour crÃ©er son propre joueur des Arenes.
 */
package arenesolo;

import jeu.Plateau;

import java.awt.*;


public class MonJoueur extends jeu.Joueur {

    public MonJoueur(String nom) { super(nom); }
    Integer i=1;
    @Override
    protected void debutDePartie(int couleur) {

        this.changeNom("La team du Tacos");
        System.out.println("La partie commence, je suis le joueur " + couleur + ".");

        // initialisation des algorithms, etc...
    }

    // action le joueur ne bouge plus
    @Override
    public Action faitUneAction(Plateau etatDuJeu) {
        Point positionActuelle = this.donnePosition();
        Integer soldeActuel = this.donneSolde();
        Integer notorieteActuelle =this.donnePointsNotoriete();
        this.c
        if(i==1){
            i=0;
            return Action.BAS;
        }
        else{
            i=1;
            return Action.GAUCHE;
        }

    }
    //commentaire
    @Override
    protected void finDePartie(String lePlateau) {
        System.out.println("La partie est finie.");
        // destructions des variables, nettoyage, enregistrement des logs et statistiques, etc...
    }
}