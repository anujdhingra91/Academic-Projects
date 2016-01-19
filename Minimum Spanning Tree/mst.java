
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/*
 * Advanced Data Structures (COP 5536) - Project
 * By Anuj dhingra
 * UFID : 0899-6498
 * Email : anujdhingra@ufl.edu
 * 
 * This program takes in as input a graph which can be a randomly generated graph or a user prepared graph.
 * It finds out the Minimum spanning tree from that graph by using adjacency list, Fibonacci Heap and Min heap data structure.
 * Min heap is commented as it was not a part of the project.
 * 
 */
public class mst {

/* Main class to take in inputs for the two modes i.e. random mode and user input mode.
 *        
 */
        public static void main(String[] args) throws Exception {
                
               
                int[] mstGraph;
                ArrayList<Edge> mstGraph2;
                ArrayList<Edge> neighbourEdgesFHeap = new ArrayList<Edge>(); // temporary data structure to store all the edges between a node and its neighbors.
                tempNode Node[]; // this is used to finally store the neighbors in an adjacency list data structure.
                
                int nodes = 0,density = 0,edges=0;
                long sum = 0;
                
                if(args.length==3) //for random graph mode.
                {
                if(args[0].equals("-r"))
                {
                	nodes = Integer.parseInt(args[1]);
                	density = Integer.parseInt(args[2]);
                    RandomNode newGraph = new RandomNode(nodes, density); //initialize the random graph.
                    UserInput randomGraph = newGraph.Generate(); // to generate the random graph.
                    ArrayList<Edge> remainEdges  = randomGraph.getEdges(); //get edges of current graph.
                    
                    Node = new tempNode[randomGraph.getNodes().size()]; 
                    for(int j=0;j<randomGraph.getNodes().size();j++)
                    Node[j] = new tempNode(j);	
                    
                    //for loop below stores the neighbor of the 'traverser' node in the graph.
                    for(int traverser=0;traverser<randomGraph.getNodes().size();traverser++)
                    {
                    	neighbourEdgesFHeap.clear();
                    	// temporary storage.
                    	neighbourEdgesFHeap = randomGraph.getNeighbourNodes(randomGraph.getNodes().get(traverser),remainEdges);
                    		for(int neighborKeeper = 0;neighborKeeper<neighbourEdgesFHeap.size();neighborKeeper++)
                    		{
                    			if(traverser == neighbourEdgesFHeap.get(neighborKeeper).vertex1().getNodeID())
                    			{
                    				//final storage.
                    				Node[traverser].addToAdjacencyList(neighbourEdgesFHeap.get(neighborKeeper).vertex2().getNodeID(), neighbourEdgesFHeap.get(neighborKeeper).getCost());
                    			}
                    			else if(traverser == neighbourEdgesFHeap.get(neighborKeeper).vertex2().getNodeID())
                    			{
                    				Node[traverser].addToAdjacencyList(neighbourEdgesFHeap.get(neighborKeeper).vertex1().getNodeID(), neighbourEdgesFHeap.get(neighborKeeper).getCost());
                    			}
                    			//System.out.println(Node[traverser].nodeID+" : "+Node[traverser].keepNeighbors.get(neighborKeeper).getNeighbor()+" "+Node[traverser].keepNeighbors.get(neighborKeeper).getCost());
                    		}
                    		
                    }
                    
/*
 *  Min heap data structure used for testing purposes. Not to be included in the final output.
 *  uncomment the following to get the results using Min heap data structure.                   
 */
       
                    /*
                    MinHeap heap = null;
                    HeapNode hn, hn2;
                    int startVertex;
                    boolean[] visited = null;
                    heap = new MinHeap(nodes);
                    visited = new boolean[nodes];
                    startVertex = (int) (Math.random() * nodes);
                    
                    for (int i = 0; i < nodes; i++) {
                        visited[i] = false;

                        if (startVertex == i) {
                            hn = new HeapNode(i, startVertex, 0);
                        } else {
                            hn = new HeapNode(i, -1, Integer.MAX_VALUE);
                        }
                        try {
                            heap.insert(hn);
                        } catch (Exception e) {
                            System.out.println("ERR. " + e.getMessage());
                        }
                    }
                
                 long b = System.currentTimeMillis();
                    for (int i = 0; i < nodes; i++) {
                        hn = heap.extractMin();
                        visited[hn.vertex] = true;
                        sum += hn.weight;
                        
                        for(int j=0;j < Node[hn.vertex].keepNeighbors.size();j++)
                        {
                        	
                        	if(!visited[Node[hn.vertex].keepNeighbors.get(j).getNeighbor()])
                        	{
                        		if(Node[hn.vertex].keepNeighbors.get(j).getCost() < heap.nodes[heap.totalPositions[Node[hn.vertex].keepNeighbors.get(j).getNeighbor()]].weight)
                        		{
                        			hn2 = heap.delete(heap.totalPositions[Node[hn.vertex].keepNeighbors.get(j).getNeighbor()]);
                        			hn2.weight = Node[hn.vertex].keepNeighbors.get(j).getCost() ;
                        			hn2.tailVertex = hn.vertex;
                        			heap.insert(hn2);
                        		}
                        	}        
                    }
                }
                     
                    System.out.println("\nOverral cost of minimum spanning tree is: " + sum + "\n");
                    
                    System.out.println(" \nTime Taken by Min Heap = "+(System.currentTimeMillis()-b));
         
 */
                    
                   FibonacciHeap heapMST = new FibonacciHeap(randomGraph); // initialize Fibonacci heap.
                    //System.out.println( " \n\n------MST----- FibonacciHeap");
                    long f = System.currentTimeMillis();
                    heapMST.getMST(Node);
                    System.out.println( " \nTime Taken by Fibonacci Heap =  "+(System.currentTimeMillis()-f));
                    System.out.println(heapMST.getTotalCost());
                    
                    //initiaize Simple Scheme.
                    SimpleScheme simpleMST = new SimpleScheme(randomGraph);
                    // System.out.println( " \n\n------MST----- Simple Scheme ");
                    //long s = System.currentTimeMillis();
                    mstGraph = simpleMST.getMST(Node);
                    //System.out.println( " \nTime Taken by Simple Scheme = "+(System.currentTimeMillis()-s));
                    System.out.println(simpleMST.getTotalCost());
                   
                }
        
                }
                else if(args.length == 2) // for user Input mode.
                {
                	long time;
                	UserInput userGraph = new UserInput();
                	
                    try {
                            @SuppressWarnings("resource")
							Scanner readFile = new Scanner(new File(args[1])); //read the file from the arguments
                            nodes = Integer.parseInt(readFile.next() );
                            edges = Integer.parseInt(readFile.next() );
                            
                            while(readFile.hasNextLine())
                            {
                            	    
                                    Node a = new Node(Integer.parseInt(readFile.next()) );
                                    Node b = new Node(Integer.parseInt(readFile.next()) );
                                    Edge e = new Edge(a, b, Integer.parseInt(readFile.next()) );
                                    userGraph.AddNode(a); // store node a
                                    userGraph.AddNode(b); // store node b
                                    userGraph.AddEdge(e); // store edge between a and b.
                            }
                    } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                    }
                    
                    //userGraph.PrintGraph();
                    Node = new tempNode[userGraph.getNodes().size()];
                    for(int j=0;j<userGraph.getNodes().size();j++)
                    Node[j] = new tempNode(j);	
                    
                    //for loop below is the same as in random graph generation mode.
                    for(int traverser=0;traverser<userGraph.getNodes().size();traverser++)
                    {
                    	
                    	neighbourEdgesFHeap.clear();
                    	neighbourEdgesFHeap = userGraph.getNeighbourNodes(userGraph.getNodes().get(traverser),userGraph.getEdges());
                    		for(int neighborKeeper = 0;neighborKeeper<neighbourEdgesFHeap.size();neighborKeeper++)
                    		{
                    			if(traverser == neighbourEdgesFHeap.get(neighborKeeper).vertex1().getNodeID())
                    			{
                    				Node[traverser].addToAdjacencyList(neighbourEdgesFHeap.get(neighborKeeper).vertex2().getNodeID(), neighbourEdgesFHeap.get(neighborKeeper).getCost());
                    			}
                    			else if(traverser == neighbourEdgesFHeap.get(neighborKeeper).vertex2().getNodeID())
                    			{
                    				Node[traverser].addToAdjacencyList(neighbourEdgesFHeap.get(neighborKeeper).vertex1().getNodeID(), neighbourEdgesFHeap.get(neighborKeeper).getCost());
                    			}
                    	
                    		}
                    		
                    }
                    if(args[0].equals("-s"))
                    {
                    SimpleScheme simpleMST = new SimpleScheme(userGraph);
                    System.out.println( " ------MST----- ");
                    //time=System.currentTimeMillis();
                    mstGraph = simpleMST.getMST(Node);
                   // System.out.println( " Time taken by SimpleScheme = "+(System.currentTimeMillis()-time));
                    System.out.println(simpleMST.getTotalCost());
                    }
                    else if(args[0].equals("-f"))
                    {
                    	 FibonacciHeap heapMST = new FibonacciHeap(userGraph);
                         time = System.currentTimeMillis();
                         mstGraph2 = heapMST.getMST(Node);
                         System.out.println( " \nTime Taken by Fibonacci Heap =  "+(System.currentTimeMillis()-time));
                         System.out.println(heapMST.getTotalCost());
                    }
                    }
                
                
                
                }
                
        }
    
