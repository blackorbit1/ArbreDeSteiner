package algorithms;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;



public class DefaultTeam {
	private static final int BUDGET = 1664;
	
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
		    
		    public boolean equals(Pair<F, S> paire2) {
		    	return (paire2.first == this.first) && (paire2.second == this.second);
		    }
	}
	
	private boolean pointsEquals(Point p1, Point p2) {
		if(p1.x == p2.x && p1.y == p2.y) return true;
		return false;
	}

	public LinkedList<Arete> getAretesS(ArrayList<Point> points_g, ArrayList<Point> points_s, int[][] matrice_directions) {
		// initialisation d'une structure de données pour savoir quelles aretes on a déjà traité (sinon elles seront toutes en double)
		HashMap<Point, ArrayList<Point>> deja_vu = new HashMap<>();
		for(Point point : points_s) {
			deja_vu.put(point, new ArrayList<>());
			deja_vu.get(point).add(point);
		}

		// résultat
		LinkedList<Arete> aretes = new LinkedList<>();
		
		for(Point p1 : points_s) {
			for(Point p2 : points_s) {
				if(!deja_vu.get(p2).contains(p1)) {
					Point current_point = p1;
					Arete arete = new Arete(p1, p2, (double) 0);
					
					// On parcours tout le chemin d'un bout à l'autre de l'arete pour connaitre la longueur de celle-ci
					while(!pointsEquals(current_point, p2)) {
						Point temp = points_g.get(matrice_directions[points_g.indexOf(current_point)][points_g.indexOf(p2)]);
						arete.longueur = arete.longueur + current_point.distance(temp);
						current_point = temp;
					}
					aretes.add(arete);
					
					// On mémorise cette arete
					deja_vu.get(p1).add(p2);
				}
			}
		}
		
		return aretes;
	}
	
	
	public Pair<ArrayList<Point>, LinkedList<Arete>> applyT0toG(ArrayList<Point> points_g, LinkedList<Arete> aretes_g, ArrayList<Point> points_s, LinkedList<Arete> aretes_s, int[][] matrice_directions){
		ArrayList<Point> points_res = new ArrayList<>();
		LinkedList<Arete> aretes_res = new LinkedList<>();
		
		HashMap<Point,ArrayList<Point>> adj = new HashMap<>();
		for(Point point : points_g) adj.put(point, new ArrayList<>());
		
		Collections.reverse(aretes_s);

		
		for(Arete arete : aretes_s) {
			Point current_point = arete.p1;
			if(!points_res.contains(arete.p2)) points_res.add(arete.p2);
			
			while(!pointsEquals(current_point, arete.p2)) {
				Point temp = points_g.get(matrice_directions[points_g.indexOf(current_point)][points_g.indexOf(arete.p2)]);
				if(!(adj.get(current_point).contains(temp) || adj.get(temp).contains(current_point))) {
					adj.get(current_point).add(temp);
				}
				current_point = temp;

				if(!points_res.contains(current_point)) points_res.add(current_point);
			}
		}
		
		for(Point p1 : adj.keySet()) {
			for(Point p2 : adj.get(p1)) {
				aretes_res.add(new Arete(p1, p2));
			}
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

		return paths; 
	}
	
	
	
	

	private Tree2D edgesToTreeAux(LinkedList<Arete> aretes_K, Point root) {
		LinkedList<Arete> remainder = new LinkedList<Arete>();
		ArrayList<Point> subTreeRoots = new ArrayList<Point>();
		Arete current;
		while (aretes_K.size()!=0) {
			current = aretes_K.remove(0);
			if (current.p1.equals(root)) {
				subTreeRoots.add(current.p2);
			} else {
				if (current.p2.equals(root)) {
					subTreeRoots.add(current.p1);
				} else {
					remainder.add(current);
				}
			}
		}

		ArrayList<Tree2D> subTrees = new ArrayList<Tree2D>();
		for (Point subTreeRoot: subTreeRoots) subTrees.add(edgesToTree((LinkedList<Arete>)remainder.clone(),subTreeRoot));

		return new Tree2D(root, subTrees);
	}
	
	private Tree2D edgesToTree(LinkedList<Arete> aretes_K, Point root) {
		LinkedList<Arete> aretes = new LinkedList<Arete>();
		aretes.addAll(aretes_K);
		return edgesToTreeAux(aretes, root);
	}
	
	
	
	public ArrayList<Point> getPointsFromEdges(LinkedList<Arete> aretes){
		ArrayList<Point> points = new ArrayList<>();
		for(Arete arete : aretes) {
			if(!points.contains(arete.p1)) points.add(arete.p1);
			if(!points.contains(arete.p2)) points.add(arete.p2);
		}
		
		return points;
	}
	
	
	public LinkedList<Arete> getAretesFromPoints(ArrayList<Point> points, int edgeThreshold){
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
	
	
	public LinkedList<Arete> getAretesFromTree(Tree2D tree){
		LinkedList<Arete> res = new LinkedList<>();
		for(Tree2D subTree : tree.getSubTrees()) {
			res.add(new Arete(tree.getRoot(), subTree.getRoot()));
			res.addAll(getAretesFromTree(subTree));
		}
		return res;
	}
	
	
	private LinkedList<Arete> sort(LinkedList<Arete> edges) {
		if (edges.size()==1) return edges;

		LinkedList<Arete> left = new LinkedList<Arete>();
		LinkedList<Arete> right = new LinkedList<Arete>();
		int n=edges.size();
		for (int i=0;i<n/2;i++) { left.add(edges.remove(0)); }
		while (edges.size()!=0) { right.add(edges.remove(0)); }
		left = sort(left);
		right = sort(right);

		LinkedList<Arete> result = new LinkedList<Arete>();
		while (left.size()!=0 || right.size()!=0) {
			if (left.size()==0) { result.add(right.remove(0)); continue; }
			if (right.size()==0) { result.add(left.remove(0)); continue; }
			if (left.get(0).longueur < right.get(0).longueur) result.add(left.remove(0));
			else result.add(right.remove(0));
		}
		return result;
	}
	
	
	class NameTag {
		private ArrayList<Point> points;
		private int[] tag;
		protected NameTag(ArrayList<Point> points){
			this.points=(ArrayList<Point>)points.clone();
			tag=new int[points.size()];
			for (int i=0;i<points.size();i++) tag[i]=i;
		}
		protected void reTag(int j, int k){
			for (int i=0;i<tag.length;i++) if (tag[i]==j) tag[i]=k;
		}
		protected int tag(Point p){
			for (int i=0;i<points.size();i++) if (p.equals(points.get(i))) return tag[i];
			return 0xBADC0DE;
		}
	}

	
	
	public LinkedList<Arete> kruskal(ArrayList<Point> points, LinkedList<Arete> liste_aretes) {		
		LinkedList<Arete> edges = new LinkedList<Arete>();
		edges.addAll(liste_aretes);
		edges = sort(edges);

		LinkedList<Arete> resultat = new LinkedList<Arete>();
		Arete current;
		NameTag forest = new NameTag(points);
		while (edges.size()!=0) {
			current = edges.remove(0);
			if (forest.tag(current.p1)!=forest.tag(current.p2)) {
				resultat.add(current);
				forest.reTag(forest.tag(current.p1),forest.tag(current.p2));
			}
		}

		return resultat;
	}
	
	
	
	
	public LinkedList<Link> getLinks(Tree2D arbre, LinkedList<Arete> link_road, ArrayList<Point> hit_points){
		LinkedList<Link> result = new LinkedList<>();
		
		//System.out.println(link_road);
		//System.out.println(hit_points);
		
		// Si on se trouve sur un hitpoint, ça veut dire qu'on a fait l'entiereté d'un lien entre 2 hitpoint
		if(hit_points.contains(arbre.getRoot()) && link_road.size() > 0) {
			Link link = new Link(link_road);
			result.add(link);
			link_road = new LinkedList<>();
		}
		
		// Fait doublon avec le if d'avant
		/*
		for(Tree2D fils : arbre.getSubTrees()) {
			if(fils.getSubTrees().size() == 0) {
				LinkedList<Arete> aretes_link = new LinkedList<>();
				aretes_link.addAll(link_road);
				aretes_link.add(new Arete(arbre.getRoot(), fils.getRoot()));
				Link link = new Link(aretes_link);
				result.add(link);
			}
		}*/
		
		for(Tree2D fils : arbre.getSubTrees()) {
			LinkedList<Arete> link_road_temp;
			// S'il y a plus de 1 fils, on fait un crée un nouveau  link_road pour pas qu'il y a de conflit
			// dans la suite de la reccursion
			if(arbre.getSubTrees().size() > 1) {
				link_road_temp = new LinkedList<>();
				link_road_temp.addAll(link_road);
			} else {
				link_road_temp = link_road;
			}
			link_road_temp.add(new Arete(arbre.getRoot(), fils.getRoot()));
			result.addAll(getLinks(fils, link_road_temp, hit_points));
		}
		
		return result;
	}
	
	
	/*
	public LinkedList<Arete> getLinks(LinkedList<Arete> aretes, ArrayList<Point> hit_points){
		HashMap<Point,ArrayList<Point>> adj = new HashMap<>();
	}
	*/
	

	
	
	
	public Tree2D calculSteiner(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		int[][] matrice_directions = calculShortestPaths(points, edgeThreshold);
		
		// Construction de l'arbre K
		LinkedList<Arete> aretesS = getAretesS(points, hitPoints, matrice_directions);

		// Kruskal sur K
		LinkedList<Arete> aretes_K = kruskal(hitPoints, aretesS);
		
		// appliquer K à G -> H
		Pair<ArrayList<Point>, LinkedList<Arete>> paire = applyT0toG(points, getAretesFromPoints(points, edgeThreshold), hitPoints, aretes_K, matrice_directions);
		ArrayList<Point> points_H = (ArrayList<Point>) paire.first;
		LinkedList<Arete> aretes_H = (LinkedList<Arete>) paire.second;
		
		// Kruskal sur H
		LinkedList<Arete> aretes_finales = kruskal(points_H, aretes_H);
		

		return edgesToTree(aretes_finales, aretes_finales.get(0).p1);
	}
	
	private class Link {
		LinkedList<Arete> aretes = new LinkedList<>();
		public Point p1;
		public Point p2;
		public Double longueur;
		
		public Link(LinkedList<Arete> aretes) {
			//System.out.println("  >>>>    " + aretes);
			this.aretes = aretes;
			
			this.longueur = (double) 0;
			for(Arete arete : aretes) {
				longueur += arete.longueur;
			}
			
	
			HashMap<Point, Integer> occurence_points = new HashMap<>();
			for(Arete arete : aretes) {
				if(!occurence_points.containsKey(arete.p1)) occurence_points.put(arete.p1, 0);
				occurence_points.put(arete.p1, occurence_points.get(arete.p1) + 1);
				
				if(!occurence_points.containsKey(arete.p2)) occurence_points.put(arete.p2, 0);
				occurence_points.put(arete.p2, occurence_points.get(arete.p2) + 1);
			}
			ArrayList<Point> extremites = new ArrayList<>();
			for(Point point : occurence_points.keySet()) {
				//System.out.println(point + "      " + occurence_points.get(point));
				if(occurence_points.get(point) == 1) extremites.add(point);
			}
			
			if(extremites.size() > 2) {
				System.out.println("ENORME PROBLEME ! !!");
				System.out.println(extremites);
			}
			
			
			this.p1 = extremites.get(0);
			this.p2 = extremites.get(1);
		}
		
		@Override
		public String toString() {
			return "p1 : " + p1 + "     p2 : " + p2 + "     longueur : " + longueur + "\naretes" + aretes;
		}
		
	}
	
	public Tree2D calculSteinerBudget(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		// Execution des différentes étapes de steiner comme plus haut
		int[][] matrice_directions = calculShortestPaths(points, edgeThreshold);
		
		LinkedList<Arete> aretes_S = getAretesS(points, hitPoints, matrice_directions);
		LinkedList<Arete> aretes_K = kruskal(hitPoints, aretes_S);
		
		Pair<ArrayList<Point>, LinkedList<Arete>> paire = applyT0toG(points, getAretesFromPoints(points, edgeThreshold), hitPoints, aretes_K, matrice_directions);
		LinkedList<Arete> aretes_sans_budget = kruskal(paire.first, paire.second);
		
		float cout = 0;
		
		LinkedList<Link> links = getLinks(edgesToTree(aretes_sans_budget, hitPoints.get(0)), new LinkedList<Arete>(), hitPoints);
		/*
		for(Link link : links) {
			System.out.println(link.longueur);
		}
		
		try {
			Thread.sleep(5000000000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		LinkedList<Arete> aretes_finales = new LinkedList<>();
		
		for(Link link : links) {
			aretes_finales.addAll(link.aretes);
			cout += link.longueur;
		}
		
		
		
		
		
		// tant qu'on est au dessus du budget
		while(cout > BUDGET) {
			System.out.println("COUT : " + cout);
			HashMap<Point, Integer> occurence_points = new HashMap<>();
			for(Link link : links) {
				if(!occurence_points.containsKey(link.p1)) occurence_points.put(link.p1, 0);
				occurence_points.put(link.p1, occurence_points.get(link.p1) + 1);
				
				if(!occurence_points.containsKey(link.p2)) occurence_points.put(link.p2, 0);
				occurence_points.put(link.p2, occurence_points.get(link.p2) + 1);
			}
			
			LinkedList<Link> links_candidates = new LinkedList<>();
			for(Link link : links) {
				if(occurence_points.get(link.p1) == 1) links_candidates.add(link);
				if(occurence_points.get(link.p2) == 1) links_candidates.add(link);
			}
			
			links_candidates.sort((o1, o2) -> o1.longueur.compareTo(o2.longueur));
			//aretes_candidates = sort(links_candidates);
			
			Link link_to_remove = links_candidates.getLast();
			for(Arete arete : link_to_remove.aretes) {
				System.out.println(arete.longueur);
				aretes_finales.remove(arete);
				cout -= arete.longueur;
			}
			links.remove(link_to_remove);
			/*
			 * 
			 * FAIRE un FOR ET TOUT
			 */
			//aretes_finales.remove(arete_to_remove);
			
			//cout -= arete_to_remove.longueur;
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		return edgesToTree(aretes_finales, aretes_finales.get(0).p1);
	}
}
