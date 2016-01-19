package philosophers;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Fork {
	private Lock lock; //locks for Each fork.
	private volatile boolean available; // Thread Safe variable to check if the fork is available
	
	public Fork()
	{
		available = true;
		lock = new ReentrantLock();
	}
	
	//Locks on pickup
	public void pickup()
	{
		available = false;
		lock.lock();
	}
	
	//unlocks when put down.
	public void putDown()
	{
		lock.unlock();
		available = true;
	}
	
	// Checks to see if the fork is available
	public boolean tryPickup()
	{
		return available;
	}

}