/* Class to store the connecting edges of a node with other nodes.
 * vertex1 is the current node in question and vertex2 is the node it is connected to.
 * Cost is their respective cost.
 */
class Edge {
        private Node vertex1, vertex2; //objects of the node class
        private int cost;
        
        public Edge(Node vertex1, Node vertex2)
        {
                this(vertex1, vertex2, Integer.MAX_VALUE);
        }
        public Edge(Node vertex1, Node vertex2, int cost)
        {
                this.vertex1 = vertex1;
                this.vertex2 = vertex2;
                this.cost = cost;
        }
        public int getCost()
        {
        		return this.cost;
        }
        
        public Node vertex1()
        {
        		return vertex1;
        }
        
        public Node vertex2()
        {
        		return vertex2;
        }
        
        
        
}
/* A redundant class which was created earlier (though still in use for a couple of functions) to store all the nodes of the graph.
 * 
 */
class Node {
        private int Id;
        private boolean visited = false;
        
        
        
        public Node(int ID){ this.Id = ID;}
        
        public boolean IsVisited(){ return visited; }
        
        public int getNodeID() {return Id;}
        public void visit(){visited = true;}
        
        public void unvisit(){visited = false;}
        
        
        
        public boolean equals(Object o) {
                
                if(o != null && o instanceof Node)
                {
                        return this.getNodeID() == ((Node) o).getNodeID();
                }
                
                return false;
        }
        
}

