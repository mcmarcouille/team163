/////////////////////////////////////////////////////////////////////////////
// Semester:         CS367 Spring 2016 
// PROJECT:          p5
// FILE:             NavigationGraph.java
//
// TEAM:    p5team 163
// Authors: Matthew Marcouiller, Jack Yang
// Author1: Matthew Marcouiller, mcmarcouille@wisc.edu, mcmarcouille, Lec 03
// Author2: Jack Yang, zyang366@wisc.edu, zyang366, Lec 03
//
// ---------------- OTHER ASSISTANCE CREDITS 
// Persons: none 
// 
// Online sources: none 
//////////////////////////// 80 columns wide //////////////////////////////////
/**
 * This class implements GraphADT and creates a graph of Locations connected by Paths. 
 *
 * <p>Bugs: (a list of bugs and other problems)
 *
 * @author Matthew Marcouiller, Jack Yang
 */

import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;

public class NavigationGraph implements GraphADT<Location, Path> {

	//private fields
	private ArrayList<GraphNode<Location, Path>> graphNodes; //Initiate an arrayList of nodes
	private ArrayList<String> edgePropertyNames; //Initiate a String array that contains edge properties
	public int num; //Initate an integer that holds the size
  	
	/**
	 * Constructs new Navigation Graph to represent locations and paths 
	 *
	 * PRECONDITIONS: none
	 * 
	 * POSTCONDITIONS: a new navigation graph is created 
	 *
	 * @param edgePropertyNames - a string array that contains the edgePropertyNames
	 * @return none
	 */
	public NavigationGraph(String[] edgePropertyNames) {
		
		this.graphNodes = new ArrayList<GraphNode<Location, Path>>();
		this.num = 0;
		this.edgePropertyNames = new ArrayList<String>();
		for (String names : edgePropertyNames){
			this.edgePropertyNames.add(names);
		}
	}

	
	/**
	 * Returns a Location object given its name
	 *
	 * PRECONDITIONS: name should not be null
	 * 
	 * POSTCONDITIONS: return the Location objcet 
	 *
	 * @param name - name of the location
	 * @return Location object or null
	 */
	public Location getLocationByName(String name) {
		for(int i = 0; i < num; i++ ){
			if(graphNodes.get(i).getVertexData().getName().equals(name)){
				return graphNodes.get(i).getVertexData();
			}
		}
		
		return null;
	}
	
	
	/**
	 * Returns a Location object given its name
	 *
	 * PRECONDITIONS: name should not be null
	 * 
	 * POSTCONDITIONS: return the Location objcet 
	 *
	 * @param name - name of the location
	 * @return Location object or null
	 */
	public void addVertex(Location vertex) {
		if(vertex == null){
			throw new IllegalArgumentException();
		}
		
		GraphNode<Location, Path> newNode = new GraphNode<Location, Path>(vertex, num);	
		graphNodes.add(newNode);
		num++;
	}

	/**
	 * Creates a directed edge from src to dest
	 *
	 * PRECONDITIONS: parameter should not be null
	 * 
	 * POSTCONDITIONS: creates a directed edge
	 *
	 * @param src - source vertex from where the edge is outgoing
	 * @param dest - destination vertex where the edge is incoming
	 * @para edge - edge between src and dest
	 * @return none
	 */
	public void addEdge(Location src, Location dest, Path edge)
					throws IllegalArgumentException {
		
		if(src.equals(dest) || edge == null || src == null || dest == null)
			throw new IllegalArgumentException();
		
		Path newPath = new Path(src, dest, edge.getProperties());
		getNode(src).addOutEdge(newPath);
		
	}
	
	
	
	/**
	 * Find Graph Node by Location, return -1 when does not
	 * find the id, else return id
	 *
	 * PRECONDITIONS: loc should not be null
	 * 
	 * POSTCONDITIONS: fidn the Graph Node
	 *
	 * @param loc - location of the Graph Node
	 * @return id of the node
	 */
	private GraphNode getNodeLoc(Location loc) {
		for (GraphNode<Location, Path> nodes : graphNodes){
			if(nodes.getVertexData().equals(loc)) {
				return nodes;
			}
		}
		return null;
	}
	
