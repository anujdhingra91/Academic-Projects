package philosophers;

// Class implementing the Data Structure for checking the correctness of the program.

public class checker {
	
	public int rightFork;
	public int leftFork;
	public boolean eating;
	public boolean thinking;
	
	public checker(int right, int left) {
		
		this.rightFork = right;
		this.leftFork = left;
		this.eating = false;
		this.thinking = false;
	}
	
}
