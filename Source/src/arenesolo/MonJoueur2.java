/*
 * Code minimal pour crÃ©er son propre joueur des Arenes.
 */
package arenesolo;

import Thread.Recherche;
import jeu.Joueur;
import jeu.Plateau;
import jeu.astar.Node;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Thread.MAX_PRIORITY;

public class MonJoueur2 extends jeu.Joueur {
    /**
     *ma position  de départ dans la partie courante
     */
    private static Point POSITION_DEPART;

    /**
     *mon numéro dans la partie courante
     */
    private static int NUMERO_JOUEUR;

    /**
     *mes nombres de sites dans la partie courante
     */
    private int NBsites = 0;

    /**
     *le tour départ
     */
    private static int tourDepart = 0;

    /**
     *le thread pour faire perdre du temps au adversaire
     */
    private Recherche tr;

    /**
     *l'action à faire à chaque tour
     */
    private Action a;

    /**
     *le temps de départ d'une action pour calculer le temps d'exécution
     */
    private long t;


    /**
     * decrit le nom du joueur
     *
     * @param nom
     */
    MonJoueur2(String nom) {
        super(nom);
    }

    /**
     * decrit la couleur du joueur pour etre distingué des 3 autres
     *
     * @param couleur
     */
    @Override
    protected void debutDePartie(int couleur) {
        System.out.println("La partie commence, je suis le joueur " + couleur + ".");
        // initialisation des algorithms, etc...
    }

    /**
     * fonction utilisée lorsque le joueur cherche un tresor
     *
     * @param etatDuJeu on a en parametre l'état du jeu
     * @return ça retourne l'action en cours RIEN, GAUCHE, DROITE, HAUT, BAS
     */
    private Action chercherTresor(Plateau etatDuJeu, Point currentposition) {
        HashMap<Integer, ArrayList<Point>> positionSitesFouille = etatDuJeu.cherche(currentposition, etatDuJeu.donneTaille(), Plateau.CHERCHE_SITE); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point> sites = positionSitesFouille.get(2);
        ArrayList<Point> sitesImportants = new ArrayList<>();
        for (Point s : sites) {
            if (estUnSiteImportant(etatDuJeu, s)) {
                sitesImportants.add(s);
            }
        }
        Point destination = TrouvePlusProche(etatDuJeu, currentposition, sitesImportants);
        while (Plateau.donneProprietaireDuSite(etatDuJeu.donneContenuCellule(destination)) == NUMERO_JOUEUR) { // si notre joueur est proprietaire du site // testé fonctionne
            positionSitesFouille.values().remove(destination);
            sitesImportants.removeAll(Collections.singleton(destination));
            destination = TrouvePlusProche(etatDuJeu, currentposition, sitesImportants);
        }
        if (etatDuJeu.donneCheminEntre(destination, currentposition).size() == 1) {
            NBsites++;
        }
        System.out.println(" destination = " + destination.toString());
        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }

    /**
     * fonction utilisée lorsque le joueur cherche du pognon
     *
     * @param etatDuJeu
     * @param currentposition
     * @return action à faire
     */
    private Action chercherPognon(Plateau etatDuJeu, Point currentposition) {
        HashMap<Integer, ArrayList<Point>> positionSitesFinance = etatDuJeu.cherche(currentposition, etatDuJeu.donneTaille(), Plateau.CHERCHE_FINANCE); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point> sites = positionSitesFinance.get(1);
        Point destination = TrouvePlusProche(etatDuJeu, currentposition, sites);
        if (etatDuJeu.donneCheminEntre(destination, currentposition).size() == 1) {
            System.out.println("pognon trouvé");
        }
        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }

    /**
     * fonction utilisée lorsque le joueur cherche de la bagarre
     *on cherche le joueur avec le plus de site le plus proche
     * @param etatDuJeu -le plateau
     * @param currentposition -la position de départ
     * @return l'action à faire
     */
    private Action chercherBagarre(Plateau etatDuJeu, Point currentposition) {
        System.out.println("Je cherche la bagarre");
        HashMap<Integer, ArrayList<Point>> positionsJoueur = etatDuJeu.cherche(currentposition, etatDuJeu.donneTaille(), Plateau.CHERCHE_JOUEUR); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point>  joueurs = positionsJoueur.get(4);
        System.out.println(joueurs.remove(this.donnePosition()));
        ArrayList<Point>  joueursAvecSite = new ArrayList<>();
        for (Point p : joueurs) {
            if (etatDuJeu.nombreDeSites1Joueur(etatDuJeu.donneJoueurEnPosition(p).donneCouleurNumerique()-1) > 0 ){
                joueursAvecSite.add(p);
            }
        }
        Point destination;
        if(joueursAvecSite.size()>0) {
             destination= TrouvePlusProche(etatDuJeu, currentposition, joueursAvecSite);
        }
        else{
            destination= TrouvePlusProche(etatDuJeu, currentposition, joueurs);
        }

        System.out.println("------------------->MOI: Joueur Proche = " + destination);
        if (etatDuJeu.donneCheminEntre(destination, currentposition).size() == 1) {
            System.out.println("Bagarre trouvé");
        }
        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }

