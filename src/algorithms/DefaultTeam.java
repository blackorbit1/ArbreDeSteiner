package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;


public class DefaultTeam {
	private class Arete {
		public Point p1;
		public Point p2;
		public Double longueur;
		
		public Arete(Point p1, Point p2) {
			this.p1 = p1;
			this.p2 = p2;
			this.longueur = p1.distance(p2);
		}
		
		public Arete(Point p1, Point p2, Double longueur) {
			this.p1 = p1;
			this.p2 = p2;
			this.longueur = longueur;
		}
		
		@Override
		public String toString() {
			return "point 1 : " + p1 + "point 2 : " + p2 + "longueur : " + longueur;
		}
	}
	
	public class Pair<F, S> {
		    public F first;
		    public S second;

		    public Pair(F first, S second) {
		        this.first = first;
		        this.second = second;
		    }
	}
	
	private boolean pointsEquals(Point p1, Point p2) {
		if(p1.x == p2.x && p1.y == p2.y) return true;
		return false;
	}
	
	public LinkedList<Arete> getAretesS(ArrayList<Point> points_g, ArrayList<Point> points_s, int[][] matrice_directions) {
		
		HashMap<Point,ArrayList<Point>> aretes_traitees = new HashMap<>();
		for(Point point : points_s) aretes_traitees.put(point, new ArrayList<>());
		LinkedList<Arete> aretes_s = new LinkedList<>();
		
		
		for(Point point : points_s) {
			for(Point voisin : points_s) {
				if(voisin != point && !aretes_traitees.get(voisin).contains(point)) {
					Arete arete = new Arete(point, voisin, (double) 0);
					Point current_point = point;
					while(!pointsEquals(current_point, voisin)) {
						Point temp = points_g.get(matrice_directions[points_g.indexOf(current_point)][points_g.indexOf(voisin)]);
						arete.longueur += current_point.distance(temp);
						current_point = temp;
					}
					aretes_s.add(arete);
					aretes_traitees.get(point).add(voisin);
				}
			}
		}		
		return aretes_s;
		
	}
	
	public Pair<ArrayList<Point>, LinkedList<Arete>> applyT0toG(ArrayList<Point> points_g, LinkedList<Arete> aretes_g, ArrayList<Point> points_s, LinkedList<Arete> aretes_s, int[][] matrice_directions){
		ArrayList<Point> points_res = new ArrayList<>();
		LinkedList<Arete> aretes_res = new LinkedList<>();
		
		HashMap<Point,ArrayList<Point>> adj = new HashMap<>();
		for(Point point : points_g) adj.put(point, new ArrayList<>());
		
		System.out.println(aretes_s.size());
		int moy = 0;
		
		Collections.reverse(aretes_s);

		
		for(Arete arete : aretes_s) {
			Point current_point = arete.p1;
			
			if(!points_res.contains(arete.p2)) points_res.add(arete.p2);
			
			if((!points_s.contains(arete.p1)) || (!points_s.contains(arete.p2))) System.out.println("ENORME PROBLEME !!!!!!");
			
			while(!pointsEquals(current_point, arete.p2)) {
				Point temp = points_g.get(matrice_directions[points_g.indexOf(current_point)][points_g.indexOf(arete.p2)]);
				if(!(adj.get(current_point).contains(temp) || adj.get(temp).contains(current_point))) {
					adj.get(current_point).add(temp);
				}
				current_point = temp;
				
				System.out.print(" -> " + temp);
				moy++;
				
				if(!points_res.contains(current_point)) points_res.add(current_point);
			}
			System.out.println("");
		}
		
		for(Point p1 : adj.keySet()) {
			for(Point p2 : adj.get(p1)) {
				System.out.print("[" + p1.x + " " + p1.y + " - " + p2.x + " " + p2.y + "]");
				aretes_res.add(new Arete(p1, p2));
			}
			System.out.println("");
		}
		
		
		return new Pair<ArrayList<Point>, LinkedList<Arete>>(points_res, aretes_res);
	}
	