	/**
	 * Getter method for the vertices
	 *
	 * PRECONDITIONS: none
	 * 
	 * POSTCONDITIONS: get the vertices
	 *
	 * @param none
	 * @return List of vertices of type V
	 */
	public List<Location> getVertices(){
		
		ArrayList<Location> locs = new ArrayList<Location>();
		
		//go through graphnodes and grab each vertex
		//add those to locs
		for (int i = 0; i < graphNodes.size(); i++){
			locs.add(graphNodes.get(i).getVertexData());
		}
		
		//return list full of vertices 
		return locs;
	}

	/**
	 * Returns edge if there is one from src to dest vertex else null
	 *
	 * PRECONDITIONS: parameter should not be null
	 * 
	 * POSTCONDITIONS: return the edge
	 *
	 * @param src - source vertex from where the edge is outgoing
	 * @param dest - destination vertex where the edge is incoming
	 * @return Edge of type E from src to dest
	 */
	public Path getEdgeIfExists(Location src, Location dest) {
		GraphNode<Location, Path> vert = getNodeLoc(src);
		
		if (vert == null){
			return null;
		}
		
		for (Path path: vert.getOutEdges()){
			if (path.getDestination().equals(dest)){
				return path;
			}
		}
		return null;
	}


	/**
	 * Returns the outgoing edges from a vertex
	 * 
	 * @param src
	 *            Source vertex for which the outgoing edges need to be obtained
	 * @return List of edges of type E
	 */
	public List<Path> getOutEdges(Location src) {
		return getNodeLoc(src).getOutEdges();
	}


	/**
	 * Returns neighbors of a vertex
	 *
	 * PRECONDITIONS: vertex should not be null
	 * 
	 * POSTCONDITIONS: return neighbors of a vertex
	 *
	 * @param vertex - vertex for which the neighbors are required
	 * @return List of vertices(neighbors) of type V
	 */
	public List<Location> getNeighbors(Location vertex) {
		if (graphNodes.size() == 0){
			throw new IllegalArgumentException();
		}
		
		ArrayList<Location> neighbors = new ArrayList<Location>();
//		GraphNode<Location, Path> loca = getNodeLoc(vertex);
		
		for (int i = 0; i < graphNodes.size(); i++){
			//search for node
			if(vertex.equals(graphNodes.get(i))){
				//get the out edges 
				List<Path> destns = graphNodes.get(i).getOutEdges();
				//add each to list 
				for (int j = 0; j < destns.size(); j++){
					neighbors.add(destns.get(i).getDestination());
				}
			}
		}
		
		return neighbors;
	}


