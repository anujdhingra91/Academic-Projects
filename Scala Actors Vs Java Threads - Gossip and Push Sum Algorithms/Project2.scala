import akka.actor._
import scala.util.Random


case class Identity(uniqueId:Int, actors:List[ActorRef], neighbors:List[Int], maxNoOfRumours:Int, superVisor:ActorRef)
case class Gossip(Id:Int)
case class Done(nodeId:Int,numNodes:Double)
case class KeepTime(b:Long)
case class pushSum(sum:Double,weight:Double)
case class PushSumDone(nodeId:Int,numNodes:Double,value:Int)

case class Nodes() extends Actor {
  
   var maxRumours: Int = 0
   var nodeId:Int = 0
   var neighbors:List[Int] = Nil
   var actors:List[ActorRef] = Nil
   var superVisor:ActorRef = null
   var selfRumourCount:Int = 0
   var count:Int = 0
   var flag :Int = _
   var numNodes:Double = 0
   var rumorCount:Int = 0
   var s:Double = 0
   var w:Double = 0
   
   def receive = {
        case Identity(nodeId:Int, actors:List[ActorRef], neighbors:List[Int], maxNoOfRumours:Int, superVisor:ActorRef) => {
  
            this.superVisor = superVisor
        	this.nodeId = nodeId
        	this.s = nodeId
        	this.w = 1
            this.neighbors = neighbors      
            this.actors = actors
            numNodes = actors.length
        	
        }

   
   case Gossip(rumourId:Int) => {
     
     if(maxRumours < 10)
       {
       if(nodeId != rumourId){
       maxRumours += 1
       if(maxRumours == 1){
       superVisor ! Done(nodeId,numNodes)
       }
       }
       
       //println("Last Rumour sent by " +rumourId+" Total No. Of Rumours received by " +nodeId+ " are " +maxRumours)
       
       var randomNode = Random.nextInt(neighbors.length)
       actors(neighbors(randomNode)) ! Gossip(nodeId)
       self ! Gossip(nodeId)
     }
    
     
     /*if(maxRumours == 10 && nodeId != rumourId)
     {
       //println(" actor "+nodeId+" Terminating")
       superVisor ! Done(nodeId,numNodes)
       context.stop(self)
     }
     
     else if(selfRumourCount == maxRumours)
     {
       count += 1
       if(count == 20)
       {
          //println(" actor "+nodeId+" Terminating")
          superVisor ! Done(nodeId,numNodes)
          context.stop(self)
       }
     }*/
     }
   
   case pushSum(sum:Double, weight:Double) => 
          {
          var condition = math.pow(10, -10)
          rumorCount+=1
          
          var oldRatio:Double = s/w;
          s+=sum
	      w+=weight
	      s=s/2
	      w=w/2
	      var newRatio:Double = s/w
	      var actor_ratio:Double = Math.abs((oldRatio-newRatio))
	      
	      if(rumorCount==1){
	        flag = 0
	        superVisor ! PushSumDone(nodeId,numNodes,flag)
	       }
	       

	      if(rumorCount==1 || actor_ratio > condition) 
          {
        	  count = 0;
        	  
	            var randomPlayer = Random.nextInt(neighbors.length);
	            actors(neighbors(randomPlayer)) ! pushSum(s,w)
	           
	      }
	      
          else
          {
           
            count+=1;
            //println("Count = "+count)
            if(count == 3) 
            {
              //println("Final Condition --- For Actor  "+nodeId+" \tRumor Count  "+rumorCount+" \ts/w: "+(s/w));
              flag = 1
              
              var randomPlayer = Random.nextInt(neighbors.length);
              actors(neighbors(randomPlayer)) ! pushSum(s,w)
              superVisor ! PushSumDone(nodeId,numNodes,flag)
	          self ! PoisonPill
	        }
            
            else
            {
	            var randomPlayer = Random.nextInt(neighbors.length);
	            actors(neighbors(randomPlayer)) ! pushSum(s,w)     
	        }
            
          }
          }
   }
}

object Project2 extends App {
     
   if(args.length != 3)
     println("Invalid Arguments")
     
