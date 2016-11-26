
//package Dijkstra;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

class Graph {
	private final Map<Integer, Graph.Vertex> graph; // mapping of vertex names to Vertex objects, built from a set of Edges
	private int NumVers;
	public int getNumVers() {
		return NumVers;
	}
	/** One edge of the graph (only used by Graph constructor) */
	public static class Edge {
		public final int v1, v2;
		public double dist;
		public Edge(int i, int j, double dist) {
			this.v1 = i;
			this.v2 = j;
			this.dist = dist;
		}
	}

	/** One vertex of the graph, complete with mappings to neighbouring vertices */
	public static class Vertex implements Comparable<Vertex> {
		public final int name;
		public double dist = Double.MAX_VALUE; // MAX_VALUE assumed to be infinity
		boolean passable = true;
		public Vertex previous = null;
		public final Map<Vertex, Double> neighbours = new HashMap<>();

		public Vertex(int name) {
			this.name = name;
		}

		private void printPath() {
			if (this == this.previous) {
				System.out.print(this.name);
			} else if (this.previous == null) {
				System.out.print(this.name);
			} else {
				this.previous.printPath();
				System.out.print(" -> "+ this.name +"("+ this.dist+")");
			}
		}

		public int compareTo(Vertex other) {
			return Double.compare(dist, other.dist);
		}
	}
	public boolean blackList(int v){
		return graph.get(v).passable;
	}
	public void mark(int bl[],boolean flag){
		for(int v:bl){
			graph.get(v).passable=flag;
		}
	}


	/** Builds a graph from a set of edges */
	public Graph(Edge[] edges,int[] bl,boolean b,int vers) {
		graph = new HashMap<>(vers);
		NumVers = vers;
		//one pass to find all vertices
		for (Edge e : edges) {
			if (!graph.containsKey(e.v1) )
				graph.put(e.v1, new Vertex(e.v1));
			if (!graph.containsKey(e.v2))
				graph.put(e.v2, new Vertex(e.v2));
		}

		//another pass to set neighbouring vertices
		
			for (Edge e : edges) {
				graph.get(e.v1).neighbours.put(graph.get(e.v2), e.dist);
				//graph.get(e.v2).neighbours.put(graph.get(e.v1), e.dist); // also do this for an undirected graph
			}

	}


	public Map<Integer, Graph.Vertex> getGraph() {
		return graph;
	}
	/** Runs dijkstra using a specified source vertex 
	 * @param v2 */ 
	public void dijkstra(int startName,int[] bl) {
		if (!graph.containsKey(startName)) {
			System.err.printf("Graph doesn't contain start vertex \"%s\"\n", startName);
			return;
		}
		for (int v : bl) {

			if(startName==v){
				System.err.printf("Graph doesn't start vertex because blacklist");
				return;
			}
		}
		final Vertex source = graph.get(startName);
		NavigableSet<Vertex> q = new TreeSet<>();

		// set-up vertices
		for (Vertex v : graph.values()) {

			v.previous = v == source ? source : null;
			v.dist = v == source ? 0 : Double.POSITIVE_INFINITY;
			q.add(v);

		}
		mark(bl,false);
		dijkstra(q);
		mark(bl,true);
	}


	/** Implementation of dijkstra's algorithm using a binary heap. */
	private void dijkstra(final NavigableSet<Vertex> q) {      
		Vertex u, v;
		while (!q.isEmpty()) {
			u = q.pollFirst(); // vertex with shortest distance (first iteration will return source)
			if (u.dist == Double.POSITIVE_INFINITY ) break; // we can ignore u (and any other remaining vertices) since they are unreachable

			//look at distances to each neighbour
			for (Map.Entry<Vertex, Double> a : u.neighbours.entrySet()) {
				v = a.getKey(); //the neighbour in this iteration
				if(!blackList(v.name)){
					final double alternateDist = u.dist + a.getValue();
					if (alternateDist < v.dist) { // shorter path to neighbour found
						q.remove(v);
						v.dist = alternateDist;
						v.previous = u;
						q.add(v);

					}
				}
			}
		}
	}

	/** Prints a path from the source to the specified vertex 
	 * @param v */
	public double printPath(int endName,int[] bl) {

		if (!graph.containsKey(endName)) {
			System.err.printf("Graph doesn't contain end vertex \"%s\"\n", endName);
			return -1;
		}
		for (int v : bl) {

			if(endName==v){
				System.err.printf("Graph doesn't end vertex because blacklist");
				return -1;
			}
		}

		//graph.get(endName).printPath();
		return graph.get(endName).dist;

	}
	/** Prints the path from the source to every vertex (output order is not guaranteed) */
	public void printAllPaths() {
		for (Vertex v : graph.values()) {
			v.printPath();
			System.out.println();
		}
	}
}

