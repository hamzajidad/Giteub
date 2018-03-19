/*
 * Programme minimal pour lancer une partie en solo avec son propre joueur.
 */
package arenesolo;

import jeu.Plateau;
import jeu.MaitreDuJeu;
import gui.FenetreDeJeu;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import jeu.Joueur;
import jeu.astar.Node;

public class AreneSolo {

    public static void main(String[] args) {
        Plateau p = new Plateau( 1200, MaitreDuJeu.PLATEAU_PAR_DEFAUT);

        MaitreDuJeu jeu = new MaitreDuJeu(p);
        jeu.metJoueurEnPosition(0, new MonJoueur("Moi"));
        /*
        jeu.metJoueurEnPosition(1, new MonJoueur("Moi2"));

        jeu.metJoueurEnPosition(2, new MonJoueur("Moi3"));

        jeu.metJoueurEnPosition(3, new MonJoueur("Moi4"));
        */
        FenetreDeJeu f = new FenetreDeJeu(jeu, true);

        f.addWindowListener( new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) { }

            @Override
            public void windowClosing(WindowEvent e) { }

            @Override
            public void windowClosed(WindowEvent e) { System.exit(0 );}

            @Override
            public void windowIconified(WindowEvent e) {  }

            @Override
            public void windowDeiconified(WindowEvent e) { }

            @Override
            public void windowActivated(WindowEvent e) { }

            @Override
            public void windowDeactivated(WindowEvent e) { }
        });

        /* Code facultatif pour rÃ©cupÃ©rer les clics souris sur le plateau
         * Pour interagir avec votre algo pendant la phase de dÃ©veloppement.
         * Par exemple : */
        f.setMouseClickListener( (int x, int y, int bt) -> {
            Joueur j = p.donneJoueur(p.donneJoueurCourant());
            ArrayList<Node> a = p.donneCheminEntre(j.donnePosition(), new Point(x,y));
            f.afficheAstarPath(a);
        });
        /* */


        EventQueue.invokeLater(() -> { f.setVisible(true); });
    }

}