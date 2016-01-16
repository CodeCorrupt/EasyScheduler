import java.util.Scanner;


public class Scheduler {

	public static void main (String[] args)
	{
		Scanner input = new Scanner(System.in);
		//Get the student's information
		System.out.print("Please enter your PID without the starting letter: ");
		int PID = input.nextInt();
		Student user = new Student(PID);
		System.out.println("\n\nHello" + user.getFull() + "!");
		System.out.println("Please select a semester you want to sign up for:");
		System.out.println("1: Fall\n2: Spring\n3: Summer");
		
		input.close();
	}
}
