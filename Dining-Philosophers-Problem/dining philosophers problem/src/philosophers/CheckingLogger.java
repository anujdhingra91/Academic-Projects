package philosophers;

import java.util.ArrayList;
import static java.lang.System.out;

public class CheckingLogger implements Logger{
	volatile ArrayList<checker> correctnessChecker;
	private final int size;
	volatile boolean isCorrect = true; // This makes the class Thread Safe
	
	public CheckingLogger(int size)
	{
		
		correctnessChecker = new ArrayList<checker>();
		for(int i=0;i<size;i++)
		{
			correctnessChecker.add(new checker(-1,-1));
		}
		//isCorrect = true;
		this.size = size;
	}
	
	
	
	/* The Following function checks the correctness of the program.
	 * When the philosopher starts eating, it first checks if the left fork and right fork numbers are appropriate for the philosopher. If not it turns the is isCorrect variable false.
	 * Secondly, it checks that the neighbor Philosophers are not eating simultaneously as him. If anyone of them is then it turns isCorrect to false.
	 * 
	 */
	@Override
	public void eats(int philosopher) {
		
		correctnessChecker.get(philosopher).eating = true;
		
		out.println("Philosopher " + philosopher + " is eating");
		if(correctnessChecker.get(philosopher).leftFork != philosopher || correctnessChecker.get(philosopher).rightFork != (philosopher+1)%getSize())
			isCorrect =  false;
		if(correctnessChecker.get((philosopher+(getSize() - 1))%getSize()).eating || correctnessChecker.get((philosopher+1)%getSize()).eating)
			isCorrect =  false;
		
		correctnessChecker.get(philosopher).eating = false;
		
	}

	@Override
	public void thinks(int philosopher) {
		
		correctnessChecker.get(philosopher).thinking = true;
		out.println("Philosopher " + philosopher + " is thinking");
		correctnessChecker.get(philosopher).thinking = false;
	}

	@Override
	public boolean isCorrect() {
		//out.println(" (WARNING:  Correctness not checked) ");
		return isCorrect;
	}

	private int getSize() {
		return size;
	}

	/* Following 2 functions perform the additional functionality with respect to the forks as explained Earlier
	 * (non-Javadoc)
	 * @see philosophers.Logger#eats(int)
	 */
	
	@Override
	public void actionPerformedRightFork(int philosopher, int i, int rightFork) {
		
		if(i==1){
			out.println("Philosopher "+philosopher+ " tries to take "+rightFork);
			return;
			}
		else if(i==2)
		{
			out.println("Philosopher "+philosopher+ " takes fork "+rightFork);
			correctnessChecker.get(philosopher).rightFork = rightFork;
			return;
		}
		else
		{
			out.println("Philosopher "+philosopher+ " puts down fork "+rightFork);
			correctnessChecker.get(philosopher).rightFork = -1;
			return;
		}
		
	}

	@Override
	public void actionPerformedLeftFork(int philosopher, int i, int leftFork) {
		
		if(i==1){
			out.println("Philosopher "+philosopher+ " tries to take "+leftFork);
			return;
			}
		else if(i==2)
		{
			out.println("Philosopher "+philosopher+ " takes fork "+leftFork);
			correctnessChecker.get(philosopher).leftFork = leftFork;
			return;
		}
		else
		{
			out.println("Philosopher "+philosopher+ " puts down fork "+leftFork);
			correctnessChecker.get(philosopher).leftFork = -1;
			return;
		}
		
	}

}