/*

	Easy Scheduler
	Jessica Carter
	Tyler Hoyt
	COP 3930H

*/

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Scheduler {

	public static void main (String[] args) throws ClassNotFoundException, SQLException
	{
		Student user = null;
		int semester = 0;
		int pid = 0;
		
		ArrayList<Classes> currSemester =  new ArrayList<Classes>();	//  Courses for the current semester being viewed
		
		
		// Welcome message!
		System.out.println("Welcome to Easy Scheduler!");
		
		// Load the JDBC driver
		Class.forName("com.mysql.jdbc.Driver");
		
		// Establish a connection
		Connection connection = DriverManager.getConnection
		("jdbc:mysql://easyschedule0.db.10711786.hostedresource.com", "easyschedule0", "MATH@3930h");
		
		Scanner input = new Scanner(System.in);
		
		// Created a boolean loop in case the user enters a PID that is not in the system, it will just ask for the PID again.
		// ** Later on, if we have time, we can add the option to add new students to the db, and ask for which courses they have already taken.
		boolean validPID = false;
		
		while(!validPID){
			//Get the student's information
			System.out.println("Please enter your PID without the starting letter.");
			pid = input.nextInt();

			// Create a statement
			Statement pidStmt = connection.createStatement();
			pidStmt.execute("use easyschedule0");	// use database name
			
			// Execute pidQuery
			String pidQuery="SELECT first_name, last_name FROM student WHERE pid = '"+pid+"'";
			ResultSet pidResults = pidStmt.executeQuery(pidQuery);
			
			// this student exists WOO!
			if (pidResults.next())
			{
				// No longer need to loop.
				validPID = true;
				
				// Some kind of welcome message to the student.
				System.out.println("Hello " + pidResults.getString("first_name") + " " + pidResults.getString("last_name") + "!");
				
				
				boolean quit = false;
				while(!quit)
				{
					// Main menu.
					System.out.println("What would you like to do?");
					System.out.println("1:\t View my Schedule");
					System.out.println("2:\t Create/Edit my Schedule");
					System.out.println("3:\t Get me outta here!");
					int menuOption = input.nextInt();
					
					// View schedule.
					if(menuOption==1)
					{
						System.out.println("Please select the semester you want to view:");
						System.out.println("1:\tSummer 2013 \n2:\tFall 2013\n3:\tSpring 2014");
						semester = input.nextInt();
						
						Student.initSemester(semester);
						
						// Create a statement
						Statement schedStmt = connection.createStatement();
						schedStmt.execute("use easyschedule0");	// use database name
						
						String schedQuery="SELECT class_num FROM courses_selected WHERE semester='"+Student.semesterString+"'";
						ResultSet classSched = schedStmt.executeQuery(schedQuery);
						
						currSemester.clear(); // Empty the arraylist first. This is taking care of a case where it's adding duplicate information.
						
						// Fill in currSemester object.
						while(classSched.next())
						{							
							currSemester.add(new Classes(classSched.getInt("class_num")));
						}
						
						
						if(currSemester.isEmpty())
						{
							System.out.println("Oops.. it looks like you have not made a schedule for " + Student.semesterString + " yet.\n");	
						}
						
						// Print out this semester's schedule.
						else
						{
							// Title.					
							System.out.format("%-9s%-10s%-42s%-11s%-6s%-5s%-40s", " ", "course", "title", "class #", "type", "hrs", "times");
							System.out.println();
							
							int cnt=1;  // to display a number next to each class.
							
							// Prints out all of the classes that you have signed up for the current semester.
							for(Classes className : currSemester)
							{
								System.out.format("%-9s%-10s%-42s%-11s%-6s%-5s%-40s", cnt+": ", className.getCourse(), className.getTitle(), className.getNum(), className.getType(), className.getCreditHrs(), className.getTimes());
								System.out.println(); 
								cnt++;
					        }
	
							
							// display menu options for the class schedule
							System.out.println("\nWhat would you like to do?");
							System.out.println("1:\t Delete a Class");
							System.out.println("2:\t Go Back to Main Menu");
							
							int scheduleOption = input.nextInt();
							
							
							// Delete a class
							if(scheduleOption==1)
							{
								int k=1; // Count for deletion purposes.
								
								// Title.
								System.out.format("%-9s%-10s%-42s%-11s%-6s%-5s%-40s", " ", "course", "title", "class #", "type", "hrs", "times");
								System.out.println();
								
								// Prints out all of the classes that you have signed up for the current semester.
								for(Classes className : currSemester)
								{
									System.out.format("%-9s%-10s%-42s%-11s%-6s%-5s%-40s", k+": ", className.getCourse(), className.getTitle(), className.getNum(), className.getType(), className.getCreditHrs(), className.getTimes());
									System.out.println(); 
									k++;
						        }
								
								System.out.println("Select a class you would like to delete from the current schedule\n");
								
								int classDel = input.nextInt()-1; // -1 since arraylist is indexed at 0
								
								// delete the class - remove from array list and from db
								Classes toDelete = currSemester.get(classDel);								
								int toDel = toDelete.getNum();
								
								currSemester.remove(toDelete);
								
								// Create a statement
								Statement delStmt = connection.createStatement();
								delStmt.execute("use easyschedule0");	// use database name
								
								String delQuery="DELETE FROM courses_selected WHERE class_num = '"+toDel+"' AND semester = '"+Student.semesterString+"'";
								int deleted = delStmt.executeUpdate(delQuery);
							
								if(deleted == 1)
								{
									System.out.println("You successfully deleted that class.");
								}
								else
								{
									System.out.println("There seems to have been an error :/ Try again.");
								}
								
								
							}
							
							// Go back to main menu
							else if(scheduleOption==2)
							{
								currSemester.clear();
								continue;
							}
							
						}
						
					}
					
					// Create/edit schedule.
					else if(menuOption==2)
					{
						System.out.println("Please select the semester you want to plan:");
						System.out.println("1:\tSummer 2013 \n2:\tFall 2013\n3:\tSpring 2014");
						semester = input.nextInt();
						
						// This section is for the fully automated scheduler where it selects all the courses for you, However This is not implemented yet.
						int reqHrs = -1, reqCours = -1;
						/*
						System.out.println("Would you like courses selected for you?\n1:\tYes\n2:\tNo");
						int choice = input.nextInt();
						if (choice == 1)
						{
							System.out.println("Would you co chose by:\n1:\tMinimum credit hours\n2:\tNumber of courses");
							int whatWay = input.nextInt();
							if (whatWay == 1)
							{
								System.out.println("Please enter the minimum number of credit hours you want.");
								reqHrs = input.nextInt();
							}
							else
							{
								System.out.println("Please enter the number of classes you want to take.");
								reqCours = input.nextInt();
							}
						}
						*/
						user = new Student(pid, semester, reqHrs, reqCours, connection);
						
						boolean goBack = false;
						while (!goBack)
						{
							
							//	Once the user selects from the list of courses it connects to that semesters DB and pulls the list of classes for that course.
							//	Then the user chooses what class it wants, then calls the student.addClass() function to add the class to the students schedule
							System.out.println("The classes available to you for the " + user.getSemesterString() + " are:\n");
							
							
							// Available classes.
							System.out.println(user.printAvailable());

							System.out.println("Please enter the number next to the course you would like to choose.");
							System.out.println("Or enter \"0\" to go back.");
						
							int selected = input.nextInt();
							
							// Going back.
							if(selected==0)
								break;
							
							String selectedCourse = Student.availableCourses.get(selected-1);  // selected-1 since the list starts at 1, but the array starts at 0.

							String allTimes = user.printTimes(selectedCourse);
							
							if (allTimes != null)
							{
								System.out.println("The times available for " + selectedCourse + " are:");
									
								System.out.println(user.printTimes(selectedCourse));
								
								System.out.println("Please enter the class number of the time you want");
								System.out.println("Or enter \"0\" to go back.");
								
								int selectedClass = input.nextInt();
					
								// Go back.
								if(selectedClass==0)
									continue;
								
								else
								{
									//Add class to your schedule
									user.addClass(new Classes(selectedClass));
									
									// Remove from available course array list
									Student.availableCourses.remove(selected-1);
								}
		
							}
							else
							{
								System.out.println("The course you selected doesn't have any times that fit in your schedule");
							}
						
							System.out.println("This is what your schedule looks like so far:");
							
							// show what semester schedule looks like so far
							printSchedule(connection);
							
							
							System.out.println("\nWould you like to add another class? yes/no");
							if(input.next().equals("no"))
							{
								goBack=true;
								
								// Adding classes that the user chose into the database.
								try
								{
									// Create a new statement
									Statement insertStmt = connection.createStatement();
									insertStmt.execute("use easyschedule0");	// use database name
									
									for(Classes className : Student.selected){
										String insert = "INSERT INTO courses_selected VALUES('"+pid+"', '"+Student.semesterString+"', '"+className.getCourse()+"', '"+className.getNum()+"', '0')";
										insertStmt.executeUpdate(insert);
									}
		
								}
								// This has already been added to the database.  We don't want to add duplicates.
								catch (Exception e) 
								{
									// System.err.println(e);
								}
							}
						}
					}
					
					else if(menuOption==3)
					{
						quit=true;
					}
					
				}

			}
				
			
			else
			{
				System.out.println("Oops, that was an invalid PID.. try again!");
			}
			
		}
	
		

		
		
		// Close the scanner
		input.close();
		// Close the connection
		connection.close();
		System.out.println("Database Closed. Good Bye :D");
	}
	
	
	// Converting the sem int into it's appropriate string name.
	public static void printSchedule(Connection connection) throws SQLException
	{	
		ArrayList<Classes> currSemester2 =  new ArrayList<Classes>(); //  Courses for the current semester being viewed
		
		// print schedule
		// Create a statement
		Statement schedStmt = connection.createStatement();
		schedStmt.execute("use easyschedule0");	// use database name
		
		String schedQuery="SELECT class_num FROM courses_selected WHERE semester='"+Student.semesterString+"'";
		ResultSet classSched = schedStmt.executeQuery(schedQuery);
		
		currSemester2.clear(); // Empty the arraylist first. This is taking care of a case where it's adding duplicate information.
		
		// Fill in currSemester object.
		while(classSched.next())
		{							
			currSemester2.add(new Classes(classSched.getInt("class_num")));
		}
		
		// Title.					
		System.out.format("%-9s%-10s%-42s%-11s%-6s%-5s%-40s", " ", "course", "title", "class #", "type", "hrs", "times");
		System.out.println();
		
		// Print out class information.
		int i=1;
		for(Classes className : Student.selected){
            System.out.format("%-9s%-10s%-42s%-11s%-6s%-5s%-40s", i+": ", className.getCourse(), className.getTitle(), className.getNum(), className.getType(), className.getCreditHrs(), className.getTimes());
            System.out.println(); 
            i++;
		}
		
		for(Classes className : currSemester2)
		{
			System.out.format("%-9s%-10s%-42s%-11s%-6s%-5s%-40s", i+": ", className.getCourse(), className.getTitle(), className.getNum(), className.getType(), className.getCreditHrs(), className.getTimes());
			System.out.println(); 
			i++;
        }
	
	}
	
}
