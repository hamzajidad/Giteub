/*
 * Code minimal pour crÃ©er son propre joueur des Arenes.
 */
package arenesolo;

import jeu.Plateau;

import java.awt.*;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;

import jeu.astar.Node;

import static jeu.Plateau.MASQUE_ENDROIT_SITE2;

public class MonJoueur extends jeu.Joueur {
    static HashMap<Integer, Point> MAP = new HashMap<Integer, Point>();
    ArrayList<Point> sitesPossedes = new ArrayList<Point>();

    /**
     *  decrit le nom du joueur
     * @param nom
     */
    public MonJoueur(String nom) { super(nom); }

    /**
     * decrit la couleur du joueur pour etre distingué des 3 autres
     * @param couleur
     */
    @Override
    protected void debutDePartie(int couleur) {
        System.out.println("La partie commence, je suis le joueur " + couleur + ".");
        // initialisation des algorithms, etc...
    }

    /**
     * Parcours en spirale
     * le parcours en spirale pour decrire autour d'un point fixe (Point p) les objectives les plus proches.
     * @param x0 position X initiale
     * @param y0 position Y initiale
     * @param distanceMax distance a parcourir
     */
    public void mappage(Plateau etatDuJeu){
        Node[][] tab = etatDuJeu.donneGrillePourAstar();
        for(int y = 0; y<etatDuJeu.donneTaille()/2; y++) {
            for (int x = 0; x < etatDuJeu.donneTaille() / 2; x++) {
                MAP.put(etatDuJeu.donneContenuCellule(x,y),new Point(x,y));
            }
        }
    }

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
                    int contenu=etatdujeu.donneContenuCellule(x,y);
                    if((contenu & MASQUE_ENDROIT_SITE2) != 0){ // si la case scannee vaut la valeur objectif passée en parametre
                        /*
                        if(Plateau.donneProprietaireDuSite(contenu)!=Plateau.PRESENCE_JOUEUR1) {
                            System.out.println("Valeur du proprietraire "+String.valueOf(Plateau.donneProprietaireDuSite(contenu)));
                            return new Point(x, y);
                        }*/
                        if(Plateau.donneProprietaireDuSite(contenu)!=1) {
                            System.out.println("Valeur du proprietraire "+String.valueOf(Plateau.donneProprietaireDuSite(contenu)));
                            return new Point(x, y);
                        }
                    }
                }
                // tourne a droite
                dirIndex = (dirIndex + 1) % 4;
            }
            // incrementer le nombre de pas a faire
            stepToDo++;
        }
    }

    /**
     *
     * @param etatDuJeu l'etat du jeu en cours
     * @param destination la destination du joueur selectionné
     * @param depart la position du départ
     * @return
     */
    public Action prochaineDirectionVers(Plateau etatDuJeu, Point destination, Point depart){
        ArrayList<Node> chemin = etatDuJeu.donneCheminEntre(destination,depart);
        Node nextpos;
        if(chemin.size()>1)
            nextpos = chemin.get(chemin.size()-2); // 6,6
        else nextpos= new Node(destination.x,destination.y);
        System.out.println(" next position = "+nextpos.toString());
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

    /**
     *
     * @param etatDuJeu  on a en parametre l'état du jeu
     * @return ça retourne l'action en cours RIEN, GAUCHE, DROITE, HAUT, BAS
     */
    @Override
    public Action faitUneAction(Plateau etatDuJeu) {
        //mappage(etatDuJeu);



        Point currentposition=this.donnePosition();
        Point destination = DonnePointObjectifPlusProche(etatDuJeu, currentposition,200, 65536); // se diriger vers un temple = 65536
        System.out.println(" destination = "+destination.toString());


        System.out.println(Plateau.donneProprietaireDuSite(etatDuJeu.donneContenuCellule(destination)));
        return prochaineDirectionVers(etatDuJeu, destination, currentposition );
    }
    //commentaries

    /**
     *
     * @param lePlateau
     *  Le Plateau est donnée en chaine de caractère en paramétre
     * la findepartie c'est quand le nombre maximum de tours est réalisé.
     *                   // Exception: Si deux joueurs ont le même nombre de points de notoriété, il n'y a pas de gagnant.
     */

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