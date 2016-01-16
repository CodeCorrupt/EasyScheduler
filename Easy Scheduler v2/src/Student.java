/*

	Easy Scheduler
	Jessica Carter
	Tyler Hoyt
	COP 3930H

*/

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Scanner;

public class Student 
{	
	private int pid;
	private String first;
	private String last;
	private String major;
	private String minor;
	
	private int requestedHrs;
	private int requestedCourses;
	private int semester;
	public static String semesterString;
	private Connection connection;
	
	private boolean[] summarizedSchedule = null; //Summarized schedule is the result of oring each classes schedule together, this gives one boolean array
												 //that shows the free time of the student.
	
	static ArrayList<Classes> selected = null; //Courses that have been selected for the current semester
	static ArrayList<String> availableCourses = new ArrayList<String>();	// Courses that are available for the student take for the current semester

	public Student(int userPID, int sem, int reqHrs, int reqCourses, Connection conn) throws ClassNotFoundException, SQLException
	{		
		// Waiting message
		System.out.println("Please wait while we do the magic!");
		
		// Initialize connection variable.
		connection = conn;
		
		// Initialize semester variable.
		initSemester(sem);
		
		// Create a statement
		Statement statement = connection.createStatement();
		statement.execute("use easyschedule0");	// use database name
		
		// Execute statement
		ResultSet result = statement.executeQuery("SELECT first_name, last_name, major, minor FROM student WHERE pid='"+userPID+"'");
		
		// Iterate through the result set and print the returned results
		if (result.next())
		{
			pid = userPID;									
			first = result.getString("first_name");
			last = result.getString("last_name");
			major = result.getString("major");
			minor = result.getString("minor");
		}

		semester = sem;
		requestedHrs = reqHrs;
		requestedCourses = reqCourses;
		
		findClasses(); 	// finds the classes that you can tke
		
		selected = new ArrayList<Classes>();
		
		summarizedSchedule = new boolean[12 * 24 * 5];
		Arrays.fill(summarizedSchedule, false);
	}
	
	public int getPID()
	{
		return pid;
	}
	
	public String getFirst()
	{
		return first;
	}
	
	public String getLast()
	{
		return last;
	}
	
	public String getFull()
	{
		return (first + " " + last);
	}
	
	public String getMajor()
	{
		return major;
	}
	
	public String getMinor()
	{
		return minor;
	}
	
	public int getSemesterInt()
	{
		return semester;
	}
	
	public String getSemesterString()
	{
		return semesterString;
	}
	
	public boolean[] getSummarizedSchedule()
	{
		return summarizedSchedule;
	}
	
	// Converting the sem int into it's appropriate string name.
	public static void initSemester(int sem)
	{
		if(sem==1)
			semesterString = "summer_2013";
		else if(sem==2)
			semesterString = "fall_2013";
		else
			semesterString = "spring_2014";	
	}


	public void addClass(Classes newClass)
	{
		selected.add(newClass);
	}
	