	/**
	 *  permet d'obtenir une matrice donnant pour deux points d'indice i et j l'indice k
	 *  du prochain sommet dans un plus court chemin de i à j
	 * 
	 * @param points
	 * @param edgeThreshold
	 * @return une matrice à deux dimensions
	 */
	public int[][] calculShortestPaths(ArrayList<Point> points, int edgeThreshold) {
		System.out.println("calculShortestPaths()");
		int[][] paths=new int[points.size()][points.size()];
		for (int i=0;i<paths.length;i++) for (int j=0;j<paths.length;j++) paths[i][j]=i;

		double[][] dist=new double[points.size()][points.size()];

		for (int i=0;i<paths.length;i++) {
			for (int j=0;j<paths.length;j++) {
				if (i==j) {dist[i][i]=0; continue;}
				if (points.get(i).distance(points.get(j))<=edgeThreshold) dist[i][j]=points.get(i).distance(points.get(j)); else dist[i][j]=Double.POSITIVE_INFINITY;
				paths[i][j]=j;
			}
		}

		for (int k=0;k<paths.length;k++) {
			for (int i=0;i<paths.length;i++) {
				for (int j=0;j<paths.length;j++) {
					if (dist[i][j]>dist[i][k] + dist[k][j]){
						dist[i][j]=dist[i][k] + dist[k][j];
						paths[i][j]=paths[i][k];

					}
				}
			}
		}

		return paths; // test 
	}
	
	
	
	
	
	public LinkedList<Arete> getAretes(ArrayList<Point> points, int edgeThreshold){
		LinkedList<Arete> liste_aretes = new LinkedList<>();
		for(Point p1 : points) {
			for(Point p2 : points) {
				if(p1 != p2 && p1.distance(p2) <= edgeThreshold) {
					liste_aretes.add(new Arete(p1, p2));
				}
			}
		}
		return liste_aretes;
	}
	
	
	public Tree2D kruskal(ArrayList<Point> points_global, LinkedList<Arete> liste_aretes) {		
		HashMap<Point, Integer> subgraphs = new HashMap<>();
		Integer subgraph_count = 0;
		
		HashMap<Point, ArrayList<Point>> voisins = new HashMap<>();
		ArrayList<Point> points = new ArrayList<>();
		points.addAll(points_global);
		Point racine = points.get(0);
		
		liste_aretes.sort((o1, o2) -> o1.longueur.compareTo(o2.longueur));
		
		while (points.size() > 0) {
			Arete arete = liste_aretes.pop();
			if(
					!subgraphs.containsKey(arete.p1)
					||
					!subgraphs.containsKey(arete.p2)
					||
					(subgraphs.containsKey(arete.p1) && subgraphs.containsKey(arete.p2) && (subgraphs.get(arete.p1) != subgraphs.get(arete.p2)))
				) {
				if(!subgraphs.containsKey(arete.p1) && !subgraphs.containsKey(arete.p2)) {
					subgraphs.put(arete.p1, subgraph_count);
					subgraphs.put(arete.p2, subgraph_count);
					subgraph_count++;
				} else if(!subgraphs.containsKey(arete.p1)) {
					subgraphs.put(arete.p1, subgraphs.get(arete.p2));
				} else if(!subgraphs.containsKey(arete.p2)) {
					subgraphs.put(arete.p2, subgraphs.get(arete.p1));
				} else {
					subgraphs.put(arete.p1, subgraphs.get(arete.p2));
					HashMap<Point, ArrayList<Point>> voisins_tampon = new HashMap<>();
					voisins_tampon.putAll(voisins);
					subgraphPropagation(voisins_tampon, subgraphs, arete.p1, subgraphs.get(arete.p2));
				} 
				
				if(!voisins.containsKey(arete.p1)) voisins.put(arete.p1, new ArrayList());
				if(!voisins.containsKey(arete.p2)) voisins.put(arete.p2, new ArrayList());
				voisins.get(arete.p2).add(arete.p1);
				voisins.get(arete.p1).add(arete.p2);

				if(points.contains(arete.p1)) points.remove(arete.p1);
				if(points.contains(arete.p2)) points.remove(arete.p2);
				
			}
		}
		
		return getFils(voisins, racine);
	}
	