/* Adjacency List class to store all the neighbors of all the n nodes in the graph. 
 * Each object of this class stores the neighbor and its respective cost with the node it belongs to. 
 */

class adjacencyNode {

	private int neighbor;
	private int cost;
	
	
	
	public adjacencyNode(int neighbor,int cost)
	{
		super();
		this.neighbor = neighbor;
		this.cost=cost;
	}
	
	 public int getCost()
     {
     		return this.cost;
     }
     
    
     public int getNeighbor()
     {
     		return this.neighbor;
     }
}


/* This class corresponds to each node in the graph.
 * It has a nodeID and a adjacency list which in turn stores all the neighbors and their respective costs with this node
 */

class tempNode {
	int nodeID;
	ArrayList<adjacencyNode> keepNeighbors = new ArrayList<adjacencyNode>(); //Stores neighbors and their respective cost with the node
	
	 public tempNode(int nodeId)
	 {
         this.nodeID = nodeId;
	 }
 
	 public void addToAdjacencyList(int n, int c){ // adds neighbor and its cost.
 	
         this.keepNeighbors.add(new adjacencyNode(n, c));
 }
}

/* Class to generate the random graph according to the provided documentation.
 * vertex1 is the current vertex
 * vertex2 is the vertex it is connected to
 * cost is the cost between the two.
 * duplicate edges are allowed.
 */ 
class RandomNode {
        private int totalNodes;
        private int totalEdges;
        
        public RandomNode(int nodes, double density)
        {
                totalNodes = nodes;
                totalEdges = (int) ( (density/100) * nodes*(nodes-1)/2);
                System.out.println(" total edges = "+totalEdges);
        }
        
        public UserInput Generate(){
        		Random generator = new Random();
                UserInput graph = new UserInput();
                int cost;
                int vertex1;
                int vertex2;
                // add node to graph
                for(int i = 0; i < totalNodes; i++)
                {
                        Node node = new Node(i);
                        graph.AddNode(node);
                }
                
                //add edge to graph
                int edgecount = 0;
                while(edgecount < totalEdges)
                {
                		
                		cost = generator.nextInt(1000) + 1;
                		vertex1 = generator.nextInt(totalNodes);
                		vertex2 = generator.nextInt(totalNodes);
                        while(vertex2 == vertex1)
                        {
                                vertex1 = generator.nextInt(totalNodes);
                        }
                        Edge e = new Edge(graph.getNodes().get(vertex1), 
                                                                graph.getNodes().get(vertex2), cost);
                        
                        graph.AddEdge(e);
                        //boolean IsAdded = graph.AddEdge(e);
                        //if(IsAdded){
                                edgecount++;
                                System.out.println( "Edges Remaining = "+(totalEdges-edgecount));
                        //}

                }
                
                
               //graph.PrintGraph();
                return graph;
                
                
        }
        
}

