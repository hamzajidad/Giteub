package arenesolo;

import jeu.Joueur;
import jeu.Plateau;
import jeu.astar.Node;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MonJoueur4 extends jeu.Joueur {
    private static Point POSITION_DEPART;
    private static int NUMERO_JOUEUR;
    private int NBsites=0;
    private static int tourDepart=0;

    /**
     *  decrit le nom du joueur
     * @param nom
     */
    MonJoueur4(String nom) { super(nom); }

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
     *
     * @param etatDuJeu  on a en parametre l'état du jeu
     * @return ça retourne l'action en cours RIEN, GAUCHE, DROITE, HAUT, BAS
     */
    public Action chercherTresor(Plateau etatDuJeu, Point currentposition){
        HashMap<Integer, ArrayList<Point>> positionSitesFouille = etatDuJeu.cherche(currentposition, 50, Plateau.CHERCHE_SITE); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point>  sites = positionSitesFouille.get(2);
        ArrayList<Point>  sitesImportants =new ArrayList<>();
        for (Point s : sites) {
            if(estUnSiteImportant(etatDuJeu, s)){
                sitesImportants.add(s);
            }
        }
        Point destination = TrouvePlusProche(etatDuJeu,currentposition, sitesImportants);
        while (Plateau.donneProprietaireDuSite(etatDuJeu.donneContenuCellule(destination)) == NUMERO_JOUEUR) {
            // si notre joueur est proprietaire du site // testé fonctionne
            System.out.println("!!!!! je suis proprietaire de "+destination);
            positionSitesFouille.values().remove(destination);
            sitesImportants.removeAll(Collections.singleton(destination));
            destination = TrouvePlusProche(etatDuJeu,currentposition, sitesImportants);        }
        if (etatDuJeu.donneCheminEntre(destination, currentposition).size() == 1) {
            NBsites++;
        }
        System.out.println(" destination= "+destination);
        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }
    private Action chercherPognon(Plateau etatDuJeu, Point currentposition){
        HashMap<Integer, ArrayList<Point>> positionSitesFinance = etatDuJeu.cherche(currentposition, 40, Plateau.CHERCHE_FINANCE); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point>  sites = positionSitesFinance.get(1);
        Point destination = TrouvePlusProche(etatDuJeu,currentposition, sites);
        if (etatDuJeu.donneCheminEntre(destination, currentposition).size() == 1) {
            System.out.println("pognon trouvé");
        }
        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }

    private Action chercherBagarre(Plateau etatDuJeu, Point currentposition){

        HashMap<Integer, ArrayList<Point>> positionsJoueur = etatDuJeu.cherche(currentposition, etatDuJeu.donneTaille(), Plateau.CHERCHE_JOUEUR); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point>  joueurs = positionsJoueur.get(4);
        ArrayList<Point>  joueursAvecSite = new ArrayList<Point>();
        for (Point p : joueurs) {
            if (etatDuJeu.nombreDeSites1Joueur(etatDuJeu.donneJoueurEnPosition(p).donneCouleurNumerique()-1) > 0 ){
                joueursAvecSite.add(p);
            }
        }

        System.out.println("\"------------------->JOUEUR 4: Joueur Supprime:"+joueurs.remove(currentposition) );
        Point destination = TrouvePlusProche(etatDuJeu,currentposition, joueursAvecSite);

        System.out.println("------------------->JOUEUR 4: Joueur Proche ="+destination);
        if (etatDuJeu.donneCheminEntre(destination, currentposition).size() == 1) {
            System.out.println("Bagarre trouvé");
        }
        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }


    @Override
    public Action faitUneAction(Plateau etatDuJeu) {
        if(tourDepart==0){
            System.out.println("Tour de départ !!!!");
            POSITION_DEPART = this.donnePosition();
            calculeNumeroJoueur(this.donneCouleur());
            tourDepart++;
        }
        // thread de la mort cloque tout les autres joueurs priority high
        Point currentposition = this.donnePosition();
        System.out.println("JOUEUR 4: current position : " + currentposition + ", position départ : " + POSITION_DEPART +" Nb site : " + NBsites);
        System.out.println("--------------->JOUEUR 4: DEPART ="+currentposition);

        //calcule le numero du joueur

        if (currentposition == POSITION_DEPART){          // si on est retourné au départ - donc mort on recherche des sites
            NBsites = 0;
        }
       if (this.donneSolde()<60){
            return chercherPognon(etatDuJeu, currentposition);
        }

        else{               //sil il posse moins de deux sites alors il  cherche
            return chercherBagarre(etatDuJeu, currentposition);
        }

    }

    private void calculeNumeroJoueur(String s) {
        if(s.equalsIgnoreCase("Bleu")) NUMERO_JOUEUR=1;
        if(s.equalsIgnoreCase("Vert")) NUMERO_JOUEUR=2;
        if(s.equalsIgnoreCase("Rouge")) NUMERO_JOUEUR=3;
        if(s.equalsIgnoreCase("Jaune")) NUMERO_JOUEUR=4;
    }

    private Point TrouvePlusProche(Plateau etatDuJeu,Point currentposition,ArrayList<Point> points) {
        int distance=9999;
        Point pointProche=null;

        for(Point p : points){
            if(etatDuJeu.donneCheminEntre(currentposition, p).size()<distance){
                pointProche=p;
                distance=etatDuJeu.donneCheminEntre(currentposition, p).size();
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
    }
    /**
     *
     * @param etatDuJeu l'etat du jeu en cours
     * @param destination la destination du joueur selectionné
     * @param depart la position du départ
     * @return
     */
    private Action prochainMouvementVers(Plateau etatDuJeu, Point destination, Point depart){
        ArrayList<Node> chemin = etatDuJeu.donneCheminEntre(destination,depart);
        Node nextpos;
        if(chemin.size()>1) {
            nextpos = chemin.get(chemin.size() - 2); // 6,6
        }
        else {
            nextpos = new Node(destination.x, destination.y);
        }
        System.out.println("--------------->JOUEUR 4: DESTINATION ="+nextpos);
        if(nextpos.getPosX()>depart.x){
            return Action.DROITE;
        }
        if(nextpos.getPosX()<depart.x){
            return Action.GAUCHE;
        }
        if(nextpos.getPosY()>depart.y){
            return Action.BAS;
        }
        if(nextpos.getPosY()<depart.y){
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

    private boolean estUnSiteImportant(Plateau plateau, Point p) {
        int contenu = plateau.donneContenuCellule(p);
        if (!Plateau.contientUnSite(contenu))
            return false;
        int typeSite = Plateau.donneTypeSites(contenu);
        return typeSite == 2;
    }
}