	private void subgraphPropagation(HashMap<Point, ArrayList<Point>> voisins, HashMap<Point, Integer> subgraphs, Point point, Integer color) {
		ArrayList<Point> voisins_temp = new ArrayList<>();
		if(voisins.containsKey(point)) {
			voisins_temp.addAll(voisins.get(point));
			voisins.remove(point);
		} 
		for(Point voisin : voisins_temp) {
			subgraphs.put(voisin, color);
			subgraphPropagation(voisins, subgraphs, voisin, color);
		}
	}
	
	
	
	private Tree2D getFils(HashMap<Point, ArrayList<Point>> voisins, Point point) {
		ArrayList<Point> voisins_temp = new ArrayList<>();
		if(voisins.containsKey(point)) {
			voisins_temp.addAll(voisins.get(point));
			voisins.remove(point);
		} 
		
		ArrayList<Tree2D> fils = new ArrayList<>();
		for(Point voisin : voisins_temp) {
			fils.add(getFils(voisins, voisin));
		}
		
		return new Tree2D(point, fils);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public LinkedList<Arete> getAretesFromTree(Tree2D tree){
		LinkedList<Arete> res = new LinkedList<>();
		for(Tree2D subTree : tree.getSubTrees()) {
			res.add(new Arete(tree.getRoot(), subTree.getRoot()));
			res.addAll(getAretesFromTree(subTree));
		}
		//System.out.println(res);
		return res;
	}
	
	
	
	
	
	
	public Tree2D calculSteiner(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		int[][] matrice_directions = calculShortestPaths(points, edgeThreshold);
		
		// Construction de l'arbre K
		LinkedList<Arete> aretesS = getAretesS(points, hitPoints, matrice_directions);

		// Kruskal sur K
		Tree2D res = kruskal(hitPoints, aretesS);
		
		// appliquer K à G -> H
		Pair paire = applyT0toG(points, getAretes(points, edgeThreshold), hitPoints, getAretesFromTree(res), matrice_directions);
		ArrayList<Point> points_H = (ArrayList<Point>) paire.first;
		LinkedList<Arete> aretes_H = (LinkedList<Arete>) paire.second;
		
		
		// Kruskal sur H
		res = kruskal(points_H, aretes_H);
		
		//System.out.println(res);
		System.out.println("aretes_res : " + getAretesFromTree(res).size());
		System.out.println("points_H : " + points_H.size());
		System.out.println("aretes_H : " + aretes_H.size());
		
		ArrayList<Point> points_experience = new ArrayList<>();
		LinkedList<Arete> aretes_experience = getAretesFromTree(res);
		for(Arete arete : aretes_experience) {
			if(!points_experience.contains(arete.p1)) points_experience.add(arete.p1);
			if(!points_experience.contains(arete.p2)) points_experience.add(arete.p2);
		}
		
		res = kruskal(points_experience, aretes_experience);
		
		System.out.println("aretes_res : " + getAretesFromTree(res).size());

		
		return res;
	}
	
	public Tree2D calculSteinerBudget(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		//REMOVE >>>>>
		Tree2D leafX = new Tree2D(new Point(700,400),new ArrayList<Tree2D>());
		Tree2D leafY = new Tree2D(new Point(700,500),new ArrayList<Tree2D>());
		Tree2D leafZ = new Tree2D(new Point(800,450),new ArrayList<Tree2D>());
		ArrayList<Tree2D> subTrees = new ArrayList<Tree2D>();
		subTrees.add(leafX);
		subTrees.add(leafY);
		subTrees.add(leafZ);
		Tree2D steinerTree = new Tree2D(new Point(750,450),subTrees);
		//<<<<< REMOVE

		return steinerTree;
	}
}
