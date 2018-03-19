/*
 * Code minimal pour crÃ©er son propre joueur des Arenes.
 */
package arenesolo;

import jeu.Plateau;

public class MonJoueur extends jeu.Joueur {

    public MonJoueur(String nom) { super(nom); }

    @Override
    protected void debutDePartie(int couleur) {
        System.out.println("La partie commence, je suis le joueur " + couleur + ".");
        // initialisation des algorithms, etc...
    }

    @Override
    public Action faitUneAction(Plateau etatDuJeu) {
        return super.faitUneAction(etatDuJeu);
    }

    @Override
    protected void finDePartie(String lePlateau) {
        System.out.println("La partie est finie.");
        // destructions des variables, nettoyage, enregistrement des logs et statistiques, etc...
    }
}