/* Class to store the graphs generated from the random scheme and from the user input scheme
 * 
 */

class UserInput {
        private ArrayList<Node> nodes = new ArrayList<Node>();
        private ArrayList<Edge> edges = new ArrayList<Edge>();
        
        public boolean AddNode(Node node){
                if(!nodes.contains(node))
                {        
                        nodes.add(node);
                        
                        return true;
                }
                else 
                {
                        //System.out.println("Duplicate node");
                        return false;
                }
        }
        
        public boolean AddEdge(Edge e)
        {
                if(!edges.contains(e))
                {
                        edges.add(e);
                        return true;
                }
                else 
                        return false;
        }
        
        public ArrayList<Node> getNodes(){ return nodes;}
        
        public ArrayList<Edge> getEdges(){ return edges;}
        
        public boolean RemoveNode(Node node){        
                return        nodes.remove(node);
        }
        
        public boolean RemoveEdge(Edge e){
                return edges.remove(e);
                
        }
        
        
        
                
    public void PrintGraph(){
            for(Node n : nodes)
            {
                    System.out.print("Node: "+n.getNodeID()+", ");
            }
            System.out.println();
            for(Edge e : edges)
            {
                    System.out.print("Edge: "+ e.vertex1().getNodeID() +"->"
                                    +e.vertex2().getNodeID()+" cost: "+e.getCost() +", ");
            }
            
    }
        //get neighbor node of current vertex
        public ArrayList<Edge> getNeighbourNodes(Node node, ArrayList<Edge> remainEdges)
        {
                ArrayList<Edge> neighbor = new ArrayList<Edge>();
                     for(Edge e : remainEdges)
                        {
							
                         if(node.getNodeID() == e.vertex1().getNodeID())
                         {
                                 neighbor.add(e);
                         }
                         if(node.getNodeID() == e.vertex2().getNodeID())
                         {
                        	 	 neighbor.add(e);
                         }
                       
                         
                                
                        }
                //for(int k=0;k<neighbor.size();k++)
                //System.out.println( " Is this right ??? "+neighbor.get(k).vertex1().getNodeID()+"--"+neighbor.get(k).vertex2().getNodeID()+" "+neighbor.get(k).getCost());
                   
                     return neighbor;
               
                	
        }
}
        
 /*Class to calcualte the minimum spanning tree by the simple scheme which take n^2 time.
 * 
 */


class SimpleScheme{
        private UserInput graph; // The generated graph
        private double TotalCost = 0;
        
        
        public SimpleScheme(UserInput randomGraph) { graph = randomGraph;}
        
        public double getTotalCost(){ return TotalCost; }
        
        public int[] getMST(tempNode Node[]){
        	int size = graph.getNodes().size();
        	
        	
        					final int [][] tempArray = new int[size][size];
                	         final int [] cost = new int [size];  //To store the corresponding cost with the node in question.
                	         final int [] mst = new int [size];   // to store the final mst nodes.
                	         final boolean [] visited = new boolean [size];  // to check if the node has been already traversed.
                	  
                	        
                	         for(int i=0;i<size;i++)
                	         {
                	        	 for(int j=0;j<size;j++)
                	        	 {
                	        		 tempArray[i][j] = Integer.MAX_VALUE;
                	        	 }
                	         }
                	         
                	         for(int i=0;i<size;i++)
                	         {
                	        	 for(int j=0;j<Node[i].keepNeighbors.size();j++)
                	        	 {
                	        		 if(tempArray[i][Node[i].keepNeighbors.get(j).getNeighbor()] > Node[i].keepNeighbors.get(j).getCost())
                	        		tempArray[i][Node[i].keepNeighbors.get(j).getNeighbor()] = tempArray[Node[i].keepNeighbors.get(j).getNeighbor()][i] = Node[i].keepNeighbors.get(j).getCost(); 
                	        	 }
                	         }
                	         
                	        
                	         
                	         for (int i=0; i<cost.length; i++) {
                	           cost[i] = Integer.MAX_VALUE;
                	        }
                	        cost[0] = 0;
                	 
                	        long lol = System.currentTimeMillis();
                	        for (int i=0; i<cost.length; i++) {
                	           final int next = minNode (cost, visited);
                	          System.out.println(next+" "+i);
                	          
                	          visited[next] = true;
                	          TotalCost += cost[next];
                	        
                	          int[] n = new int[size];
                	          n = tempArray[next];
                	          
                	          for (int j=0; j<size; j++) { //Traversing the neighbors of the current min cost node.
                	              final int v = j;
                	              final int d = n[j];
                	             if (cost[v] > d) {
                	                 cost[v] = d;
                	                 mst[v] = next;
                	              }
                	           }
                	          
                	        }
                	        lol = System.currentTimeMillis()-lol;
                	        System.out.println("\nTime Taken By Simple Scheme = "+lol);
                	        
                	        return mst;
                	    }
                	  