    /**
     * cherche les cases autour du joueur si elle contiennent Site de fouille/joueur/Financenement alors retourne l'action correspondante
     *
     * @param etatDuJeu on a en parametre l'état du jeu
     * @return ça retourne l'action en cours RIEN, GAUCHE, DROITE, HAUT, BAS
     */
    private Action chercherCaseAutour(Plateau etatDuJeu, Point currentposition) {
        Point autour = this.caseProcheContient(etatDuJeu,currentposition);
        if(autour != null){
            return prochainMouvementVers(etatDuJeu, autour, currentposition);
        }
        return null;
    }

    /**
     * Fonction appelée a chaque debut de tout par le maitre du jeu,
     * reçoit le plateau en parametre et doit renvoyer une action correspondante au deplacement HAUT/BAC/GAUCHE/DROITE
     *
     * @param etatDuJeu
     * @return
     */
    @Override
    public Action faitUneAction(Plateau etatDuJeu) {
        t = System.currentTimeMillis();
        if (tourDepart != 0) {
            tr.interrupt();
            tr.stop(); //deprecated
        }
        if(tourDepart == 0) {
            tr = new Recherche(this, this.donneNom(), etatDuJeu,20);
            tr.setPriority(MAX_PRIORITY);
            System.out.println("Tour de départ !!!!");
            POSITION_DEPART = this.donnePosition();
            calculeNumeroJoueur(this.donneCouleur());
            tourDepart++;
        }

        // thread de la mort cloque tout les autres joueurs priority high
        Point currentposition = this.donnePosition();

        if (currentposition == POSITION_DEPART) {          // si on est retourné au départ - donc mort on recherche des sites
            NBsites = 0;
        }

        if (this.donneSolde() < 40) {
            a = chercherPognon(etatDuJeu, currentposition);
        }

        Action actionAutour = this.chercherCaseAutour(etatDuJeu, currentposition);
        if( actionAutour!= null){
            return actionAutour;
        }

        if (NBsites < 2) {                //sil il posse moins de deux sites alors il  cherche
            a = chercherTresor(etatDuJeu, currentposition);

        } else {
            a = chercherBagarre(etatDuJeu, currentposition);
        }

        if (tourDepart != 0) {
            tr = new Recherche(this, this.donneNom(), etatDuJeu, 20);
            tr.setPriority(MAX_PRIORITY); //Mettre en priorité ce thread
        }

        tr.start();
        long t1 = System.currentTimeMillis();
        System.out.println("temps :" + (t1 - t));
        return a;
    }

    /**
     * cherche si les cases adjacentes contiennent des choses d'interet,
     *on cherche le joueur avec le max de site dans le cas où on a au moins 40 milliers,
     * après le centre de finance le plus proche dans le cas où on a moins 60 milliers ,
     * à la fin ,le site le plus important
     *
     * @param etatDuJeu -un plateau
     * @param currentposition -la position courante
     * @return le point correspondant avec le plus d'intérêt comme évoqué avant, sinon null
     */
    private Point caseProcheContient(Plateau etatDuJeu, Point currentposition) {
        HashMap<Integer, ArrayList<Point>> FouilleProches = etatDuJeu.cherche(currentposition, 1, Plateau.CHERCHE_SITE); // cherche n'importe quel site, 1 ou 3 //
        HashMap<Integer, ArrayList<Point>> JoueurProches = etatDuJeu.cherche(currentposition, 1, Plateau.CHERCHE_JOUEUR); // cherche n'importe quel site, 1 ou 3 //
        HashMap<Integer, ArrayList<Point>> FinanceProches= etatDuJeu.cherche(currentposition, 1, Plateau.CHERCHE_FINANCE);
        if(this.donneSolde()<60){
            FinanceProches = etatDuJeu.cherche(currentposition, 1, Plateau.CHERCHE_FINANCE);
        }
        if(this.donneSolde()>40){
            JoueurProches = etatDuJeu.cherche(currentposition, 1, Plateau.CHERCHE_JOUEUR);
        }

        ArrayList<Point>  finances = FinanceProches.get(1);
        ArrayList<Point>  joueurs = JoueurProches.get(4);
        joueurs.remove(donnePosition());
        ArrayList<Point>  fouilles = FouilleProches.get(2);

        if(this.donneSolde() > 40 && finances.size() > 0 || joueurs.size() > 0 && this.donneSolde() > 40 || fouilles.size() > 0){
            if(joueurs.size()>0){
                int nbSite, nbSiteMax = 0;

                for (Point p: joueurs) {
                    nbSite = etatDuJeu.nombreDeSites1Joueur(etatDuJeu.donneJoueurEnPosition(p).donneCouleurNumerique()-1)+etatDuJeu.nombreDeSites2Joueur(etatDuJeu.donneJoueurEnPosition(p).donneCouleurNumerique()-1);
                    if ( nbSite > nbSiteMax){
                        nbSiteMax = nbSite;
                    }
                }
                if(nbSiteMax>0){
                    for (Point p: joueurs) {
                        nbSite = etatDuJeu.nombreDeSites1Joueur(etatDuJeu.donneJoueurEnPosition(p).donneCouleurNumerique()-1)+etatDuJeu.nombreDeSites2Joueur(etatDuJeu.donneJoueurEnPosition(p).donneCouleurNumerique()-1);
                        if(nbSite == nbSiteMax ){
                            return p;
                        }
                    }
                }
                return TrouvePlusProche(etatDuJeu,currentposition,joueurs);
            }
            if(finances.size()>0){
                return TrouvePlusProche(etatDuJeu,currentposition,finances);
            }
            if(fouilles.size()>0){
                for (Point f: fouilles) {
                    int proprio=Plateau.donneProprietaireDuSite(etatDuJeu.donneContenuCellule(f));
                    if(!(proprio == NUMERO_JOUEUR)) { // si notre joueur est proprietaire du site // testé fonctionne
                        return f;
                    }
                }
            }
            return null;
        }
        else{
            return null;
        }
    }

