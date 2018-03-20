/*
 * Code minimal pour crÃ©er son propre joueur des Arenes.
 */
package arenesolo;

import jeu.Joueur;
import jeu.Plateau;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import jeu.astar.Node;

import static jeu.Plateau.MASQUE_ENDROIT_SITE2;

public class MonJoueur extends jeu.Joueur {

    /**
     * decrit le nom du joueur
     *
     * @param nom
     */
    public MonJoueur(String nom) {
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
     * Parcours en spirale
     * le parcours en spirale pour decrire autour d'un point fixe (Point p) les objectives les plus proches.
     *
     * @param x0          position X initiale
     * @param y0          position Y initiale
     * @param distanceMax distance a parcourir
     */
    private static Point DonnePointObjectifPlusProche(Plateau etatdujeu, Point p, int distanceMax, Integer objectif) {
        //Recherche du temple le plus proche
        int x0 = p.x;
        int y0 = p.y;
        System.out.println("current position = " + String.valueOf(x0) + " " + String.valueOf(y0));
        // directions possibles: G=(-1,0) H=(0,-1) D=(1,0) B=(0,1)
        int[] dx = new int[]{-1, 0, 1, 0};
        int[] dy = new int[]{0, -1, 0, 1};
        int dirIndex = 0;
        // distance parcourue
        int distance = 0;
        // nombre de pas a faire
        int stepToDo = 1;
        // position courante
        int x = x0, y = y0;
        //System.out.println("Initial position: " +x+","+y);
        while (true) {
            // a faire 2 fois avec le meme nombre de pas (gauche+haut) ou (droite+bas)
            for (int i = 0; i < 2; i++) {
                // déplacement du nombre de pas
                for (int j = 0; j < stepToDo; j++) {
                    // condition de sortie
                    distance++;
                    if (distance > distanceMax) return null;
                    // déplacement
                    x += dx[dirIndex];
                    y += dy[dirIndex];
                    //System.out.println("Current position: " +x+","+y+" distance="+distance+" contenu = "+etatdujeu.donneContenuCellule(x,y));
                    int contenu = etatdujeu.donneContenuCellule(x, y);
                    if ((contenu & MASQUE_ENDROIT_SITE2) != 0) { // si la case scannee vaut la valeur objectif passée en parametre
                        /*
                        if(Plateau.donneProprietaireDuSite(contenu)!=Plateau.PRESENCE_JOUEUR1) {
                            System.out.println("Valeur du proprietraire "+String.valueOf(Plateau.donneProprietaireDuSite(contenu)));
                            return new Point(x, y);
                        }*/
                        if (Plateau.donneProprietaireDuSite(contenu) != 1) {
                            System.out.println("Valeur du proprietraire " + String.valueOf(Plateau.donneProprietaireDuSite(contenu)));
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
     * Description fonction ici
     *
     * @param etatDuJeu   l'etat du jeu en cours
     * @param destination la destination du joueur selectionné
     * @param depart      la position du départ
     * @return
     */
    private Action prochaineDirectionVers(Plateau etatDuJeu, Point destination, Point depart) {
        ArrayList<Node> chemin = etatDuJeu.donneCheminEntre(destination, depart);
        Node nextpos;
        if (chemin.size() > 1)
            nextpos = chemin.get(chemin.size() - 2); // 6,6
        else nextpos = new Node(destination.x, destination.y);
        System.out.println(" next position = " + nextpos.toString());
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
    // action

    /**
     * Description fonction ici
     *
     * @param etatDuJeu on a en parametre l'état du jeu
     * @return ça retourne l'action en cours RIEN, GAUCHE, DROITE, HAUT, BAS
     */
    @Override
    public Action faitUneAction(Plateau etatDuJeu) {
        //mappage(etatDuJeu);
        Point currentposition = this.donnePosition();
        HashMap<Integer, ArrayList<Point>> positionJoueur = etatDuJeu.cherche(currentposition, 20, Plateau.CHERCHE_JOUEUR);
        HashMap<Integer, ArrayList<Point>> positionSiteFouille = etatDuJeu.cherche(currentposition, 20, Plateau.CHERCHE_SITE);
        HashMap<Integer, ArrayList<Point>> positionSiteFinance = etatDuJeu.cherche(currentposition, 20, Plateau.CHERCHE_FINANCE);
        //System.out.println(etatDuJeu.cherche(currentposition,20,Plateau.CHERCHE_JOUEUR));
        Set<Integer> integer = positionJoueur.keySet();
        for (Integer i : integer) {
            if (positionJoueur.get(i) != null)
                System.out.println("Les joueurs :  " + i + " is: " + positionJoueur.get(i));
        }

        Set<Integer> intege = positionSiteFouille.keySet();
        for (Integer i : intege) {
            if (positionSiteFouille.get(i) != null)
                System.out.println("Site de fouille : " + i + " is: " + positionSiteFouille.get(i));
        }

        Set<Integer> integ = positionSiteFinance.keySet();
        for (Integer i : integ) {
            if (positionSiteFinance.get(i) != null)
                System.out.println("Site finance :" + i + " is: " + positionSiteFinance.get(i));
        }
        Point destination = DonnePointObjectifPlusProche(etatDuJeu, currentposition, 200, 65536); // se diriger vers un temple = 65536
        assert destination != null;
        System.out.println(" destination = " + destination.toString());


        System.out.println(Plateau.donneProprietaireDuSite(etatDuJeu.donneContenuCellule(destination)));
        return prochaineDirectionVers(etatDuJeu, destination, currentposition);
    }
    //commentaries

    /**
     * Description fonction ici
     *
     * @param lePlateau Le Plateau est donnée en chaine de caractère en paramétre
     *                  la findepartie c'est quand le nombre maximum de tours est réalisé.
     *                  // Exception: Si deux joueurs ont le même nombre de points de notoriété, il n'y a pas de gagnant.
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
     * @return ca retourne un boolean pour savoir si le site est important ou pas
     */
    public boolean estUnSiteImportant(Plateau plateau, Point p) {
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