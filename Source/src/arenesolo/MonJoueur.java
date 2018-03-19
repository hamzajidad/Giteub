/*
 * Code minimal pour crÃ©er son propre joueur des Arenes.
 */
package arenesolo;

import Thread.Recherche;
import jeu.Plateau;

import java.awt.*;
import java.util.ArrayList;
import jeu.astar.Node;

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
    static public Point DonnePointObjectifPlusProche(Plateau etatdujeu, Point p, int distanceMax, Integer objectif) {
        //Recherche du temple le plus proche
        int x0=p.x;
        int y0=p.y;
        System.out.println("current position = "+String.valueOf(x0)+" "+String.valueOf(y0));
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
                    if(distance>distanceMax) return null;
                    // déplacement
                    x+=dx[dirIndex];
                    y+=dy[dirIndex];
                    //System.out.println("Current position: " +x+","+y+" distance="+distance+" contenu = "+etatdujeu.donneContenuCellule(x,y));
                    if(etatdujeu.donneContenuCellule(x,y)==objectif){
                        return new Point(x,y);
                    }
                }
                // tourne a droite
                dirIndex = (dirIndex + 1) % 4;
            }
            // incrementer le nombre de pas a faire
            stepToDo++;
        }
    }
    public Action prochaineDirectionVers(Plateau etatDuJeu, Point destination, Point depart){

    public void prochaineDirectionVers(Plateau etatDuJeu, Point destination, Point depart){
        ArrayList<Node> chemin = etatDuJeu.donneCheminEntre(destination,depart);
        Node nextpos;
        if(chemin.size()>1)
            nextpos = chemin.get(chemin.size()-2); // 6,6
        else nextpos= new Node(destination.x,destination.y);
        System.out.println(" enxt position = "+nextpos.toString());
        if(nextpos.getPosX()>depart.x){
            return Action.DROITE;
        }
        if(nextpos.getPosX()<depart.x){
            return Action.GAUCHE;
        }
        if(nextpos.getPosX()>depart.y){
            return Action.BAS;
        }
        if(nextpos.getPosX()<depart.y){
            return Action.HAUT;
        }
        else {
            int j =(int)(Math.random() * 6.0D);

                if(j==1)
                    return Action.GAUCHE;
                if(j==2)
                    return Action.DROITE;
                if(j==3)
                    return Action.HAUT;
                else
                    return Action.BAS;

        }
    }
    // action
    @Override
    public Action faitUneAction(Plateau etatDuJeu) {
        final int distanceMax = 110;
        Point currentposition=this.donnePosition();
        Point destination = DonnePointObjectifPlusProche(etatDuJeu, currentposition,distanceMax, 65536);
        System.out.println(" destination = "+destination.toString());
        prochaineDirectionVers(etatDuJeu, destination, currentposition );
        Thread.Recherche r = new Recherche(this, this.donneNom(), etatDuJeu, distanceMax, 16384 );
        r.start();
        return Action.RIEN;
        //return super.faitUneAction(etatDuJeu);

    }
    //commentaries
    @Override
    protected void finDePartie(String lePlateau) {
        //testGermain96
        System.out.println("La partie est finie.");
        // destructions des variables, nettoyage, enregistrement des logs et statistiques, etc...
    }
    /*
    public void chercheProche(Plateau etatDuJeu, Point p){
        p.x=p.x+1;

        etatDuJeu.donneContenuCellule(p);
    }
    */
}