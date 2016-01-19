package philosophers;

import static java.lang.System.out;

public class PrintLogger implements Logger{

	@Override
	public void eats(int philosopher) {
		out.println("Philosopher " + philosopher + " is eating");
	}

	@Override
	public void thinks(int philosopher) {
		out.println("Philosopher " + philosopher + " is thinking");
	}

	@Override
	public boolean isCorrect() {
		out.println(" (WARNING:  Correctness not checked) ");
		return true;
	}

	@Override
	public void actionPerformedLeftFork(int philosopher,int i,int leftFork) {
		if(i==1){
		out.println("Philosopher "+philosopher+ " tries to take "+leftFork);
		}
		else if(i==2)
			out.println("Philosopher "+philosopher+ " takes fork "+leftFork);
		else
			out.println("Philosopher "+philosopher+ " puts down fork "+leftFork);
		
		
	}

	@Override
	public void actionPerformedRightFork(int philosopher, int i, int rightFork) {
		if(i==1){
			out.println("Philosopher "+philosopher+ " tries to take "+rightFork);
			}
			else if(i==2)
				out.println("Philosopher "+philosopher+ " takes fork "+rightFork);
			else
				out.println("Philosopher "+philosopher+ " puts down fork "+rightFork);	
	}

}
