/*
 * Code minimal pour crÃ©er son propre joueur des Arenes.
 */
package arenesolo;

import jeu.Joueur;
import jeu.Plateau;
import jeu.astar.Node;

import java.awt.*;
import java.util.*;

public class MonJoueur3 extends jeu.Joueur {
    /**
     * Description variable ici
     */
    private static Point POSITION_DEPART;
    /**
     * Description variable ici
     */
    private static int NUMERO_JOUEUR;
    /**
     * Description variable ici
     */
    private static int tourDepart = 0;
    /**
     * Description variable ici
     */
    Action a;

    /**
     * decrit le nom du joueur
     * IA de test lors du choix de l'IA retenue pour la compétition /!\ Ne pas utiliser /!\
     *
     * @param nom
     */
    MonJoueur3(String nom) {
        super(nom);
    }

    /**
     * Description fonction ici
     * decrit la couleur du joueur pour etre distingué des 3 autres
     *
     * @param couleur
     */
    @Override
    protected void debutDePartie(int couleur) {


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
        while (Plateau.donneProprietaireDuSite(etatDuJeu.donneContenuCellule(destination)) == NUMERO_JOUEUR) {
            // si notre joueur est proprietaire du site // testé fonctionne
            positionSitesFouille.values().remove(destination);
            sitesImportants.removeAll(Collections.singleton(destination));
            destination = TrouvePlusProche(etatDuJeu, currentposition, sitesImportants);
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
    private Action chercherPognon(Plateau etatDuJeu, Point currentposition) {
        HashMap<Integer, ArrayList<Point>> positionSitesFinance = etatDuJeu.cherche(currentposition, 40, Plateau.CHERCHE_FINANCE); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point> sites = positionSitesFinance.get(1);
        Point destination = TrouvePlusProche(etatDuJeu, currentposition, sites);

        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }

    /**
     * Description fonction ici
     *
     * @param etatDuJeu
     * @param currentposition
     * @return
     */
    public Action chercherBagarre(Plateau etatDuJeu, Point currentposition) {

        HashMap<Integer, ArrayList<Point>> positionSitesFinance = etatDuJeu.cherche(currentposition, 40, Plateau.CHERCHE_JOUEUR); // cherche n'importe quel site, 1 ou 3 //
        ArrayList<Point> sites = positionSitesFinance.get(4);
        Point destination = TrouvePlusProche(etatDuJeu, currentposition, sites);
        return prochainMouvementVers(etatDuJeu, destination, currentposition);
    }

    /**
     * Description fonction ici
     *
     * @param etatDuJeu
     * @return
     */
    @Override
    public Action faitUneAction(Plateau etatDuJeu) {
        long t = System.currentTimeMillis();
        Action a;
        System.out.println("Timer task started at:" + new Date());
        if (tourDepart == 0) {
            POSITION_DEPART = this.donnePosition();
            calculeNumeroJoueur(this.donneCouleur());
            tourDepart++;
        }
        // thread de la mort cloque tout les autres joueurs priority high
        Point currentposition = this.donnePosition();
        //calcule le numero du joueur

        if (this.donneSolde() < 60) {
            a = chercherPognon(etatDuJeu, currentposition);
        } else {               //sil il posse moins de deux sites alors il  cherche
            a = chercherTresor(etatDuJeu, currentposition);
        }
        long t1 = System.currentTimeMillis();
        System.out.println("temps :" + (t1 - t));
        return a;
    }

    /**
     * Description fonction ici
     *
     * @param s
     */
    private void calculeNumeroJoueur(String s) {
        if (s.equalsIgnoreCase("Bleu")) NUMERO_JOUEUR = 1;
        if (s.equalsIgnoreCase("Vert")) NUMERO_JOUEUR = 2;
        if (s.equalsIgnoreCase("Rouge")) NUMERO_JOUEUR = 3;
        if (s.equalsIgnoreCase("Jaune")) NUMERO_JOUEUR = 4;
    }

    /**
     * Description fonction ici
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

    /**
     * Description fonction ici
     *
     * @param plateau
     * @param p
     * @return ça retourne si le Point p sur le Plateau est bien le point du depart d'adversaire ou pas
     */
    public boolean estUnPointDepartAdverse(Plateau plateau, Point p) {
        int contenu = plateau.donneContenuCellule(p);
        int numProprietairePointDepart = Plateau.donneProprietaireDuPointDeDepart(contenu);
        int numMonJoueur = this.donneCouleurNumerique() + 1;
        return (numProprietairePointDepart > 0 && numProprietairePointDepart != numMonJoueur);
    }

    /**
     * Description fonction ici
     *
     * @param plateau
     * @param p
     * @return Ca vérifie si le site surlequel on est positionné appartient ou pas à un adversaire
     */
    public boolean estUnSiteAdverse(Plateau plateau, Point p) {
        return (!estUnSiteAbandonne(plateau, p) && Plateau.contientUnSiteQuiNeLuiAppartientPas(this, plateau.donneContenuCellule(p)));
    }

    /**
     * Description fonction ici
     *
     * @param plateau
     * @param p
     * @return ça fait le tri des sites moins importants parmi tous les sites
     */
    public boolean estUnSiteMoinsImportant(Plateau plateau, Point p) {
        int contenu = plateau.donneContenuCellule(p);
        if (!Plateau.contientUnSite(contenu))
            return false;
        int typeSite = Plateau.donneTypeSites(contenu);
        return typeSite == 1;
    }

    /**
     * Description fonction ici
     *
     * @param plateau
     * @param p       le point p
     * @return Ca vérifie si le site est abandoné ou pas
     */
    private boolean estUnSiteAbandonne(Plateau plateau, Point p) {
        int contenu = plateau.donneContenuCellule(p);
        return Plateau.contientUnSite(contenu) && ((contenu & Plateau.MASQUE_ENDROIT_SITE1) == Plateau.ENDROIT_SITE1_ABANDONNE || (contenu & Plateau.MASQUE_ENDROIT_SITE2) == Plateau.ENDROIT_SITE2_ABANDONNE);
    }

    /**
     * Description fonction ici
     *
     * @param plateau
     * @param p
     * @return Vérifier si le point p sur le plateau est un centre de financement
     */
    public boolean estUnCentreDeFinance(Plateau plateau, Point p) {
        return Plateau.contientUnPointDeFinancement(plateau.donneContenuCellule(p));
    }

    /**
     * Description fonction ici
     *
     * @param plateau
     * @param p
     * @return vérifier l'existence d'un adversaire sur le plateau au point p
     */
    public boolean existePresenceAdverse(Plateau plateau, Point p) {
        Joueur joueur = plateau.donneJoueurEnPosition(p);
        return (joueur != null && this.equals(joueur));
    }

    /**
     * Description fonction ici
     *
     * @param joueur
     * @param n
     * @return
     */
    public boolean aPlusDeNMilliers(Joueur joueur, int n) {
        return joueur.donneSolde() > n;
    }
}