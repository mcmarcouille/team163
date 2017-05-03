import java.util.*;
import java.io.FileNotFoundException;
import java.io.File;

public class NavigationGraph implements GraphADT<Location, Path> {

	//private fields
	private ArrayList<GraphNode<Location, Path>> graphNodes;
  	private String[] edgeProperties;
	public int size;
  	
  	/**
  	 * Constructs new Navigation Graph to represent locations and paths 
  	 * 
  	 * @param edgePropertyNames
  	 */
	public NavigationGraph(String[] edgePropertyNames) {
		this.edgeProperties = edgePropertyNames;
		this.graphNodes = new ArrayList<GraphNode<Location, Path>>();
		this.size = 0;
	}

	
	/**
	 * Returns a Location object given its name
	 * 
	 * @param name
	 *            name of the location
	 * @return Location object
	 */
	public Location getLocationByName(String name) {
		for(int i = 0; i < size; i++ ){
			if(graphNodes.get(i).getVertexData().getName().equals(name)){
				return graphNodes.get(i).getVertexData();
			}
		}
		
		return null;
	}
	
	
	/**
	 * Adds a vertex to the Graph
	 * 
	 * @param vertex
	 *            vertex to be added
	 */
	public void addVertex(Location vertex) {
		if(vertex == null){
			throw new IllegalArgumentException();
		}
		
		GraphNode newNode = new GraphNode(vertex, size);	
		graphNodes.add(newNode);
		size++;
	}

	/**
	 * Creates a directed edge from src to dest
	 * 
	 * @param src
	 *            source vertex from where the edge is outgoing
	 * @param dest
	 *            destination vertex where the edge is incoming
	 * @param edge
	 *            edge between src and dest
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
     * @return id of the node
     */
	private GraphNode getGraphNode(Location loc) {
		Iterator itr = graphNodes.iterator();
		GraphNode temp = null;
		while (itr.hasNext()) {
			temp = (GraphNode) itr.next();
			if (temp.getVertexData().equals(loc))
				return temp;
		}
		return null;
	}
	
	/**
	 * Getter method for the vertices
	 * 
	 * @return List of vertices of type V
	 */
	public List<Location> getVertices(){
		
		List<Location> locs = new ArrayList<Location>();
		
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
	 * @param src
	 *            Source vertex
	 * @param dest
	 *            Destination vertex
	 * @return Edge of type E from src to dest
	 */
	public Path getEdgeIfExists(Location src, Location dest) {
		List<Path> outEdges = getGraphNode(src).getOutEdges();
		Iterator itr = outEdges.iterator();
		Path temp;
		while (itr.hasNext()){
			temp = (Path) itr.next();
			if (temp.getDestination().equals(dest))
				return temp;
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
		return getGraphNode(src).getOutEdges();
	}


	/**
	 * Returns neighbors of a vertex
	 * 
	 * @param vertex
	 *            vertex for which the neighbors are required
	 * @return List of vertices(neighbors) of type V
	 */
	public List<Location> getNeighbors(Location vertex) {
		if (graphNodes.size() == 0){
			throw new IllegalArgumentException();
		}
		
		List<Location> neighbors = new ArrayList<Location>();
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
	 * @param src
	 *            Source vertex from which the shortest route is desired
	 * @param dest
	 *            Destination vertex to which the shortest route is desired
	 * @param edgePropertyName
	 *            edge property by which shortest route has to be calculated
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
	 * @return array of String that denotes the edge property names
	 */
	public String[] getEdgePropertyNames(){
		return edgeProperties;
	}

	private int Prop(String edgePropertyName) {
		// represent the order where the data of the property is in the file
		int j = 0;
		while (j < edgeProperties.length) {
			if (edgeProperties[j] == edgePropertyName)
				return j;
		}
		return -1;
	}
	
	// the method to get the path with the min property
	private Path getMin(List<Path> outEdges, int j) {
		Path min = outEdges.get(0);
		for (int i = 0; i < outEdges.size(); i++) {
			if (min.getProperties().get(j) > outEdges.get(i).getProperties()
					.get(j))
				min = outEdges.get(i);
		}
		return min;
	}

	private GraphNode<Location, Path> getNode(Location src) {
		for (int i = 0; i < graphNodes.size(); i++) {
			if (graphNodes.get(i).getVertexData() == src)
				return graphNodes.get(i);
		}
		return null;
	}

}
