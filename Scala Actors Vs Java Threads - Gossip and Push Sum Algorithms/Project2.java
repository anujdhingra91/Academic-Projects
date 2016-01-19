import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;




public class project2 implements Runnable{
	private int id;
	private double s;
	private double w;

		project2(int id)
		{
			this.id=id;
			this.s = id;
			this.w = 1;
	    }	
		static ArrayList<Long> input = new ArrayList<Long>();
		static ArrayList<Long> output = new ArrayList<Long>();
		static int numThreads;
		static double numNodes,numNodesRoot;
		static int maxRumours,rumourCount;
		static int selfRumourCount;
		static boolean exit1 = false;
		static long N;
		static int nodeId;
		static String topology,algorithm;
		static int first;
		static List<Queue<Integer>> queues = new ArrayList<Queue<Integer>>();
		static List<Queue<double[]>> queuespushsum = new ArrayList<Queue<double[]>>();
		static Set<Object> sendList=new HashSet<Object>();
		static Set<Object> deleteList=new HashSet<Object>();
		static Set<Integer> not_received_any=new HashSet<Integer>();		
	
	public static void main(String[] args) {

		if(args.length==3)
		{
			numNodes=Integer.parseInt(args[0]);
			topology=args[1];
			algorithm = args[2];
			maxRumours = 10;
			
			if(topology == "2d" || topology == "imp2d")
			      while(Math.sqrt(numNodes)%1!=0){
			       numNodes += 1;
			     }
			numNodesRoot = Math.sqrt(numNodes);
			numThreads = (int) numNodes;
			for(int i=0;i<numThreads;i++){
				Queue<Integer> q = new LinkedList<Integer>();
				queues.add(q);
			}
			for(int i=0;i<numThreads;i++){
				Queue<double[]> q = new LinkedList<double[]>();
				queuespushsum.add(q);
			}
			Random rand = new Random();
            first = rand.nextInt(numThreads);
            for(int i=0;i<numThreads;i++)
            	not_received_any.add(i);
            long starttime = System.currentTimeMillis();
            gossipSimulator();
            long totaltime = (System.currentTimeMillis() - starttime);
            System.out.println("Time taken: "+totaltime+"ms");
			
		    
		}
		else
			System.out.print("error :: Please provide the value of N and k as command line arguments.");
	}
		
	private static void gossipSimulator() {
		ArrayList<Thread> arr = new ArrayList<>();

		for(int i = 0;i < numThreads ; i++){
		Thread t = new Thread(new project2(i));	
		
		arr.add(t);
		}
		for(int i=0;i<numThreads;i++){
			arr.get(i).start();
		}
		for(int i=0;i<numThreads;i++){
		    try{
		    	arr.get(i).join();
		    
		    }catch(InterruptedException e){
		    	 e.printStackTrace();
		    }
			
		}
	
	}

