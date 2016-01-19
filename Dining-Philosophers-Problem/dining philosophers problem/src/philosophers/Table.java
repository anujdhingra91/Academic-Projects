package philosophers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.System.out;

/* The following program solves the Dining Philosophers problem.
 * By Anuj Dhingra
 * UFID : 0899-6498
 * Course ID : COP5618 Concurrent Programming.
 * Description : The class table implements the table across which all the philosophers are sitting with a fork on both the sides.
 * The Data structure used for implementing forks is an ArrayList. The Fork class is provided with locks which is acquired and released by the philosopher if it is available in the first place.
 * A Thread safe CheckingLogger class is provided which logs all the information for all the actions performed by the philosophers i.e. thinking,trying to get both the forks,getting them,eating and putting them down.
 * The Data structure used for checking the program behavior is an ArrayList which stores the actions and fork numbers of each Philosopher. 
 * The Logger Interface is modified to include the functions for trying, getting and putting down the forks.  
 * The program is data race free by making the shared variables volatile and shared classes Thread safe so that when a thread makes a change to a shared variable, it is seen by the others and the locking functionality is also implemented.
 * The Lock acquired by the philosopher for the left fork is released if the philosopher is not able to get the right fork after that.
*/ 

public class Table {
	int numSeats; // The number of seats allocated for the philosophers.
	Thread[] phils;  // Each philosopher is represented as a thread.
	final Random r = new Random();
	final int  timesToEat; // For the number of iterations of each Thread
	static final int MAXMSECS = 1000;
	//final Logger log;
	final Logger log; // An instance of checking logger class.
	private volatile List<Fork> Forks; // ArrayList for the number of Forks made Thread Safe.
	volatile boolean isCancelled = false; // For Stopping the Threads once the time expires.
	
	class Philosopher implements Runnable{
		final int seat;  //where this philosopher is seated, seats numbered from 0 to numSeats-1
	    int timesToEat;  // Not useful when the second argument (args[1]) is number of seconds.
		
	    
		Philosopher(int seat, int timesToEat){
			this.seat = seat;
			this.timesToEat = timesToEat;
		}
		
		void think(){
			log.thinks(seat);
			try{
				Thread.sleep(r.nextInt(MAXMSECS));
			}
			catch(InterruptedException e){
				/*ignore*/
			}
		}
		

		void eat(){
			log.eats(seat); // call to CheckingLogger class which checks the correctness of the program.
			//checkerLog.eats(seat);
			try{
				Thread.sleep(r.nextInt(MAXMSECS));
			}
			catch(InterruptedException e){
				/*ignore*/
			}			
		}
			
		public void run(){
			/*for (;timesToEat > 0; --timesToEat){
				think();	
				if(tryPickup()){
				eat();
				getLeft(3).putDown();
				getRight(3).putDown();
				}*/
			// Infinite loop for the Philosophers until the time expires in which case the isCancelled variable is made to be true.
			while(!isCancelled){
				try{
					Thread.sleep(10000);
				}
				catch(InterruptedException e){
					/*ignore*/
				}			
				think();	
				if(tryPickup()){ // Tries to pickup both the forks
				eat();
				getLeft(3).putDown(); // puts down left fork
				getRight(3).putDown(); // puts down right fork
				}
			}
		}

		/* Following 2 functions implement the actions performed on Forks.
		 * Value of variable i =
		 * 1 When a Philosopher tries to pick fork.
		 * 2 When a Philosopher gets the Fork.
		 * 3 When a Philosopher puts the Fork down.
		 */

		private Fork getLeft(int i) {
		
				log.actionPerformedLeftFork(seat,i,leftFork()); 
					return Forks.get(leftFork());	
		}
		
		private Fork getRight(int i) {
			
			log.actionPerformedRightFork(seat,i,rightFork());
			return Forks.get(rightFork());
		}
		
		//The Left Fork number is same as the seat number
		private int leftFork() {
		 return seat;
			
		}
		
		//The right Fork number is 1 + the seat number.
		private int rightFork(){
			 int fork;
		        if(seat == numSeats - 1) {
		            fork = 0;
		        } else {
		            fork = seat + 1;
		        }
		        return fork;
		}
/*		
 *     DEADLOCK SAFE.
		Here the philosopher tries to pick the left fork first if it is available else it returns false and iteration ends.
		 * If the left fork is available, it is picked and availability of right Fork is checked.
		 * If the right Fork is unavailable, the philosopher puts down the left fork -- CONDITION TO AVOID DEADLOCK.
		 * If the right fork is available, it gets picked and the philosophers goes on to eat the spaghetti. 
		 */
		private boolean tryPickup() {
			if (!getLeft(1).tryPickup()) {
				 return false;
				 }
					getLeft(2).pickup();
				if (!getRight(1).tryPickup()) {
				 getLeft(3).putDown();
				 return false;
				}
				getRight(2).pickup();
				return true;
		}
		
		

		
	}
	
	Table(int numSeats, int timesToEat, Logger log) throws InterruptedException{
		Forks = new ArrayList<Fork>(); 
		// initialize the Fork ArrayList.
		for (int i = 0; i < numSeats; i++) {
            Forks.add(new Fork());
        }
		this.numSeats = numSeats;  //set the number of seats around the table.  Must be at least 2
		this.timesToEat = timesToEat;  //number of times each philosopher should eat
		this.log = log;
		phils = new Thread[numSeats];  //create a Thread for each philosopher
		for (int i = 0; i < numSeats; i++) phils[i] = new Thread(new Philosopher(i, timesToEat));	
	}
	
	
	void startDining(){
		for (int i = 0; i < numSeats; i++) phils[i].start();
	}
	
	void closeRestaurant() throws InterruptedException{
		//for(int i=0; i<numSeats; i++) 
			//{
			isCancelled = true; // Turned True when the time specified by the user Expires. This also ensures that the philosophers finish eating gracefully.
			//phils[i].cancel();
			//phils[i].join();
			//}
		//for (int i = 0; i < numSeats; i++) phils[i].join();
	}
	
	
	
	public static void main(String[] args) throws InterruptedException{
		if (args.length < 2){
			out.println("usage:  java Table numSeats timesToEat");
			return;
		}
		int numPhils = Integer.parseInt(args[0]);
		//int timesToEat = Integer.parseInt(args[1]);
		int seconds = Integer.parseInt(args[1]) * 1000;
		//Logger log = new PrintLogger();
		Logger log = new CheckingLogger(numPhils); //CheckingLogger Instance.
		//Table table = new Table(numPhils, timesToEat, log);
		Table table = new Table(numPhils, seconds, log);
		table.startDining();
		
		//The Main thread is made to sleep for the number of seconds specified by the user.
		try{
			Thread.sleep(seconds);
		}
		catch(InterruptedException e){
			/*ignore*/
		}
		table.closeRestaurant(); // The philosophers can complete eating gracefully even when the restaurant closes.
	    System.out.println("restaurant closed.  Behavior was " + (log.isCorrect()?"correct":"incorrect"));
	}
}


