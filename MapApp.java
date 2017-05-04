// Semester:         CS367 Spring 2016 
// PROJECT:          p5
// FILE:             MapApp.java
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
 * This is the driver class that contains the main method. This app must first read/parse the input filename passed as command line argument and create a NavigationGraph object.
 *
 * <p>Bugs: (a list of bugs and other problems)
 *
 * @author Matthew Marcouiller, Jack Yang
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class MapApp {

	private NavigationGraph graphObject;		//Initiate a Navigation Graph object


	/**
	 * Constructs a MapApp object
	 *
	 * PRECONDITIONS: graph should not null
	 * 
	 * POSTCONDITIONS: contstruct the object
	 *
	 * @param graph - NaviagtionGraph object
	 * @return none
	 */
	public MapApp(NavigationGraph graph) {
		this.graphObject = graph;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Usage: java MapApp <pathToGraphFile>");
			System.exit(1);
		}

		// read the filename from command line argument
		String locationFileName = args[0];
		try {
			NavigationGraph graph = createNavigationGraphFromMapFile(locationFileName);
			MapApp appInstance = new MapApp(graph);
			appInstance.startService();

		} catch (FileNotFoundException e) {
			System.out.println("GRAPH FILE: " + locationFileName + " was not found.");
			System.exit(1);
		} catch (InvalidFileException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		}

	}


	/**
	 * Displays options to user about the various operations on the loaded graph
	 *
	 * PRECONDITIONS: none
	 * 
	 * POSTCONDITIONS: display options
	 *
	 * @param none
	 * @return none
	 */
	public void startService() {

		System.out.println("Navigation App");
		Scanner sc = new Scanner(System.in);

		int choice = 0;
		do {
			System.out.println();
			System.out.println("1. List all locations");
			System.out.println("2. Display Graph");
			System.out.println("3. Display Outgoing Edges");
			System.out.println("4. Display Shortest Route");
			System.out.println("5. Quit");
			System.out.print("Enter your choice: ");

			while (!sc.hasNextInt()) {
				sc.next();
				System.out.println("Please select a valid option: ");
			}
			choice = sc.nextInt();

			switch (choice) {
			case 1:
				System.out.println(graphObject.getVertices());
				break;
			case 2:
				System.out.println(graphObject.toString());
				break;
			case 3: {
				System.out.println("Enter source location name: ");
				String srcName = sc.next();
				Location src = graphObject.getLocationByName(srcName);

				if (src == null) {
					System.out.println(srcName + " is not a valid Location");
					break;
				}

				List<Path> outEdges = graphObject.getOutEdges(src);
				System.out.println("Outgoing edges for " + src + ": ");
				for (Path path : outEdges) {
					System.out.println(path);
				}
			}
				break;

			case 4:
				System.out.println("Enter source location name: ");
				String srcName = sc.next();
				Location src = graphObject.getLocationByName(srcName);

				System.out.println("Enter destination location name: ");
				String destName = sc.next();
				Location dest = graphObject.getLocationByName(destName);

				if (src == null || dest == null) {
					System.out.println(srcName + " and/or " + destName + " are not valid Locations in the graph");
					break;
				}

				if (src == dest) {
					System.out.println(srcName + " and " + destName + " correspond to the same Location");
					break;
				}

				System.out.println("Edge properties: ");
				// List Edge Property Names
				String[] propertyNames = graphObject.getEdgePropertyNames();
				for (int i = 0; i < propertyNames.length; i++) {
					System.out.println("\t" + (i + 1) + ": " + propertyNames[i]);
				}
				System.out.println("Select property to compute shortest route on: ");
				int selectedPropertyIndex = sc.nextInt() - 1;

				if (selectedPropertyIndex >= propertyNames.length) {
					System.out.println("Invalid option chosen: " + (selectedPropertyIndex + 1));
					break;
				}

				String selectedPropertyName = propertyNames[selectedPropertyIndex];
				List<Path> shortestRoute = graphObject.getShortestRoute(src, dest, selectedPropertyName);
				for(Path path : shortestRoute) {
					System.out.print(path.displayPathWithProperty(selectedPropertyIndex)+", ");
				}
				if(shortestRoute.size()==0) {
					System.out.print("No route exists");
				}
				System.out.println();

				break;

			case 5:
				break;

			default:
				System.out.println("Please select a valid option: ");
				break;

			}
		} while (choice != 5);
		sc.close();
	}


	/**
	 * Reads and parses the input file passed as argument create a
	 * NavigationGraph object. The edge property names required for
	 * the constructor can be got from the first line of the file
	 * by ignoring the first 2 columns - source, destination.  
	 * Use the graph object to add vertices and edges as 
	 * you read the input file. 
	 *
	 * PRECONDITIONS: file path should be valid
	 * 
	 * POSTCONDITIONS: rens the program
	 *
	 * @param graphFilepath
	 *            path to the input file
	 * @return NavigationGraph object
	 * @throws FileNotFoundException
	 *             if graphFilepath is not found
	 * @throws InvalidFileException
	 *             if header line in the file has < 3 columns or 
	 *             if any line that describes an edge has different number of properties 
	 *             	than as described in the header or 
	 *             if any property value is not numeric 
	 */
	public static NavigationGraph createNavigationGraphFromMapFile(String graphFilepath)
					throws FileNotFoundException, InvalidFileException{
		
		Scanner scr;
		
		try{
		//get input from File
		File f = new File(graphFilepath);
		scr = new Scanner(f);
		} //if graphFilepath is not found
		catch(FileNotFoundException e){
			throw new FileNotFoundException("File not found");
		}
		
		//get first line
		String currLine = scr.nextLine();
		String[] info = currLine.split(" ");
		
		//if header line in the file has < 3 columns
		if (info.length < 3)
			throw new InvalidFileException("Too many columns");
		
		//parsing 
		String[] names = new String[info.length - 2];
		for(int i = 2; i < info.length; i++){
			names[i - 2] = info[i];
		}
		
		NavigationGraph naviG = new NavigationGraph(names);
		
		while(scr.hasNextLine()){
			currLine = scr.nextLine();
			String[] data = currLine.split(" ");
			
			if (data.length != info.length){
				throw new InvalidFileException("Different number of properties");
			}
			
			Location src = new Location(data[0]);
			Location dest = new Location(data[1]);
			List<Double> properties = new ArrayList<Double>();
			
			try{
				for (int i = 2; i < info.length; i++){
					properties.add(Double.parseDouble(data[i]));
				}
			}catch (NumberFormatException e){
				throw new InvalidFileException("Not numeric");
			}
			
			Path edge = new Path(src, dest, properties);
			
			naviG.addVertex(src);
			naviG.addVertex(dest);
			naviG.addEdge(src, dest, edge);
		}
		
		return naviG;
	}

}
