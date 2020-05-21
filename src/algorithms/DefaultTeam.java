package algorithms;

import java.awt.Point;
import java.util.ArrayList;
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
	}
	
	public ArrayList<Arete> getAretesS(ArrayList<Point> points_g, ArrayList<Point> points_s, int edgeThreshold) {
		int[][] matrice_directions = calculShortestPaths(points_g, edgeThreshold);
		
		HashMap<Point,ArrayList<Point>> aretes_traitees = new HashMap<>();
		for(Point point : points_s) aretes_traitees.put(point, new ArrayList<>());
		ArrayList<Arete> aretes_s = new ArrayList<>();
		
		for(Point point : points_s) {
			for(Point voisin : points_s) {
				if(voisin != point
				&& voisin.distance(point) <= edgeThreshold 
				&& !aretes_traitees.get(voisin).contains(point)) {
					Arete arete = new Arete(point, voisin, (double) 0);
					Point current_point = point;
					while(true) {
						if(current_point == voisin) break;
						Point temp = points_g.get(matrice_directions[points_g.indexOf(current_point)][points_g.indexOf(voisin)]);
						arete.longueur += current_point.distance(temp);
						current_point = temp;
					}
					aretes_traitees.get(point).add(voisin);
				}
			}
		}		
		return aretes_s;
		
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
	
	
	
	
	
	
	
	
	public Tree2D kruskal(ArrayList<Point> points_global) {
		int i = 0;
		
		HashMap<Point, Integer> subgraphs = new HashMap<>();
		Integer subgraph_count = 0;
		
		LinkedList<Arete> liste_aretes = new LinkedList<>();
		HashMap<Point, ArrayList<Point>> voisins = new HashMap<>();

		
		
		ArrayList<Point> points = new ArrayList<>();
		points.addAll(points_global);
		
		
		Point racine = points.get(0);
		
		for(Point p1 : points) {
			for(Point p2 : points) {
				if(p1 != p2) {
					liste_aretes.add(new Arete(p1, p2));
				}
			}
		}
		
		
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public Tree2D calculSteiner(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		//REMOVE >>>>>
		/*
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
		*/
		
		return kruskal(points);
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
