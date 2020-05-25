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
	
	
	public LinkedList<Arete> getAretesSnew(ArrayList<Point> points_g, ArrayList<Point> points_s, int[][] matrice_directions) {
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
					//while(current_point != p2) {
						//System.out.println(current_point + " --- " + p2);
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
		
		//System.out.println(aretes_s.size());
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
				
				//System.out.print(" -> " + temp);
				moy++;
				
				if(!points_res.contains(current_point)) points_res.add(current_point);
			}
			//System.out.println("");
		}
		
		for(Point p1 : adj.keySet()) {
			for(Point p2 : adj.get(p1)) {
				//System.out.print("[" + p1.x + " " + p1.y + " - " + p2.x + " " + p2.y + "]");
				aretes_res.add(new Arete(p1, p2));
			}
			//System.out.println("");
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
		//System.out.println("calculShortestPaths()");
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
	
	
	public LinkedList<Arete> kruskal(ArrayList<Point> points_global, LinkedList<Arete> liste_aretes) {		
		HashMap<Point, Integer> subgraphs = new HashMap<>();
		Integer subgraph_count = 0;
		 LinkedList<Arete> result = new LinkedList<>();
		
		HashMap<Point, ArrayList<Point>> voisins = new HashMap<>();
		ArrayList<Point> points = new ArrayList<>();
		points.addAll(points_global);
		Point racine = points.get(0);
		
		liste_aretes.sort((o1, o2) -> o1.longueur.compareTo(o2.longueur));
		
		/*
		//System.out.println(liste_aretes);
		System.out.println("liste_aretes.size()  : " + liste_aretes.size());
		System.out.println("points_global.size() : " + points_global.size());
		
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		while (points.size() > 0 && liste_aretes.size() > 0) {
			//System.out.println(points);
			//System.out.println(points.size());
			
			Arete arete = liste_aretes.pop();
			
			//System.out.print(!subgraphs.containsKey(arete.p1));
			//System.out.print(!subgraphs.containsKey(arete.p2));
			//System.out.println((subgraphs.containsKey(arete.p1) && subgraphs.containsKey(arete.p2) && (subgraphs.get(arete.p1) != subgraphs.get(arete.p2))));
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
				
				if(!voisins.containsKey(arete.p1)) voisins.put(arete.p1, new ArrayList<Point>());
				if(!voisins.containsKey(arete.p2)) voisins.put(arete.p2, new ArrayList<Point>());
				voisins.get(arete.p2).add(arete.p1);
				voisins.get(arete.p1).add(arete.p2);
				result.add(arete);

				if(points.contains(arete.p1)) points.remove(arete.p1);
				if(points.contains(arete.p2)) points.remove(arete.p2);
				
			}
		}
		
		// Au cas où il y ait plusieurs graphes ou des points tous seuls, on prends le plus gros graphe
		
		HashMap<Integer, Integer> subgraphsSize = new HashMap<>();
		
		for(Point point : subgraphs.keySet()) {
			if(!subgraphsSize.containsKey(subgraphs.get(point))) subgraphsSize.put(subgraphs.get(point), 0);
			subgraphsSize.put(subgraphs.get(point), subgraphsSize.get(subgraphs.get(point)) + 1);
		}
		int max = 0;
		int biggest_subgraph = 0;
		for(Integer subgraph : subgraphsSize.keySet()) {
			if(subgraphsSize.get(subgraph) >= max) {
				max = subgraphsSize.get(subgraph);
				biggest_subgraph = subgraph;
			}
		}
		for(Point point : subgraphs.keySet()) {
			if(subgraphs.get(point) == biggest_subgraph) racine = point;
		}
		
		
		
		return result;
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
			//voisins.get(voisin).remove(point); ça me parait bizarre qu'il faille pas emttre ça
			fils.add(getFils(voisins, voisin));
		}
		
		return new Tree2D(point, fils);
	}
	
	
	private Tree2D edgesToTree(LinkedList<Arete> aretes_K, Point root) {
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public LinkedList<Arete> getAretesFromTree(Tree2D tree){
		LinkedList<Arete> res = new LinkedList<>();
		for(Tree2D subTree : tree.getSubTrees()) {
			res.add(new Arete(tree.getRoot(), subTree.getRoot()));
			res.addAll(getAretesFromTree(subTree));
		}
		//System.out.println(res);
		return res;
	}
	
	
	
	
	
	private boolean contains(LinkedList<Arete> edges,Point p,Point q){
		for (Arete e:edges){
			if (e.p1.equals(p) && e.p2.equals(q) ||
					e.p1.equals(q) && e.p2.equals(p) ) return true;
		}
		return false;
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

	
	
	public LinkedList<Arete> kruskalProf(ArrayList<Point> points, LinkedList<Arete> liste_aretes, int edgeThreshold) {		
		LinkedList<Arete> edges = new LinkedList<Arete>();
		/*
		for (Point p: points) {
			for (Point q: points) {
				if (p.equals(q) || contains(edges,p,q)) continue;
				edges.add(new Arete(p,q));
			}
		}
		*/
		edges.addAll(liste_aretes);
		edges = sort(edges);

		LinkedList<Arete> kruskal = new LinkedList<Arete>();
		Arete current;
		NameTag forest = new NameTag(points);
		while (edges.size()!=0) {
			current = edges.remove(0);
			if (forest.tag(current.p1)!=forest.tag(current.p2)) {
				kruskal.add(current);
				forest.reTag(forest.tag(current.p1),forest.tag(current.p2));
			}
		}

		return kruskal;
	}


	
	
	
	
	
	public Tree2D calculSteiner(ArrayList<Point> points, int edgeThreshold, ArrayList<Point> hitPoints) {
		
		int[][] matrice_directions = calculShortestPaths(points, edgeThreshold);
		
		// Construction de l'arbre K
		LinkedList<Arete> aretesS = getAretesSnew(points, hitPoints, matrice_directions);

		// Kruskal sur K
		//Tree2D res = kruskal(hitPoints, aretesS);
		LinkedList<Arete> aretes_K = kruskalProf(hitPoints, aretesS, edgeThreshold);
		
		// appliquer K à G -> H
		
		Pair<ArrayList<Point>, LinkedList<Arete>> paire = applyT0toG(points, getAretes(points, edgeThreshold), hitPoints, aretes_K, matrice_directions);
		ArrayList<Point> points_H = (ArrayList<Point>) paire.first;
		LinkedList<Arete> aretes_H = (LinkedList<Arete>) paire.second;
		
		System.out.println("points_H : " + points_H.size());
		System.out.println("aretes_H : " + aretes_H.size());
		
		
		/*
		
		HashMap<Point, ArrayList<Point>> voisins = new HashMap<>();
		for(Point point : hitPoints) voisins.put(point, new ArrayList<Point>());
		
		for(Arete arete : aretes_K) {
			//if(!voisins.get(arete.p1).contains(arete.p2)) voisins.get(arete.p1).add(arete.p2);
			//if(!voisins.get(arete.p2).contains(arete.p1)) voisins.get(arete.p2).add(arete.p1);
			// normalement ça ne fait rien car le cas est traité
			voisins.get(arete.p1).add(arete.p2);
			voisins.get(arete.p2).add(arete.p1);
		}
		
		//Tree2D res = getFils(voisins, hitPoints.get(0));
		*/
		
		LinkedList<Arete> aretes_finales = kruskalProf(points_H, aretes_H, edgeThreshold);
		
		System.out.println("aretes_finales : " + aretes_finales.size());
		
		Tree2D res = edgesToTree(aretes_finales, aretes_finales.get(0).p1);

		
		//System.out.println("aretes_res : " + getAretesFromTree(res).size());
		
		
		
		return res;
		/*
		
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
		*/
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