	@Override
	public void run() {
		 ArrayList<Integer> neighbors = new ArrayList<Integer>();
		 double [] values=new double[2];
		 int rumour = 0;
		 int count = 0;
		 double sum = 0;  
		 double weight = 1;
		 if(topology.equals("full"))
		   {
		     int i = id;
		            
		            int j = 0;
		            while(j<numThreads){
		              if(j!=i){
		                neighbors.add(j);
		               		}
		              j += 1;
		            }
		   }
		if(topology.equals("2d"))
		   {
		    
		    int i = id;
		            
		            if((i - numNodesRoot) >= 0)
		              neighbors.add((int) (i-numNodesRoot));
		             if((i + numNodesRoot) < numThreads)
		             neighbors.add((int) (i+numNodesRoot));
		             if((i+1) % numNodesRoot != 0)
		               neighbors.add((int) (i));
		             if(i % numNodesRoot != 0)
		               neighbors.add((int) (i-1));  
		   }
		   
		   if(topology.equals("imp2d"))
		   {
		     
		     int i = id;
		     int randomNeighbor = -1;
		            
		            if((i - numNodesRoot) >= 0)
		              neighbors.add((int) (i-numNodesRoot));
		             if((i + numNodesRoot) < numThreads)
		             neighbors.add((int) (i+numNodesRoot));
		             if((i+1) % numNodesRoot != 0)
		               neighbors.add(i);
		             if(i % numNodesRoot != 0)
		               neighbors.add(i-1);  
		            
		            
		            do{
		            	Random rand = new Random();
		              randomNeighbor = rand.nextInt(numThreads);
		              
		                if(neighbors.contains(randomNeighbor))
		                	randomNeighbor = -1;
		            }while(randomNeighbor == -1);
		            
		            neighbors.add(randomNeighbor);
		            
		          }
		     
		   
		   if(topology.equals("line"))
		   {
		          int i = id;
		          if(i>0) neighbors.add(i-1);
		          if(i<numThreads-1) neighbors.add(i+1);
		            
		            
		     }
		   if(algorithm.equalsIgnoreCase("gossip"))
		   {
			 
			   if(id==first)
			   {
				   //Incrementing rumor
				   rumour++;
				   //add to sendlist 
				   sendList.add(Thread.currentThread());
				   // remove from not_received_any
				   not_received_any.remove(id);
				}
			   
			   // if not_received_any is empty then exit
			   while(!not_received_any.isEmpty())
			   {
				   //sending the rumour
				   if(sendList.contains(Thread.currentThread()) && rumour<10)
				   {
					    Random rand1 = new Random();
				            int receiver = rand1.nextInt(neighbors.size());
				            synchronized(queues.get(neighbors.get(receiver))){
				            	queues.get(neighbors.get(receiver)).add(id);
				             }
				            synchronized(queues.get(id)){
				            	queues.get(id).add(id);
				            }
				      
					}
				   
				   // receiving the rumour and updating the rumour count 
				   if(!queues.get(id).isEmpty())
				   {
					   int received;
					   synchronized(queues.get(id)){
					   received = queues.get(id).poll();
					   }
					  
					 //Incrementing rumor
					   if(received != id)
					   rumour++;

					  System.out.println("");
					   
					   //adding to delete list if rumour exceeds 10
					   if(rumour==10){
						   deleteList.add(Thread.currentThread()) ;
						   sendList.remove(Thread.currentThread());
					   }
					   
					   //adding if new element 
					   if(!((sendList.contains(Thread.currentThread()))&& (deleteList.contains(Thread.currentThread()))))
						{
							   sendList.add(Thread.currentThread()) ;
							   not_received_any.remove(id);
						}
					}
			   }
		   }
		   else if(algorithm.equalsIgnoreCase("pushsum")|| algorithm.equalsIgnoreCase("push-sum"))
		   {
			 
			   if(id==first)
			   {
				   //Condition initialization
				   s +=sum;
				   w+=weight;
				   s = s/2;                 
				   w = w/2;   
				   Random rand1 = new Random();
			       int receiver = rand1.nextInt(neighbors.size());
			       values[0] = s;
			       values[1] = w;
			       synchronized(queuespushsum.get(neighbors.get(receiver))){
			       queuespushsum.get(neighbors.get(receiver)).add(values);
			       }
				   rumour++;     //Incrementing rumor
				   
				  
				   sendList.add(Thread.currentThread());
			       not_received_any.remove(id);  
			   }
			   			 
			   while(!(not_received_any.isEmpty() && exit1))
			     {
				   if(!queuespushsum.get(id).isEmpty())
				       {
					   rumour++;
					   not_received_any.remove(id);
					   if(!sendList.contains(Thread.currentThread()))
						   sendList.add(Thread.currentThread());
					   double condition = Math.pow(10, -10);
					   synchronized(queuespushsum.get(id)){
					   sum = queuespushsum.get(id).peek()[0];
					   weight = queuespushsum.get(id).peek()[1];
					   queuespushsum.get(id).remove();
					   }
					   double oldRatio = s/w;   //Old ratio
					   s +=sum;
					   w +=weight;
					   s = s/2;                 
					   w = w/2;                 
					   double newRatio = s/w;   //New Ratio
					   double actor_ratio = Math.abs(oldRatio - newRatio);
					   if(actor_ratio > condition)
					   {
						   count = 0;
						   
						               
						   Random rand1 = new Random();
					       int receiver = rand1.nextInt(neighbors.size());
					       values[0] = s;
					       values[1] = w;
					       synchronized(queuespushsum.get(neighbors.get(receiver))){
					       queuespushsum.get(neighbors.get(receiver)).add(values);
					       }
						}
					   else
					   {
						   
						   if(!sendList.contains(Thread.currentThread()))
							   sendList.add(Thread.currentThread());
						   count+=1;
						   
						   if(count == 3)
						   {
							   exit1 = true; // check termination condition 
							   Random rand1 = new Random();
						       int receiver = rand1.nextInt(neighbors.size());
						       values[0] = s;
						       values[1] = w;
						       synchronized(queuespushsum.get(neighbors.get(receiver))){
						       queuespushsum.get(neighbors.get(receiver)).add(values);
						       }
						       break;
						   }
						   else
						  {
							                 
							   Random rand1 = new Random();
						       int receiver = rand1.nextInt(neighbors.size());
						       values[0] = s;
						       values[1] = w;
						       synchronized(queuespushsum.get(neighbors.get(receiver))){
						       queuespushsum.get(neighbors.get(receiver)).add(values);
						       }
						   }
					   }
				   }
				   else  if(sendList.contains(Thread.currentThread()))
					 {
						   Random rand1 = new Random();
					       int receiver = rand1.nextInt(neighbors.size());
					       values[0] = s;
					       values[1] = w;
					       synchronized(queuespushsum.get(neighbors.get(receiver))){
					       queuespushsum.get(neighbors.get(receiver)).add(values);
					       }
					 } 
				}
			}
		   else
		   {
			   System.out.println("Enter correct Algorithm.");
		   }
		
	}

}