						private static int minNode (int [] dist, boolean [] v) { //to find the current minimum node
                	        int x = Integer.MAX_VALUE;
                	        int y = 1;   // graph not connected, or no unvisited vertices
                	        for (int i=0; i<dist.length; i++) {
                	           if (!v[i] && dist[i]<x) {y=i; x=dist[i];}
                	        }
                	       return y;
						}

                	  
}                    
       
/* Class to store all the nodes of the fibonacci heap.
 * Objects of this class are used to work with the fibonacci heap datastructure.
 */

class Fnode
{

    Fnode child;

     // left sibling node
    Fnode left;

    // parent node
    Fnode parent;

    // right sibling node
    Fnode right;

    // key value for this node
    int key;
    int data;

    // number of children of this node (does not count grandchildren)
    int degree;

    // true if this node has had a child removed since this node was added to
    // its parent
    boolean mark;

    public Fnode(int data, int key)
    {
        right = this;
        left = this;
        this.data = data;
        this.key = key;
    }
}

class FibonacciHeap
{
	private UserInput graph1;
    private Fnode min1;
    private double totalCost;
    

    private int nodeCount;//total node count

    //Initialize empty heap
    public FibonacciHeap(UserInput randomGraph)
    { 
    	this.graph1 = randomGraph;
    	
    }
    
    public double getTotalCost()
   {
    	return totalCost;
    }
    
    public FibonacciHeap()
    {
        this.min1 = null;
        this.nodeCount =0;
    }

    public boolean isEmpty()
    {
        return this.min1 == null;
    }

    /**
     * Decrease key of a node.
     */
    public void decreaseKey(Fnode current, int keyToChange)
    {
        current.key = keyToChange;

        Fnode p = current.parent;

        //When it voilates heap property other do nothing.
        if ((p != null) && (current.key < p.key)) {
            cut(current, p); // cut d and put it in root list
            cascadingCut(p);// cut all nodes that are marked false stop at the first unmarked one encountered and mark true.
        }
        //if(d.parent != null)
        if (current.key < this.min1.key) {
            this.min1 = current;
        }
    }

    /** Just insert in doubly linked list at root
     * change root if inserted key is less than current root
     * node: node to insert
     */
    public void insert(Fnode node)
    {
    	//changed right left
        if (this.min1 != null) {
            node.left = this.min1;
            node.right = this.min1.right;
            this.min1.right = node;
            node.right.left = node;

            if (node.key < this.min1.key) {
                this.min1 = node;
            }
        } else {
            this.min1 = node;
        }

        this.nodeCount++;
    }

    public Fnode remove(Fnode node){
		if(node.parent!=null && node.parent.child == node){
			if(node.right == node)
				node.parent.child = null;
			else
				node.parent.child = node.right;
		}
		if(node.parent!=null){
			node.parent.degree--;
			node.parent = null;
		}
		node.left.right = node.right;
		node.right.left = node.left;
		node.right = node;
		node.left = node;
		return node;
	
	}
    /** Remove min key from Heap.
     *
     */
    public Fnode removeMin()
    {
    	
        Fnode tempNode = this.min1;

        if (tempNode != null) {
            Fnode current = tempNode.child;
            Fnode tempRight;
            
            // for each child of tempH do...
            for(int i=tempNode.degree;i>0;i--) {
                tempRight = current.right;

                // remove current from child list
                current.left.right = current.right;
                current.right.left = current.left;

                // add current to root list of heap
                current.left = this.min1;
                current.right = this.min1.right;
                this.min1.right = current;
                current.right.left = current;

                // set parent[current] to null
                current.parent = null;
                current = tempRight;
            }

            // remove tempH from root list of heap
            tempNode.left.right = tempNode.right;
            tempNode.right.left = tempNode.left;

            if (tempNode == tempNode.right) {
                this.min1 = null;
                
            } 
                this.min1 = tempNode.right;
                consolidate(); // pair heaps if neccassary
            

            // decrement size of heap
            this.nodeCount--;
        }

        return tempNode;
    }


