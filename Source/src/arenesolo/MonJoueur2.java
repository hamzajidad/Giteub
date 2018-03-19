/*
 * Code minimal pour crÃ©er son propre joueur des Arenes.
 */
package arenesolo;

import jeu.Joueur;
import jeu.Plateau;
import jeu.astar.Node;

import java.awt.*;
import java.util.*;

public class MonJoueur2 extends jeu.Joueur {
    static Point POSITION_DEPART;
    static int NUMERO_JOUEUR;
    int NBsites=0;

    /**
     *  decrit le nom du joueur
     * @param nom
     */
    public MonJoueur2(String nom) { super(nom); }

    /**
     * decrit la couleur du joueur pour etre distingué des 3 autres
     * @param couleur
     */
    @Override
    protected void debutDePartie(int couleur) {
        POSITION_DEPART =this.donnePosition();
        NUMERO_JOUEUR=couleur;
        System.out.println("La partie commence, je suis le joueur " + couleur + ".");


        // initialisation des algorithms, etc...
    }

    /**
     *
     * @param etatDuJeu  on a en parametre l'état du jeu
     * @return ça retourne l'action en cours RIEN, GAUCHE, DROITE, HAUT, BAS
     */
    public Action chercherTresor(Plateau etatDuJeu, Point currentposition){
        HashMap<Integer, ArrayList<Point>> positionSitesFouille = etatDuJeu.cherche(currentposition, 100, Plateau.CHERCHE_SITE); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point>  sites = positionSitesFouille.get(2);

        Point destination = TrouvePlusProche(currentposition, sites);
        while (Plateau.donneProprietaireDuSite(etatDuJeu.donneContenuCellule(destination)) == NUMERO_JOUEUR) {
            // si notre joueur est proprietaire du site // testé fonctionne
            System.out.println("!!!!! je suis proprietaire");
            positionSitesFouille.values().remove(destination);
            sites.removeAll(Collections.singleton(destination));

            System.out.println(positionSitesFouille.values());
            destination = TrouvePlusProche(currentposition, sites);
        }
        if (etatDuJeu.donneCheminEntre(destination, currentposition).size() == 1) {
            NBsites=NBsites+1;
        }
        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }
    public Action chercherPognon(Plateau etatDuJeu, Point currentposition){

        HashMap<Integer, ArrayList<Point>> positionSitesFinance = etatDuJeu.cherche(currentposition, 40, Plateau.CHERCHE_FINANCE); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point>  sites = positionSitesFinance.get(2);
        Point destination = TrouvePlusProche(currentposition, sites);
        if (etatDuJeu.donneCheminEntre(destination, currentposition).size() == 1) {
            //chercherPognon = false;
        }
        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }


    @Override
    public Action faitUneAction(Plateau etatDuJeu) {
            Point currentposition = this.donnePosition();

            if (currentposition==POSITION_DEPART){          // si on est retourné au départ - donc mort on recherche des sites
                NBsites=0;
            }

            if (NBsites<4){                //sil il posse moins de deux sites alors il  cherche
                return chercherTresor(etatDuJeu,currentposition);
            }
            if(this.donneSolde()<60){
                chercherPognon(etatDuJeu,currentposition);
            }
        System.out.println(" DEPLACEMENT RANDOM ");
            return super.faitUneAction(etatDuJeu);
    }

    private Point TrouvePlusProche(Point currentposition,ArrayList<Point> points) {
        Double distance=9999.0;
        Point pointProche=null;
        for(Point p : points){
            if(currentposition.distanceSq(p)<distance){
                pointProche=p;
                distance=currentposition.distanceSq(p); // renvoie c2=a2+b2 // distance de la ligne droite au point
            }
        }
        return pointProche;
    }

    /**
     *
     * @param lePlateau
     *  Le Plateau est donnée en chaine de caractère en paramétre
     * la findepartie c'est quand le nombre maximum de tours est réalisé.
     *                   // Exception: Si deux joueurs ont le même nombre de points de notoriété, il n'y a pas de gagnant.
     */

    @Override
    protected void finDePartie(String lePlateau) {
        System.out.println("Encore une belle victoire");
    }
    /**
     *
     * @param etatDuJeu l'etat du jeu en cours
     * @param destination la destination du joueur selectionné
     * @param depart la position du départ
     * @return
     */
    public Action prochainMouvementVers(Plateau etatDuJeu, Point destination, Point depart){
        ArrayList<Node> chemin = etatDuJeu.donneCheminEntre(destination,depart);
        Node nextpos;
        if(chemin.size()>1)
            nextpos = chemin.get(chemin.size()-2); // 6,6
        else {
            nextpos = new Node(destination.x, destination.y);
            destination=null;
            System.out.println("destination atteinte");
        }
        if(nextpos.getPosX()>depart.x){
            return Action.DROITE;
        }
        if(nextpos.getPosX()<depart.x){
            return Action.GAUCHE;
        }
        if(nextpos.getPosX()>depart.y){
            return Action.HAUT;
        }
        if(nextpos.getPosX()<depart.y){
            return Action.BAS;
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
}