	// Fills database with the classes that the student needs and that are available for the given semester.
	private void findClasses() throws ClassNotFoundException, SQLException
	{		
		// Run time!!!		
		// double startTime = System.currentTimeMillis();
		
		// Create a statement
		Statement statement = connection.createStatement();
		statement.execute("use easyschedule0");	// use database name
				
		// Execute courses
		String courseQuery = 	"SELECT distinct major_requirements.course " +
								"FROM major_requirements " +
									"RIGHT JOIN "+semesterString+" ON "+semesterString+".course=major_requirements.course " +
									"LEFT JOIN courses_taken ON courses_taken.course=major_requirements.course " +
									"LEFT JOIN course_prerequisites ON course_prerequisites.prerequisites=courses_taken.course " +
									"LEFT JOIN courses_selected ON courses_selected.course=major_requirements.course " +
								"WHERE courses_taken.course IS NULL " +
									"AND major_requirements.major='"+major+"' " +
									"AND major_requirements.elective = 0 " +
									"AND courses_selected.class_num IS NULL;";
		
		ResultSet courses = statement.executeQuery(courseQuery);
		
		while (courses.next())
		{
			String tempCourse = courses.getString("course");
			
			// Create a statement
			Statement statement2 = connection.createStatement();
			statement2.execute("use easyschedule0");	// use database name
			
			// Execute prereqCheck to see if the prerequisites have been met for this class.
			String prQuery = 	"SELECT distinct course_prerequisites.course, course_prerequisites.prerequisites " +
								"FROM course_prerequisites " +
									"LEFT JOIN courses_taken ON courses_taken.course = course_prerequisites.prerequisites " +
								"WHERE course_prerequisites.course = '"+tempCourse+"' " +
									"AND courses_taken.course IS NULL " +
									"AND course_prerequisites.prerequisites != 'null';";
			
			
			ResultSet prereqCheck = statement2.executeQuery(prQuery);
			
			// Iterate through the prerequisites for this course to see if you have fulfilled the requirements.
			if (!prereqCheck.next())
			{
				//System.out.println(tempCourse);
				availableCourses.add(tempCourse);
			}
		}
				
		
		// Now do electives!
		
		// Create a statement
		Statement statement3 = connection.createStatement();
		statement3.execute("use easyschedule0");	// use database name
		
		String electiveQuery = 	"SELECT distinct electives.course " +
								"FROM electives " +
									"RIGHT JOIN "+semesterString+" ON "+semesterString+".course=electives.course " +
									"LEFT JOIN courses_taken ON courses_taken.course=electives.course " +
									"LEFT JOIN major_requirements ON major_requirements.elective = 1 " +
								"WHERE electives.type=major_requirements.elective_area " +
									"AND major_requirements.major='"+major+"' " +
									"AND courses_taken.course IS NULL;";
		
		ResultSet electives = statement.executeQuery(electiveQuery);
		
		while (electives.next())
		{
			String tempCourse = electives.getString("electives.course");
			
			// Create a statement
			Statement statement2 = connection.createStatement();
			statement2.execute("use easyschedule0");	// use database name
			
			// Execute prereqCheck to see if the prerequisites have been met for this class.
			String prQuery = 	"SELECT distinct course_prerequisites.course, course_prerequisites.prerequisites " +
								"FROM course_prerequisites " +
									"LEFT JOIN courses_taken ON courses_taken.course = course_prerequisites.prerequisites " +
								"WHERE course_prerequisites.course = '"+tempCourse+"' " +
									"AND courses_taken.course IS NULL " +
									"AND course_prerequisites.prerequisites != 'null';";
			
			
			ResultSet prereqCheck = statement2.executeQuery(prQuery);
			
			// Iterate through the prerequisites for this course to see if you have fulfilled the requirements.
			if (!prereqCheck.next())
			{
				availableCourses.add(tempCourse + " e");
			}
		}
		
		
		// If I want to show runtime. - Last optimization made this 9 times faster!!
		/*double endTime   = System.currentTimeMillis();
		double totalTime = endTime - startTime;
		System.out.println("Run time = " + totalTime/1000);*/

	}

	//returns a string with all of the courses available for the selected semester
	public String printAvailable() throws SQLException
	{
		String returnString = new String();
		
		
		for(int i=0; i<availableCourses.size(); i++){
			returnString += (i+1) + ":\t" + availableCourses.get(i) + "\n";
		}
		
		return returnString;
	}
	
	
	//returns a string of available times
	public String printTimes(String course) throws SQLException //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Shows all times! not just the ones that work
	{
		String returnString = new String();
		
		
		// Create a statement
		Statement fillSelected = connection.createStatement();
		fillSelected.execute("use easyschedule0");	// use database name
		
		// Execute takenCourses
		String fillSelect = "SELECT times FROM courses_selected NATURAL JOIN " + semesterString;
		
		// Execute takenCourses - will use below in while loop
		ResultSet selectedDB = fillSelected.executeQuery(fillSelect);
		
		
		// Create a statement
		Statement availableDB = connection.createStatement();
		availableDB.execute("use easyschedule0");	// use database name
		
		// Execute takenCourses
		ResultSet allTimes = availableDB.executeQuery("SELECT title, class_num, type, credit_hrs, times FROM " + semesterString + " WHERE type != 'LAB' AND course = '" + course + "'");
		
		while (allTimes.next())
		{
			String title = allTimes.getString("title");
			String classNum = allTimes.getString("class_num");
			String type = allTimes.getString("type");
			String credit_hrs = allTimes.getString("credit_hrs");
			String times = allTimes.getString("times");
			
			boolean classWorks = true;
			
			// Loop loop through selectedDB
			while(selectedDB.next())
			{						
				if (!Classes.classFits(selectedDB.getString("times"), times))
					classWorks = false;
			}
			

			if (classWorks)
				returnString += classNum + ":\t" + course + "\t" + title + "\t" + type + "\t" + credit_hrs + "\t" + times + "\t" + "\n";
		}
		if (!returnString.isEmpty())
			return returnString;
		return null;
	}
	
}