    /**
     * Travel above, cut all nodes that are marked true and add them to root list
     * stop at first unmarked node and mark it true.
     */
    private void cascadingCut(Fnode node)
    {
        Fnode tempParent = node.parent;

        // if there is a parent...
        if (tempParent != null) {
            // if y is unmarked, set it marked
            if (!node.mark) {
                node.mark = true;
            } else {
                // it's marked, cut it from parent
                cut(node, tempParent);

                // cut its parent as well
                cascadingCut(tempParent);
            }
        }
    }

    /**
     * Combine/Pair nodes with same degrees after remove min.
     * algo described in cormen.
     */

    private void consolidate()
    {
        if(this.min1 == null)
            return;

        // limit for possible degrees based on Fibonacci gloden ratio.
        final double phi = (1.0 + Math.sqrt(5.0)) / 2.0;

        int degreeCount = (int) Math.floor(Math.log(this.nodeCount) / Math.log(phi));

        Fnode[] degreeIndex=new Fnode[degreeCount+1]; // degree index

        for (int i = 0; i < degreeCount; i++) {
            degreeIndex[i] = null;
        }
        int rootCount = 0; //nodes in root list.
        Fnode tempMin = this.min1;

        if (tempMin != null) {
            rootCount++;
            tempMin = tempMin.right;

            while (tempMin != this.min1) {
                rootCount++;
                tempMin = tempMin.right;
            }
        }

        // For each node in root list do...
        while (rootCount > 0) {
            // Access this node's degree..
            int degree = tempMin.degree;
            Fnode next = tempMin.right;

            // go until u find node of same degree same degree.
            while (degreeIndex[degree]!=null) {
                Fnode currentDegree = degreeIndex[degree];

                if (tempMin.key > currentDegree.key) {
                    Fnode temp = currentDegree;
                    currentDegree = tempMin;
                    tempMin = temp;
                }

                union(currentDegree, tempMin); //pairwise combine nodes with same degree

                // set degree to null , go to next one.
                degreeIndex[degree]= null;
                degree++;
            }

            // Save this node for later when we might encounter another
            // of the same degree.
            degreeIndex[degree]= tempMin;

            tempMin = next;
            rootCount--;
        }

        // Reintialize tree from dIndex
        this.min1 = null;

        for (int i = 0; i < degreeCount; i++) {
            Fnode y = degreeIndex[i];
            if (y!= null) {
                if (this.min1 != null) {
                    // First remove node from root list.
                    y.left.right = y.right;
                    y.right.left = y.left;

                    // Now add to root list, again.
                    y.left = this.min1;
                    y.right = this.min1.right;
                    this.min1.right = y;
                    y.right.left = y;

                    // Check if this is a new min.
                    if (y.key < this.min1.key) {
                        this.min1 = y;
                    }
                } else {
                    this.min1 = y;
                }
            }
        }
    }

    /**
     * Cut the node and put it in root list.
     * node: to be removed
     * paren: parent node to node
     */
    private void cut(Fnode node, Fnode paren)
    {
        // remove x from childlist of y and decrease degree[y]
        node.left.right = node.right;
        node.right.left = node.left;
        paren.degree--;

        // reset paren.child if necessary
        if (paren.child == node) {
            paren.child = node.right;
        }

        if (paren.degree == 0) {
            paren.child = null;
        }

        // add node to root list of heap
        node.left = this.min1;
        node.right = this.min1.right;
        this.min1.right = node;
        node.right.left = node;

        // set parent[node] to nil
        node.parent = null;

        // set mark[node] to false
        node.mark = false;
    }

