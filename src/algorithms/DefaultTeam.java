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
	
	
	private class Segment {
		public int budget;
		
		public LinkedList<Arete> aretes_temp;
		public Point last_point;
		public int dist_act = 0; // le cout de l'arbre
		public int val_act = 0; // le nb de hitPoint contenues dans l'arbre
		
		public LinkedList<Arete> aretes;
		public int dist_max = Integer.MAX_VALUE;
		public int val_max = 0;
		
		private ArrayList<Point> points_acquis;
		private ArrayList<Point> points_to_keep;
		private ArrayList<Point> hit_points;
		
		private boolean can_evolve = true;
	
		
		public Segment(int budget, Point point, ArrayList<Point> points_acquis, ArrayList<Point> points_to_keep, ArrayList<Point> hit_points) {
			this.budget = budget;
			this.last_point = point;
			this.aretes_temp = new LinkedList<>();
			this.aretes = new LinkedList<>();
			
			this.aretes = new LinkedList<>();
			this.aretes_temp = new LinkedList<>();
			
			this.points_acquis = points_acquis;
			this.points_to_keep = points_to_keep; 
			this.hit_points = hit_points;
		}
		
		public String toString() {
			return  ( "---------------------------------\n"
					+ "budget : " + budget + "\n"
					+ "\n"
					+ "aretes_temp : " + aretes_temp + "\n"
					+ "last_point :  " + last_point + "\n"
					+ "dist_act :    " + dist_act + "\n"
					+ "val_act :     " + val_act  + "\n"
					+ "\n"
					+ "aretes :      " + aretes + "\n"
					+ "dist_max :    " + dist_max + "\n"
					+ "val_max :     " + val_max  + "\n"
					+ "---------------------------------\n"
					);
		}
		
		public void addPoint(Point point, boolean is_hitpoint) {
			System.out.println("----- adresse : " + super.toString());
			System.out.println("----- dist_act : " + dist_act);
			System.out.println("----- last_point.distance(point) : " + last_point.distance(point));
			System.out.println("----- points_acquis.contains(last_point) : " + points_acquis.contains(last_point) + "           last_point : " + last_point);
			System.out.println("----- points_acquis.contains(point) : " + points_acquis.contains(point) + "           point : " + point);
			System.out.println("----- is_hitpoint : " + is_hitpoint);
			System.out.println("----- has_common_elements(aretes_temp, points_acquis) : " + has_common_elements(aretes_temp, points_acquis));
			System.out.println("----- aretes_temp : " + aretes_temp);
			System.out.println("----- points_acquis : " + points_acquis);
			
			if((dist_act + last_point.distance(point)) <= budget) { ////// GROS PROBLEME AVEC last_point.distance(point) VU QU'Y EN A QU'ON AJOUTE PAS
				if(!points_acquis.contains(last_point)
				&& !points_acquis.contains(point)) {
					
					aretes_temp.addFirst(new Arete(last_point, point));
					dist_act += last_point.distance(point);
					if(is_hitpoint) val_act++;
				} 
				
				// Pour pouvoir retenir un segment comme bon, il faut qu'il contienne un des points_to_keep
				if( is_hitpoint
				&& ((val_act > val_max || (val_act == val_max && dist_act < dist_max))
					&& (points_acquis.size() == 0 || has_common_elements(aretes_temp, points_acquis)))) {
					//|| (!Collections.disjoint(aretes_temp, points_to_keep)))) {
					
					aretes.clear();// Peut etre tout supprimer pour pas surcharger le GC
					aretes.addAll(aretes_temp);
					val_max = val_act;
					dist_max = dist_act;
				}
				
				last_point = point;
				
			} else if(last_point.distance(point) > budget) {
				aretes_temp.clear();
				dist_act = 0;
				val_act = 0;
				last_point = point;
			} else {
				System.out.println(">>>> " + last_point.distance(point) + "     " + budget + "     " + dist_act);
				System.out.println(">>>> " + last_point);
				System.out.println(">>>> " + point);
				System.out.println(">>>> " + hit_points);
				while((dist_act + last_point.distance(point)) > budget && !hit_points.contains(aretes_temp.getLast().p1)) {
					/*
					// si on retire un des points à garder et qu'il n'y en reste plus du tout dans aretes_temp
					// -> ça veut dire qu'on part de l'arbre en construction et qu'on serait en train de
					//	  construire une branche détachée de cet arbre
					if(Collections.disjoint(aretes_temp, points_to_keep)
					&& (points_to_keep.contains((aretes_temp.getLast().p1))
					   || points_to_keep.contains((aretes_temp.getLast().p2)))) {
						can_evolve = false;
					}
					*/
					
					Arete temp = aretes_temp.removeLast();
					dist_act -= temp.longueur; 
					val_act--;
					
					
				}
				
				aretes_temp.addFirst(new Arete(last_point, point));
				dist_act += last_point.distance(point);
				val_act++;
				
				// Comme on ne peut qu'enlever du score dans ce if, 
				// on ne vérifie pas si aretes_temp peut remplacer aretes
				
				last_point = point;
			}
		}
		
		
		
	}
	
	
	public ArrayList<Segment> getSegmentCandidates(Tree2D arbre, int budget, ArrayList<Point> hitPoints, ArrayList<Point> points_acquis, ArrayList<Point> points_to_keep){
		System.out.println("getSegmentCandidates : " + arbre.getRoot());
		if(arbre.getSubTrees().size() == 0) {
			ArrayList<Segment> result = new ArrayList<>();
			result.add(new Segment(budget, arbre.getRoot(), points_acquis, points_to_keep, hitPoints));
			return result;
		}
		ArrayList<Segment> result = new ArrayList<>();
		
		for(Tree2D fils : arbre.getSubTrees()) {
			for(Segment segment : getSegmentCandidates(fils, budget, hitPoints, points_acquis, points_to_keep)) {
				segment.addPoint(arbre.getRoot(), hitPoints.contains(arbre.getRoot()));
				System.out.println("segment.val_max : " + segment.val_max);
				System.out.println("segment.dist_max : " + segment.dist_max);
				System.out.println(segment);
				result.add(segment);
			}
		}
		
		return result;
		
	}
	
	public boolean has_common_elements(LinkedList<Arete> aretes, ArrayList<Point> points) {
		for(Arete arete : aretes) {
			if(points.contains(arete.p1)) return true;
			if(points.contains(arete.p2)) return true;
		}
		return false;
	}

	public Tree2D getPointAsRootOfTree(Tree2D arbre, Point point) {
		if(point.equals(arbre.getRoot())) return arbre;
		for(Tree2D fils : arbre.getSubTrees()) {
			Tree2D temp = getPointAsRootOfTree(fils, point);
			if(temp != null) return temp;
		}
		return null;
	}
	
	
	
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
	
	public ArrayList<Tree2D> getTreeExtremities(LinkedList<Arete> aretes){
		
		//System.out.println(aretes);
		
		ArrayList<Tree2D> points_to_start = new ArrayList<>();
		Tree2D tree = edgesToTree(aretes, aretes.get(0).p1);
		if(tree.getSubTrees().size() == 1) points_to_start.add(tree);
		//points_to_start.add(tree);
		
		//System.out.println(aretes);
		
		ArrayList<Point> points = getPointsFromEdges(aretes);
		
		//System.out.println(points);
		
		for(Point point : points) {
			//System.out.println(point + "\n\n" + tree);
			Tree2D temp = getPointAsRootOfTree(tree, point);
			//System.out.println(temp);
			
			if (temp.getSubTrees().size() == 0) {
				points_to_start.add(edgesToTree(aretes, point));
			}
		}
		
		return points_to_start;
	}
	
	public ArrayList<Point> getPointsFromEdges(LinkedList<Arete> aretes){
		ArrayList<Point> points = new ArrayList<>();
		for(Arete arete : aretes) {
			if(!points.contains(arete.p1)) points.add(arete.p1);
			if(!points.contains(arete.p2)) points.add(arete.p2);
		}
		
		return points;
	}
	
	public Tree2D calculSteinerBudget(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		// Execution des différentes étapes de steiner comme plus haut
		int[][] matrice_directions = calculShortestPaths(points, edgeThreshold);
		
		LinkedList<Arete> aretes_S = getAretesS(points, hitPoints, matrice_directions);
		LinkedList<Arete> aretes_K = kruskal(hitPoints, aretes_S);
		
		Pair<ArrayList<Point>, LinkedList<Arete>> paire = applyT0toG(points, getAretesFromPoints(points, edgeThreshold), hitPoints, aretes_K, matrice_directions);
		LinkedList<Arete> aretes_finales = kruskal(paire.first, paire.second);
		
		// TODO \/ \/ \/
		// a faire pour chaque point du graphe : on pourrait n'examiner que les segments en partant d'un point
		// qui n'appartient qu'à une seule arete  (si on commence on milieu, ça ne sert à rien et regarder si 
		// la racine qu'on s'apprete à examiner est pas en fait le bout d'un autre segment
		
		boolean finished = false;
		
		ArrayList<Tree2D> extremites = getTreeExtremities(aretes_finales);
		for(Tree2D extremite : extremites) {
			System.out.println(extremite.getRoot());
		}
		
		LinkedList<Arete> aretes_acquises = new LinkedList<>();
		ArrayList<Point> points_acquis = new ArrayList<>();
		
		int budget = BUDGET;
		
		Tree2D tree = edgesToTree(aretes_finales, hitPoints.get(0));
		
		
		while(!finished) {
			System.out.println("=== NOUVELLE ITERATION === === === === === === === === === === === === ===");

			// Il faudra qu'il y ait au moins 1 de ces points dans le segment final
			ArrayList<Point> points_to_keep = new ArrayList<>();
			
			//Il faudra chercher le meilleur segment à partir de chacun de ces extremités 
			// -> On va donc en faire des racines d'un arbre qu'on va parcourir
			ArrayList<Tree2D> points_to_start = new ArrayList<>();
			points_to_start.add(tree);
			
			for(Point point : points_acquis) {
				Tree2D temp = getPointAsRootOfTree(tree, point);
				if(temp.getSubTrees().size() >= 2) {
					boolean is_all_taken = true;
					for(Tree2D subtree : temp.getSubTrees()) {
						
						if(!(points_acquis.contains(point)
						  && points_acquis.contains(subtree.getRoot()))) {
							is_all_taken = false;
							// on peut faire candidate_roots.add(point) ; break
						}
						
					}
					
					if(!is_all_taken) points_to_keep.add(point);
				} 
			}
			

			int score_max = 0;
			int cout_max = Integer.MAX_VALUE;
			// ((val_act > val_max || (val_act == val_max && dist_act < dist_max))
			Segment segment_max = new Segment(BUDGET, hitPoints.get(0), points_acquis, points_to_keep, hitPoints);
			
			//System.out.println(extremites);
			
			for(Tree2D extremite : extremites) {
				System.out.println("=== NOUVELLE EXTREMITE === === === === === === === === === === === === ===");
				for(Tree2D subtree : extremite.getSubTrees()) {
					System.out.println("subtree.getRoot() : " + subtree.getRoot());
				}
				ArrayList<Segment> segments = getSegmentCandidates(extremite, budget, hitPoints, points_acquis, points_to_keep);
				for(Segment segment : segments) {
					
					System.out.println("segment.val_max : " + segment.val_max);
					System.out.println("segment.dist_max : " + segment.dist_max);
					System.out.println("score_max/cout_max : " + ((float)score_max/cout_max));
					System.out.println("segment.val_max/segment.dist_max : " + ((float)segment.val_max/segment.dist_max));
					//if(segment.val_max > score_max || (segment.val_max == score_max && segment.dist_max < cout_max)) {
					if(segment.val_max > 0 && ((float)segment.val_max/segment.dist_max) > ((float)score_max/cout_max)) {
						segment_max = segment;
						score_max = segment.val_max;
						cout_max = segment.dist_max;
					}
				}
				System.out.println(segment_max);
				System.out.println("racine extremite : " + extremite.getRoot());
				System.out.println("arbre extremite : " + extremite);
				System.out.println("points acquis : " + points_acquis);
				System.out.println("points to keep : " + points_to_keep);
				try {
					Thread.sleep(250L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(score_max == 0) {
				System.out.println(segment_max);
				System.out.println("Impossible de construire un arbre avec ce budget");
				break;
			}
			if(segment_max.dist_max == 0) break;
			
			budget -= segment_max.dist_max;
			
			aretes_acquises.addAll(segment_max.aretes);
			points_acquis = getPointsFromEdges(aretes_acquises);
			
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
		
		
		
		
		
		
		
		
		
		return edgesToTree(aretes_acquises, points_acquis.get(0));
	}
}
