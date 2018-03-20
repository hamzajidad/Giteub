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
     *
     */
    private static Point POSITION_DEPART;
    /**
     *
     */
    private static int NUMERO_JOUEUR;
    /**
     *
     */
    private int NBsites = 0;
    /**
     *
     */
    private static int tourDepart = 0;
    /**
     *
     */
    private Recherche tr;
    /**
     *
     */
    private Action a;
    /**
     *
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
     * Description fonction ici
     *
     * @param etatDuJeu on a en parametre l'état du jeu
     * @return ça retourne l'action en cours RIEN, GAUCHE, DROITE, HAUT, BAS
     */
    private Action chercherTresor(Plateau etatDuJeu, Point currentposition) {
        HashMap<Integer, ArrayList<Point>> positionSitesFouille = etatDuJeu.cherche(currentposition, 50, Plateau.CHERCHE_SITE); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point> sites = positionSitesFouille.get(2);
        ArrayList<Point> sitesImportants = new ArrayList<>();
        for (Point s : sites) {
            if (estUnSiteImportant(etatDuJeu, s)) {
                sitesImportants.add(s);
            }
        }
        Point destination = TrouvePlusProche(etatDuJeu, currentposition, sitesImportants);
        while (Plateau.donneProprietaireDuSite(etatDuJeu.donneContenuCellule(destination)) == NUMERO_JOUEUR) { // si notre joueur est proprietaire du site // testé fonctionne
            System.out.println("!!!!! je suis deja proprietaire de " + destination);
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
     * Description fonction ici
     *
     * @param etatDuJeu
     * @param currentposition
     * @return
     */
    private Action chercherPognon(Plateau etatDuJeu, Point currentposition) {
        HashMap<Integer, ArrayList<Point>> positionSitesFinance = etatDuJeu.cherche(currentposition, 20, Plateau.CHERCHE_FINANCE); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point> sites = positionSitesFinance.get(1);
        Point destination = TrouvePlusProche(etatDuJeu, currentposition, sites);
        if (etatDuJeu.donneCheminEntre(destination, currentposition).size() == 1) {
            System.out.println("pognon trouvé");
        }
        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }

    /**
     * Description fonction ici
     *
     * @param etatDuJeu
     * @param currentposition
     * @return
     */
    private Action chercherBagarre(Plateau etatDuJeu, Point currentposition) {
        HashMap<Integer, ArrayList<Point>> positionsJoueur = etatDuJeu.cherche(currentposition, etatDuJeu.donneTaille(), Plateau.CHERCHE_JOUEUR); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point>  joueurs = positionsJoueur.get(4);
        joueurs.remove(this.donnePosition());
        ArrayList<Point>  joueursAvecSite = new ArrayList<Point>();
        for (Point p : joueurs) {
            if (etatDuJeu.nombreDeSites1Joueur(etatDuJeu.donneJoueurEnPosition(p).donneCouleurNumerique()-1) > 0 ){
                joueursAvecSite.add(p);
            }
        }

        System.out.println("\"------------------->JOUEUR 4: Joueur Supprime:"+joueurs.remove(currentposition) );
        Point destination = TrouvePlusProche(etatDuJeu,currentposition, joueursAvecSite);

        System.out.println("------------------->JOUEUR 4: Joueur Proche =" + destination);
        if (etatDuJeu.donneCheminEntre(destination, currentposition).size() == 1) {
            System.out.println("Bagarre trouvé");
        }
        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }

    /**
     * Fonction appellée par e maitre du jeu au debut de chaque tour
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
            //tr = new Recherche(this, this.donneNom(), etatDuJeu,20);
            //tr.setPriority(MAX_PRIORITY);
            System.out.println("Tour de départ !!!!");
            POSITION_DEPART = this.donnePosition();
            calculeNumeroJoueur(this.donneCouleur());
            tourDepart++;
        }

        // thread de la mort cloque tout les autres joueurs priority high
        Point currentposition = this.donnePosition();
        System.out.println("current position : " + currentposition + ", position départ : " + POSITION_DEPART + " Nb site : " + NBsites); //calcule le numero du joueur

        if (currentposition == POSITION_DEPART) {          // si on est retourné au départ - donc mort on recherche des sites
            NBsites = 0;
        }

        if (this.donneSolde() < 40) {
            a = chercherPognon(etatDuJeu, currentposition);
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
     * cherche si les cases adjacentes contiennent des choses d'interet
     *
     * @param s
     */
    private Point caseProcheContient(Plateau etatDuJeu, Point currentposition) {
        HashMap<Integer, ArrayList<Point>> FouilleProches = etatDuJeu.cherche(currentposition, 1, Plateau.CHERCHE_SITE); // cherche n'importe quel site, 1 ou 3 //
        HashMap<Integer, ArrayList<Point>> JoueurProches = new HashMap<>(); // cherche n'importe quel site, 1 ou 3 //
        HashMap<Integer, ArrayList<Point>> FinanceProches= new HashMap<>();
        if(this.donneSolde()<60){
            FinanceProches = etatDuJeu.cherche(currentposition, 1, Plateau.CHERCHE_FINANCE);
        }
        if(this.donneSolde()<60){
            JoueurProches = etatDuJeu.cherche(currentposition, 1, Plateau.CHERCHE_JOUEUR);
        }

        ArrayList<Point>  finances = FinanceProches.get(1);
        ArrayList<Point>  joueurs = JoueurProches.get(4);
        joueurs.remove(donnePosition());
        ArrayList<Point>  fouilles = FouilleProches.get(2);

        if( finances.size()>0 || joueurs.size()>0 || fouilles.size()>0){
            if(joueurs.size()>0){
                int nbSite =0, nbSiteMax = 0;

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
                    if(this.estUnSiteImportant(etatDuJeu,f)){
                        return f;
                    }
                }
                return TrouvePlusProche(etatDuJeu,currentposition,fouilles);
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
        ArrayList<Point> spawns = new ArrayList<>();
        HashMap<Integer, ArrayList<Point>> list = etatDuJeu.cherche(positionJoueurDepart, 20, Plateau.CHERCHE_JOUEUR);
        spawns = list.get(4);
        return spawns;
    }

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
     * Description fonction ici
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
     * Description fonction ici
     *
     * @param etatDuJeu   l'etat du jeu en cours
     * @param destination la destination du joueur selectionné
     * @param depart      la position du départ
     * @return
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
     * Description fonction ici
     *
     * @param plateau
     * @param p
     * @return
     */
    private boolean estUnSiteImportant(Plateau plateau, Point p) {
        int contenu = plateau.donneContenuCellule(p);
        if (!Plateau.contientUnSite(contenu))
            return false;
        int typeSite = Plateau.donneTypeSites(contenu);
        return typeSite == 2;
    }
}