	/**
	 * Calculate the shortest route from src to dest vertex using
	 * edgePropertyName
	 *
	 * PRECONDITIONS: parameter should not be null
	 * 
	 * POSTCONDITIONS: return the shortest route
	 *
	 * @param src - Source vertex from which the shortest route is desired
	 * @param dest - Destination vertex to which the shortest route is desired
	 * @para edgePropertyName - edge property by which shortest route has to be calculated 
	 * @return List of edges that denote the shortest route by edgePropertyName
	 */
	public List<Path> getShortestRoute(Location src, Location dest, 
			String edgePropertyName) throws IllegalArgumentException{
		
		List<Path> path = new ArrayList<Path>();
		
		if (getEdgeIfExists(src, dest) != null){
			path.add(getEdgeIfExists(src, dest));
			return path;
		}
		
		//current location
		Location curr = src;
		
		//current edge
		Path currEdge;
		
		//neighbors of current position
		List<Location> neighbors = getNeighbors(curr);
		
		//unvisted places
		List<Location> un = getVertices();
		
		//weight of edge
		double edgeWeight = 0;
		
		//alternative path
		double altPath = 0;
		
		//total of each location, make it really big 
		double[] weights = new double[graphNodes.size()];
		for (int i = 0; i < weights.length; i++){
			weights[i] = 100;
		}
		
		//getting src weight 
		int srcWeight = 0;
		for (int j = 0; j < graphNodes.size(); j++){
			if (src == graphNodes.get(j).getVertexData()) {
				srcWeight = j;
			}
		}
		weights[srcWeight] = 0;
		
		int p = Prop(edgePropertyName);
		if (p == -1)
			throw new IllegalArgumentException();
		
		while (un.isEmpty() == false){
			//got minimum distance first
			currEdge = getMin(getNode(src).getOutEdges(), p);
			curr = currEdge.getDestination();
			un.remove(curr);
			
			for (int k = 0; k < graphNodes.size(); k++) {
				edgeWeight = currEdge.getProperties().get(p);
				int x = 0;
				for (int y = 0; y < graphNodes.size(); y++) {
					if (neighbors.get(k) == graphNodes.get(y).getVertexData()){
						x = y;
					}
				}
				
				//update weight 
				altPath = weights[x] + edgeWeight;
				
				//if there is a shorter path then update
				if (altPath < weights[x]) {
					weights[x] = altPath;
					addEdge(neighbors.get(k), curr, currEdge);
				}
			}
		}
		
		path.add(getEdgeIfExists(src, dest));
		
		return path;
	}
	
	/**
	 * Getter method for edge property names
	 *
	 * PRECONDITIONS: none
	 * 
	 * POSTCONDITIONS: return array of strings
	 *
	 * @param none
	 * @return array of String that denotes the edge property names
	 */
	public String[] getEdgePropertyNames(){
		String[] edgeProp = new String[edgePropertyNames.size()];
		for (int i = 0; i < edgePropertyNames.size(); i++){
			edgeProp[i] = edgePropertyNames.get(i);
		}
		
		return edgeProp;
	}

	
	/**
	 * Represent the order where the data of the property is in the file
	 *
	 * PRECONDITIONS: edgePropertyName should not null
	 * 
	 * POSTCONDITIONS: return the order
	 *
	 * @param edgePropertyName - the name of the edge
	 * @return an integer of the order
	 */
	private int Prop(String edgePropertyName) {
		// represent the order where the data of the property is in the file
		int j = 0;
		while (j < edgePropertyNames.size()){
			if (edgePropertyNames.equals(edgePropertyName))
				return j;
		}
		return -1;
	}
	
	/**
	 * Get the minimum 
	 *
	 * PRECONDITIONS: outEdges should not be null and j should not negative
	 * 
	 * POSTCONDITIONS: return the minimum path
	 *
	 * @param outEdges - outedges of the path
	 * @param j - counter 
	 * @return the minimum path
	 */
	private Path getMin(List<Path> outEdges, int j) {
		Path min = outEdges.get(0);
		for (int i = 0; i < outEdges.size(); i++) {
			if (min.getProperties().get(j) > outEdges.get(i).getProperties()
					.get(j))
				min = outEdges.get(i);
		}
		return min;
	}

	/**
	 * Get the node given the location
	 *
	 * PRECONDITIONS: src should not null
	 * 
	 * POSTCONDITIONS: return the node desired
	 *
	 * @param src - the location of the desired node
	 * @return the desired node
	 */
	private GraphNode<Location, Path> getNode(Location src) {
		for (int i = 0; i < graphNodes.size(); i++) {
			if (graphNodes.get(i).getVertexData() == src)
				return graphNodes.get(i);
		}
		return null;
	}
	
	public String toString() {
		String graph = "";
		int currentLength = 1;
		for (GraphNode<Location, Path> v: graphNodes) {
			for (Path p: v.getOutEdges()) {
				if (currentLength < v.getOutEdges().size()) {
					graph += p + ", ";
					currentLength++;
				}
				else {
					graph += p + "\n";
					currentLength = 1;
				}
			}
		}
		return graph.substring(0, graph.length() - 1);
	}
	

}