    /**
     *  Pair/combine two node in root list during consolidate
     *  operation.
     * less node to become child
     * great x node to become parent
     */
    private void union(Fnode less, Fnode great)
    {
        // remove less from root list of heap
        less.left.right = less.right;
        less.right.left = less.left;

        // make less a child of great
        less.parent = great;

        if (great.child == null) {
            great.child = less;
            less.right = less;
            less.left = less;
        } else {
            less.left = great.child;
            less.right = great.child.right;
            great.child.right = less;
            less.right.left = less;
        }

        // increase degree[great] and mark as false as it is in
        // root list.
        great.degree++;
        less.mark = false;
    }

   

	public ArrayList<Edge> getMST(tempNode Node[]) { //To compute the Minimum spanning tree with fibonacci heap.
		
		 
		boolean[] isVisited = null; //to check if the node has already been traversed.
		isVisited = new boolean[this.graph1.getNodes().size()];
		
		 
	        Fnode[] t;
	        t = new Fnode[this.graph1.getNodes().size()]; // initializing heap nodes.
	        for(int i=0;i<this.graph1.getNodes().size();i++){
	        	isVisited[i] = false;
	           t[i] = new Fnode(i,Integer.MAX_VALUE);
	           this.insert(t[i]);
	           
	        }
	        
	        this.decreaseKey(t[0] , 0);
	        
	        for(int i=0;i<this.graph1.getNodes().size();i++)
	        {
	            Fnode v = this.removeMin(); //removing node with the minimum key.
	            isVisited[v.data] = true;
	            System.out.println(v.data);
	            totalCost += v.key;
	           
	            //Node[] is the ArrayList which is a part of the adjacency list data structure. It keeps all the neighbors of the current node with its cost.
	            for(int j=0;j < Node[v.data].keepNeighbors.size();j++) //traversing all the neighbors of the  current minimum node in the graph.
                {
                	
                	if(!isVisited[Node[v.data].keepNeighbors.get(j).getNeighbor()])
                	{
                		if(Node[v.data].keepNeighbors.get(j).getCost() < t[Node[v.data].keepNeighbors.get(j).getNeighbor()].key)
                		{
                			//Decreasing key of the node if its cost to its neighbor is less than its current key.
                			this.decreaseKey(t[Node[v.data].keepNeighbors.get(j).getNeighbor()], Node[v.data].keepNeighbors.get(j).getCost()); 
                			
                		}
                	}        
                }
	        }
	      //  System.out.println(totalCost);
	        return null; //can be changed to return the nodes of the minimum spanning tree as generated.
	}

}

class HeapNode implements Comparable {

    public int vertex;
    public int tailVertex;
    public int weight;

    /**
     * Constructor.
     *
     *  vertex The vertex to store.
     *  tailVertex The tail vertex.
     *  weight The weight between tailVertex and vertex.
     */
    public HeapNode(int vertex, int tailVertex, int weight) {
        this.vertex = vertex;
        this.tailVertex = tailVertex;
        this.weight = weight;
    }
    
    /**
     * Override compareTo
     * @param obj   The object to compare to
     * @return
     */
    public int compareTo(Object obj) {
        if (!(obj instanceof HeapNode)) {
            throw new ClassCastException("A HeapNode object expected.");
        }
        if (this.weight > ((HeapNode) obj).weight) {
            return 1;
        } else if (this.weight == ((HeapNode) obj).weight) {
            return 0;
        }
        return -1;
    }
}

class MinHeap {

    public int position; // last free position in heap
    public HeapNode[] nodes; // heap 's elements
    public int[] totalPositions; // key is the vertex, value is position in heapNodes

    /**
     * Constructor.
     *
     * heapDimension Heap 's dimension.
     */
    public MinHeap(int totalNodes) {
        this.position = 0;
        this.nodes = new HeapNode[totalNodes];
        this.totalPositions = new int[totalNodes];
        for (int i = 0; i < totalNodes; i++) {
            this.nodes[i] = null;
            this.totalPositions[i] = -1;
        }
    }

    /**
     * Inserts a new element into heap.
     *
     * hn The element to insert.
     * Exception If heap is full.
     */
    public void insert(HeapNode node) throws Exception {
        if (this.position == nodes.length) {
            throw new Exception("Heap overflow");
        }
        this.nodes[this.position] = node;
        this.totalPositions[node.vertex] = this.position;
        int childPos = this.position, parentPos = (childPos - 1) / 2;
        HeapNode aux;
        while (parentPos >= 0 && this.nodes[parentPos].compareTo(this.nodes[childPos]) > 0) {
            aux = this.nodes[parentPos];
            this.nodes[parentPos] = this.nodes[childPos];
            this.nodes[childPos] = aux;
            this.totalPositions[this.nodes[childPos].vertex] = childPos;
            this.totalPositions[this.nodes[parentPos].vertex] = parentPos;
            childPos = parentPos;
            parentPos = (childPos - 1) / 2;
        }
        this.position++;
    }

