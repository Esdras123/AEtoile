
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ESDRAS
 */
public class Solution {
    /*
        La classe Solution est utilisée pour tester l'algorithme sur les graphes
    */
    public static Sommet[] returnSommet(ArrayList<Sommet> listeSommets, String lettre1, String lettre2) {
        Sommet[] sommet = new Sommet[2];
        sommet[0] = null;
        sommet[1] = null;
        for (Sommet somm : listeSommets) {
            if (somm.getNom().equals(lettre1)) {
                sommet[0] = somm;
            }
            if (somm.getNom().equals(lettre2)) {
                sommet[1] = somm;
            }
        }
        return sommet;
    }

    public static void main(String[] args) {
        //initialisation des listes

        ArrayList<Sommet> listeSommets = new ArrayList<Sommet>();
        ArrayList<Arc> listeArcs = new ArrayList<Arc>();
        // on récupère les sommets
        System.out.println("Entrez le nombre de sommets");
        Scanner sc = new Scanner(System.in);
        int nb = sc.nextInt();
        System.out.println("Entrez chaque sommet avec son heuristique comme ceci :");
        System.out.println("A 5 si le sommet est A et l'heuristique 5 (une ligne par sommet)");
        String ligneA;
        Integer ligneB;
        for (int i = 0; i < nb; i++) {
            ligneA = sc.next();
            ligneB = sc.nextInt();
            listeSommets.add(new Sommet(ligneA, ligneB));
        }
        //on récupère les arcs
        System.out.println("Maintenant entrez les arcs.\nLorsque vous avez terminé de citer les arcs, tapez -1 sur la ligne qui suit");
        System.out.println("Un arc est sous la forme 'noeudExpediteur noeudDestinataire Poids \n"
                + "donc par exemple si A est le noeud expéditeur B le noeud destinataire et 5 le poids"
                + "\nce sera sous la forme A B 5");
        String test = new String();
        Sommet[] sommets = new Sommet[2];
        do {
            test = sc.next();
            if (!test.equals("-1")) {
                ligneA = sc.next();
                ligneB = sc.nextInt();
                sommets = returnSommet(listeSommets, test, ligneA);
                listeArcs.add(new Arc(sommets[0], sommets[1], ligneB));
            }
        } while (!test.equals("-1"));

        //fin de l'initialisation des listes
        Graphe graphe = new Graphe(listeSommets, listeArcs);
        String a, b;
        System.out.println("Entrez le nom du sommet de départ");
        a = sc.next();
        System.out.println("Entrez le nom du sommet d'arrivée");
        b = sc.next();
        // on affiche les sommets et les arcs pour s'assurer qu'ils ont été bien pris
        System.out.println("-------------Sommets-------------");
        for (Sommet somm : listeSommets) {
            System.out.println(somm);
        }
        System.out.println("--------------Arcs-------------");
        for (Arc arc : listeArcs) {
            System.out.println(arc);
        }
        sommets = returnSommet(listeSommets, a, b);
        Noeud solution = graphe.rechercheAEtoile(sommets[0], sommets[1]);
        if (solution == null) {
            System.out.println("Aucune solution possible");
        } else {
            solution.afficher();
        }
    }

}

class Graphe {

    private ArrayList<Sommet> listeSommets;
    private ArrayList<Arc> listeArcs;

    public Graphe(ArrayList<Sommet> listeSommets, ArrayList<Arc> listeArcs) {
        this.listeSommets = listeSommets;
        this.listeArcs = listeArcs;
    }

    public ArrayList<Noeud> listeVoisins(Sommet sommet) {
        ArrayList<Noeud> voisins = new ArrayList<Noeud>();
        if (!listeSommets.contains(sommet)) {
            return null;
        } else {
            for (Arc arc : listeArcs) {
                if (arc.getExp().equals(sommet)) {
                    voisins.add(new Noeud(arc.getDest(), null));
                }
            }
        }
        return voisins;
    }

    public int trouverCout(Sommet depart, Sommet arrivee) {
        for (Arc arc : listeArcs) {
            if (arc.getExp().equals(depart) && arc.getDest().equals(arrivee)) {
                return arc.getPoids();
            }
        }
        return - 1;
    }

    public String afficherListe(ArrayList<Noeud> liste) {
        StringBuilder mot = new StringBuilder();
        if (liste.size() == 0) {
            mot.append("vide");
        }
        for (int nb = 0; nb < liste.size(); nb++) {
            mot.append(liste.get(nb).toString());
            if (nb != liste.size() - 1) {
                mot.append(", ");
            }
        }
        return mot.toString();
    }

