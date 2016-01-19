package philosophers;

/* The sufficiency of Logger Interface is modified by :
 * Adding 2 more functions which traces the actions performed by the philosopher with respect to both the forks.
 * The actions performed by the forks are Trying to get the fork, getting the fork and putting down the fork.
 */

public interface Logger {
	boolean isCorrect();
	public void eats(int seat);
	public void thinks(int seat);
	public void actionPerformedRightFork(int seat, int i, int rightFork);
	public void actionPerformedLeftFork(int philosopher, int i, int leftFork);
}