    /**
     * Extracts min value (situated on first position in vector).
     * 
     * The heap node with minimal shortest path.
     * Exception If heap is empty.
     */
    public HeapNode extractMin() throws Exception {
        if (this.position == 0) {
            throw new Exception("Empty heap");
        }
        // It 's basicly a deletion from the first position without needing to bubble up
        HeapNode hn = this.nodes[0], aux;
        this.nodes[0] = this.nodes[this.position - 1];
        this.totalPositions[this.nodes[0].vertex] = 0;
        this.nodes[this.position - 1] = null;
        this.totalPositions[hn.vertex] = -1;
        this.position--;
        
        /* bubble down */
        int parentPos = 0, leftChildPos = (parentPos + 1) * 2 - 1, rightChildPos = leftChildPos + 1, minChildPos;
        while ((leftChildPos < this.position && this.nodes[parentPos].compareTo(this.nodes[leftChildPos]) > 0)
                || (rightChildPos < this.position && this.nodes[parentPos].compareTo(this.nodes[rightChildPos]) > 0)) {
            minChildPos = leftChildPos;
            if (rightChildPos < this.position && this.nodes[leftChildPos].compareTo(this.nodes[rightChildPos]) > 0) {
                minChildPos = rightChildPos;
            }
            aux = this.nodes[parentPos];
            this.nodes[parentPos] = this.nodes[minChildPos];
            this.nodes[minChildPos] = aux;
            this.totalPositions[this.nodes[minChildPos].vertex] = minChildPos;
            this.totalPositions[this.nodes[parentPos].vertex] = parentPos;
            parentPos = minChildPos;
            leftChildPos = (parentPos + 1) * 2 - 1;
            rightChildPos = leftChildPos + 1;
        }
        return hn;
    }

    /**
     * Deletes an element from heap.
     *
     * pos The element 's position in heap.
     * The deleted element.
     * Exception If position is out of permitted bounds.
     */
    public HeapNode delete(int pos) throws Exception {
        if (pos < 0 || pos >= this.position) {
            throw new Exception("Invalid position");
        }
        HeapNode node = this.nodes[pos], aux;
        this.nodes[pos] = this.nodes[this.position - 1];
        this.totalPositions[this.nodes[pos].vertex] = pos;
        this.nodes[this.position - 1] = null;
        this.totalPositions[node.vertex] = -1;
        this.position--;
        
        int parentPos = (pos - 1) / 2, childPos = pos;
        if (parentPos >= 0 && null != this.nodes[childPos] && this.nodes[parentPos].compareTo(this.nodes[childPos]) > 0) { // bubble up
            while (parentPos >= 0 && this.nodes[parentPos].compareTo(this.nodes[childPos]) > 0) {
                aux = this.nodes[parentPos];
                this.nodes[parentPos] = this.nodes[childPos];
                this.nodes[childPos] = aux;
                this.totalPositions[this.nodes[childPos].vertex] = childPos;
                this.totalPositions[this.nodes[parentPos].vertex] = parentPos;
                childPos = parentPos;
                parentPos = (childPos - 1) / 2;
            }
        } else { // bubble down
            parentPos = pos;
            int leftChildPos = (parentPos + 1) * 2 - 1, rightChildPos = leftChildPos + 1, minChildPos;
            while ((leftChildPos < this.position && this.nodes[parentPos].compareTo(this.nodes[leftChildPos]) > 0)
                    || (rightChildPos < this.position && this.nodes[parentPos].compareTo(this.nodes[rightChildPos]) > 0)) {
                minChildPos = leftChildPos;
                if (rightChildPos < this.position && this.nodes[leftChildPos].compareTo(this.nodes[rightChildPos]) > 0) {
                    minChildPos = rightChildPos;
                }
                aux = this.nodes[parentPos];
                this.nodes[parentPos] = this.nodes[minChildPos];
                this.nodes[minChildPos] = aux;
                this.totalPositions[this.nodes[minChildPos].vertex] = minChildPos;
                this.totalPositions[this.nodes[parentPos].vertex] = parentPos;
                parentPos = minChildPos;
                leftChildPos = (parentPos + 1) * 2 - 1;
                rightChildPos = leftChildPos + 1;
            }
        }
        return node;
    }
}