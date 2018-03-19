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

    /**
     * Parcours en spirale
     *
     * @param x0 position X initiale
     * @param y0 position Y initiale
     * @param distanceMax distance a parcourir
     */
    void move(int x0, int y0, int distanceMax) {
        // directions possibles: G=(-1,0) H=(0,-1) D=(1,0) B=(0,1)
        int[] dx = new int[] {-1,0,1,0};
        int[] dy = new int[] {0,-1,0,1};
        int dirIndex=0;

        // distance parcourue
        int distance=0;

        // nombre de pas a faire
        int stepToDo=1;

        // position courante
        int x=x0, y=y0;
        //System.out.println("Initial position: " +x+","+y);

        while (true) {

            // a faire 2 fois avec le meme nombre de pas (gauche+haut) ou (droite+bas)
            for (int i = 0; i < 2; i++) {

                // déplacement du nombre de pas
                for (int j = 0; j < stepToDo; j++) {

                    // condition de sortie
                    distance++;
                    if(distance>distanceMax) return;

                    // déplacement
                    x+=dx[dirIndex];
                    y+=dy[dirIndex];

                    System.out.println("Current position: " +x+","+y+" distance="+distance);
                }

                // tourne a droite
                dirIndex = (dirIndex + 1) % 4;
            }

            // incrementer le nombre de pas a faire
            stepToDo++;
        }

    }

    // action
    @Override
    public Action faitUneAction(Plateau etatDuJeu) {
        chercheProche(etatDuJeu, this.donnePosition());
        return Action;
        etatDuJeu.
        //return super.faitUneAction(etatDuJeu);

    }
    //commentaries
    @Override
    protected void finDePartie(String lePlateau) {
        //testGermain
        System.out.println("La partie est finie.");
        // destructions des variables, nettoyage, enregistrement des logs et statistiques, etc...
    }

    public void chercheProche(Plateau etatDuJeu, Point position){

    }
}