    /**
     * Fonction qui permet de renvoyer la liste des points représentant les points de départ des adversaires.
     * Cette liste de points est à éviter pour éviter de mourir si l'adversaire respawn et qu'on se trouve dessus, qu'il nous tue.
     * @param etatDuJeu
     * @param positionJoueurDepart
     * @return
     */
    private ArrayList<Point> trouvePointSpawnAdversaire(Plateau etatDuJeu, Point positionJoueurDepart){
        ArrayList<Point> spawns;
        HashMap<Integer, ArrayList<Point>> list = etatDuJeu.cherche(positionJoueurDepart, 20, Plateau.CHERCHE_JOUEUR);
        spawns = list.get(4);
        return spawns;
    }

    /**
     * Calcul le numero des joueur en fonction couleur
     * @param s -la couleur du joueur
     */
    private void calculeNumeroJoueur(String s) {
        if (s.equalsIgnoreCase("Bleu")) NUMERO_JOUEUR = 1;
        if (s.equalsIgnoreCase("Vert")) NUMERO_JOUEUR = 2;
        if (s.equalsIgnoreCase("Rouge")) NUMERO_JOUEUR = 3;
        if (s.equalsIgnoreCase("Jaune")) NUMERO_JOUEUR = 4;
    }

    /**
     * renvoie le point le plus proche d'une liste de point
     *
     * @param etatDuJeu
     * @param currentposition
     * @param points
     * @return
     */
    private Point TrouvePlusProche(Plateau etatDuJeu, Point currentposition, ArrayList<Point> points) {
        int distance = 9999;
        Point pointProche = null;
        for (Point p : points) {
            if (etatDuJeu.donneCheminEntre(currentposition, p).size() < distance) {
                pointProche = p;
                distance = etatDuJeu.donneCheminEntre(currentposition, p).size();
            }
        }
        return pointProche;
    }

    /**
     * Appellée en fin de partie
     *
     * @param lePlateau Le Plateau est donnée en chaine de caractère en paramétre
     *                  la findepartie c'est quand le nombre maximum de tours est réalisé.
     *                  // Exception: Si deux joueurs ont le même nombre de points de notoriété, il n'y a pas de gagnant.
     */
    @Override
    protected void finDePartie(String lePlateau) {
        System.out.println("Encore une belle victoire");
    }

    /**
     * definit la direction du prochain mouvement et la renvoie sous forme d'une action
     *
     * @param etatDuJeu   l'etat du jeu en cours
     * @param destination la destination du joueur selectionné
     * @param depart      la position du départ
     * @return action à faire
     */
    private Action prochainMouvementVers(Plateau etatDuJeu, Point destination, Point depart) {
        ArrayList<Node> chemin = etatDuJeu.donneCheminEntre(destination, depart);
        Node nextpos;
        if (chemin.size() > 1) {
            nextpos = chemin.get(chemin.size() - 2); // 6,6
        } else {
            nextpos = new Node(destination.x, destination.y);
        }
        if (nextpos.getPosX() > depart.x) {
            return Action.DROITE;
        }
        if (nextpos.getPosX() < depart.x) {
            return Action.GAUCHE;
        }
        if (nextpos.getPosY() > depart.y) {
            return Action.BAS;
        }
        if (nextpos.getPosY() < depart.y) {
            return Action.HAUT;
        } else {
            int j = (int) (Math.random() * 6.0D);

            if (j == 1)
                return Action.GAUCHE;
            if (j == 2)
                return Action.DROITE;
            if (j == 3)
                return Action.HAUT;
            else
                return Action.BAS;
        }
    }

    /**
     * renvoie un boolean true si le site passé en parametre est Important
     *
     * @param plateau -le plateau
     * @param p -le point à tester
     * @return vrai si présence de site important sur le point p
     */
    private boolean estUnSiteImportant(Plateau plateau, Point p) {
        int contenu = plateau.donneContenuCellule(p);
        if (!Plateau.contientUnSite(contenu))
            return false;
        int typeSite = Plateau.donneTypeSites(contenu);
        return typeSite == 2;
    }
}