     else if(args.length == 3)
     {
     var numNodes:Int = args(0).toInt
     var topology:String = args(1)
     var algorithm:String = args(2)
     var i:Int = 0
     var actors:List[ActorRef] = Nil
     
     var b:Long = 0
     val system = ActorSystem("GossipSimulator")
     val maxNoOfRumours = 10
     if(topology == "2d" || topology == "imp2d")
      while(math.sqrt(numNodes.toDouble)%1!=0){
       numNodes += 1
     }
     
     val numNodesRoot = math.sqrt(numNodes.toDouble)
     var superVisor:ActorRef = system.actorOf(Props[supervisor])
     
     while(i<numNodes){
        actors ::=  system.actorOf(Props[Nodes])
        i += 1
      }
   
   if(topology == "full" || topology == "Full")
   {
     var i:Int = 0
          while(i<actors.length){
            var neighbors:List[Int] = Nil
            
            var j:Int = 0
            while(j<actors.length){
              if(j!=i){
                neighbors ::= j
               		}
              j += 1
            }
            actors(i) ! Identity(i, actors, neighbors, maxNoOfRumours, superVisor)
            i += 1
          }
    }
   
   if(topology == "2D" || topology == "2d")
   {
    
     var i:Int = 0
          while(i<actors.length){
            var neighbors:List[Int] = Nil
            
            if((i.toDouble - numNodesRoot) >= 0)
              neighbors ::= ((i-numNodesRoot).toInt)
             if((i.toDouble + numNodesRoot) < actors.length)
             neighbors ::= ((i+numNodesRoot).toInt)
             if((i.toDouble+1) % numNodesRoot != 0)
               neighbors ::= i+1
             if((i.toDouble) % numNodesRoot != 0)
               neighbors ::= i-1  
            
            actors(i) ! Identity(i, actors, neighbors, maxNoOfRumours, superVisor)
            
            i += 1
          }
     }
   
   if(topology == "imp2D" || topology == "imp2d" || topology == "Imp2D" || topology == "Imp2d")
   {
     
     var i:Int = 0
     var j:Int = 0
     var k:Int = 0
     var randomNeighbor:Int = -1
          while(i<actors.length){
            var neighbors:List[Int] = Nil
            
            if((i.toDouble - numNodesRoot) >= 0)
              neighbors ::= ((i-numNodesRoot).toInt)
             if((i.toDouble + numNodesRoot) < actors.length)
             neighbors ::= ((i+numNodesRoot).toInt)
             if((i.toDouble+1) % numNodesRoot != 0)
               neighbors ::= i+1
             if((i.toDouble) % numNodesRoot != 0)
               neighbors ::= i-1  
            
            
            do{
              randomNeighbor = Random.nextInt(actors.length)
              for(k<-neighbors){
                if(randomNeighbor == k) randomNeighbor = -1
              }   
            }while(randomNeighbor == -1)
            
            neighbors ::= (randomNeighbor)
            
            actors(i) ! Identity(i, actors, neighbors, maxNoOfRumours, superVisor)
            
            i += 1
          }
     }
   
   if(topology == "line" || topology == "Line")
   {
          var i:Int = 0;
          while(i<actors.length){
            var neighbors:List[Int] = Nil
            
            if(i>0) neighbors ::= (i-1)
            if(i<actors.length-1) neighbors ::= (i+1)
            
            actors(i) ! Identity(i, actors, neighbors, maxNoOfRumours, superVisor)
            
            i += 1
          }
   }
    if(algorithm == "Gossip" || algorithm == "gossip"){
      b = System.currentTimeMillis
    		  superVisor ! KeepTime(b)
              actors(0) ! Gossip(-1)
     }
     if(algorithm == "push-sum" || algorithm == "Push-sum" || algorithm =="Pushsum" || algorithm == "pushsum"){

      b = System.currentTimeMillis
      superVisor ! KeepTime(b)
      
      var randomActor = Random.nextInt(actors.length);
	  actors(randomActor) ! pushSum(0,1)
	  
    }
    }
}


case class supervisor() extends Actor{
  
  var count:Double = 0
  var b:Long = 0
  var f:Int = 0
  var sumCheck:Int = 0
  
  def receive = {
    
    case KeepTime(b:Long) => {
      
      this.b = b
    }
    
    case Done(finished:Int,numNodes:Double) => {
      count += 1
      
      if(count == numNodes){
      //println(" All nodes Terminated ")
      println(" Time Taken = "+(System.currentTimeMillis()-b))
      sys.exit()
     }
    }
    
   case PushSumDone(actorId:Int,numNodes:Double,value:Int) => {
     this.f = value
      if(f==0)
      {
        count+=1    
      }
      if(f==1)
      {
        sumCheck = f
      }
      //if(count/numNodes && sumCheck == 1)             uncomment this if convergence not reached.
      if(count==numNodes && sumCheck==1){
      //println("All nodes heard rumor and s/w changed more than 3 consecutive rounds")  
      println(" Time Taken = "+(System.currentTimeMillis()-b))
      sys.exit();
      }
   }
  }
}