    /*
        Cette méthode est l'implémentation de la recherche A*
    */
    public Noeud rechercheAEtoile(Sommet nomDepart, Sommet nomArrivee) {
        Comparator comparator = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Noeud n1 = (Noeud) o1, n2 = (Noeud) o2;
                if (n1.equals(n2)) {
                    return 0;
                }
                if (n1.getFonction() < n2.getFonction()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        };
        Noeud depart = new Noeud(nomDepart, null), noeud1, noeud2;
        ArrayList<Noeud> open = new ArrayList<Noeud>();
        ArrayList<Noeud> closed = new ArrayList<Noeud>();
        open.add(depart);
        int tour = 1;
        while (true) {
            System.out.println("-------Tour " + tour++ + "--------");
            System.out.println("Contenu de open (etat, f, parent)");
            System.out.println(afficherListe(open));
            System.out.println("Contenu de closed (etat, f, parent)");
            System.out.println(afficherListe(closed));
            if (open.size() == 0) {
                return null;
            }
            noeud1 = open.get(0);
            open.remove(0);
            closed.add(noeud1);
            if (noeud1.getNom().equals(nomArrivee)) {
                return noeud1;
            }
            for (Noeud noeud : listeVoisins(noeud1.getNom())) {
                noeud.setCoutMeilleurChemin(noeud1.getCoutMeilleurChemin() + trouverCout(noeud1.getNom(), noeud.getNom()));
                noeud.setParent(noeud1);
                boolean flag = true;
                for (Noeud n : open) {
                    if (noeud.getNom().getNom().equals(n.getNom().getNom())) {
                        flag = false;
                        if (noeud.getFonction() <= n.getFonction()) {
                            open.remove(n);
                            open.add(noeud);
                            open.sort(comparator);
                            break;
                        }
                    }
                }
                if (flag) {
                    for (Noeud n : closed) {
                        if (noeud.getNom().getNom().equals(n.getNom().getNom())) {
                            flag = false;
                            if (noeud.getFonction() <= n.getFonction()) {
                                flag = false;
                                closed.remove(n);
                                open.add(noeud);
                                open.sort(comparator);
                                break;
                            }
                        }
                    }
                }
                if (flag) {
                    open.add(noeud);
                    open.sort(comparator);
                }
            }

        }
    }
}

class Arc {
    /*
        Cette classe représente la structure Arc
    */
    private Sommet exp;
    private Sommet dest;
    private int poids;

    public Arc(Sommet exp, Sommet dest, int poids) {
        this.exp = exp;
        this.dest = dest;
        this.poids = poids;
    }

    public Sommet getExp() {
        return exp;
    }

    public void setExp(Sommet exp) {
        this.exp = exp;
    }

    public Sommet getDest() {
        return dest;
    }

    public void setDest(Sommet dest) {
        this.dest = dest;
    }

    public int getPoids() {
        return poids;
    }

    public void setPoids(int poids) {
        this.poids = poids;
    }

    @Override
    public String toString() {
        return "(" + exp.getNom() + ", " + dest.getNom() + ", " + poids + ')';
    }

}

class Noeud {

    /*
        Cette classe représente la structure Noeud
    */
    private Sommet nom;
    private Noeud parent;
    private int coutMeilleurChemin;

    public Noeud(Sommet nom, Noeud parent) {
        this.nom = nom;
        this.parent = parent;
        this.coutMeilleurChemin = 0;
    }

    public int getCoutMeilleurChemin() {
        return coutMeilleurChemin;
    }

    public void setCoutMeilleurChemin(int coutMeilleurChemin) {
        this.coutMeilleurChemin = coutMeilleurChemin;
    }

    public Sommet getNom() {
        return nom;
    }

    public void setNom(Sommet nom) {
        this.nom = nom;
    }

    public Noeud getParent() {
        return parent;
    }

    public int getFonction() {
        return coutMeilleurChemin + nom.getHeuristique();
    }

    @Override
    public String toString() {
        String name;
        if (parent == null) {
            name = "void";
        } else {
            name = parent.nom.getNom();
        }
        return "(" + nom.getNom() + ", " + getFonction() + ", " + name + ")";
    }

    public void setParent(Noeud parent) {
        this.parent = parent;
    }

    public void afficher() {
        ArrayList<String> liste = new ArrayList<String>();
        liste.add(nom.getNom());
        Noeud pere = parent;
        System.out.println("\n\n******Le plus court chemin est donc : *****");
        while (pere != null) {
            liste.add(pere.getNom().getNom());
            pere = pere.getParent();
        }
        for (int i = liste.size() - 1; i >= 0; i--) {
            System.out.print(liste.get(i));
            if (i != 0) {
                System.out.print("---->");
            }
        }
        System.out.println();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Noeud other = (Noeud) obj;
        if (!nom.equals(other.getNom())) {
            return false;
        }
        return true;
    }

}

class Sommet {

    private String nom;
    private int heuristique;

    public Sommet(String nom, int heuristique) {
        this.nom = nom;
        this.heuristique = heuristique;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getHeuristique() {
        return heuristique;
    }

    public void setHeuristique(int heuristique) {
        this.heuristique = heuristique;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sommet other = (Sommet) obj;
        if (!nom.equals(other.nom)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "(" + nom + ", " + heuristique + ')';
    }

}

