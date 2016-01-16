import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Parser {

	public static void main (String[] args) throws ClassNotFoundException, SQLException
	{
		
		long startTime = System.currentTimeMillis();
		
		String[] arr = { "course_prerequisites", "courses_taken", "electives", "fall_2013", "major_requirements", "semester_schedule", "spring_2014", "summer_2013"};
		//String[] arr = {"semester_schedule", "spring_2014", "summer_2013"};
		for (int i=0; i<arr.length; i++){
			String table = arr[i];
			System.out.println(table);
			
			// Load the JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Driver loaded");
			
			// Establish a connection
			Connection connection = DriverManager.getConnection
			("jdbc:mysql://easyschedule0.db.10711786.hostedresource.com", "easyschedule0", "MATH@3930h");
			System.out.println("Database connected");
			
			// Create a statement
			Statement statement = connection.createStatement();
			System.out.println("Statement created");
			
			// use database name
			statement.execute("use easyschedule0");
			System.out.println("Executed");
			
			// Execute a statement
			String query = "SELECT course FROM " + table;
			ResultSet updateResult = statement.executeQuery(query);
			
			// Iterate through the result set and print the returned results
			while (updateResult.next())
			{
				
				// Create a new statement
				Statement updateStmt = connection.createStatement();
				
				// use database name
				updateStmt.execute("use easyschedule0");
				
				
				String course = updateResult.getString("course");
				if(!course.equals("null")){
					String parse = parseString(course);
					if(!course.equals(parse))
					{
						System.out.println(course + " -> " + parse);
						String update = "UPDATE "+table+" SET course= '"+parse+"' WHERE course= '"+course+"'";
						
						updateStmt.executeUpdate(update);
					}
				}
			}
			
			// Close the connection
			connection.close();
			
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Runtime: " + totalTime);
			
			
			//String myString = "    mac   1234  c";
			//System.out.println(myString + " > " + parseString(myString));
		
		}
	}
	
	static String parseString(String stringCourse)
	{
		final int NUM_LETTERS_IN_COURSE_NAME = 3;
		final int NUM_NUMBERS_IN_COURSE_NAME = 4;
		
		char[] charCourse = stringCourse.toCharArray();
		char[] charParsed = new char[7];
		
		int courseIndex = 0, parsedIndex = 0;
		
		//Check to see if the next character is white space, if so then skip it
		while (Character.isWhitespace(charCourse[courseIndex]))
		{
			courseIndex++;
		}
		
		//Copy the first 3 letters of the course name
		for (int i = 0; i < NUM_LETTERS_IN_COURSE_NAME; i++)
			charParsed[parsedIndex++] = Character.toUpperCase(charCourse[courseIndex++]);
		
		//Check to see if the next character is white space, if so then skip it
		while (Character.isWhitespace(charCourse[courseIndex]))
		{
			courseIndex++;
		}
		
		//We dont care about the letter(s) after the next four numbers, so we only need the
		//For loop, then return
		for (int i = 0; i < NUM_NUMBERS_IN_COURSE_NAME; i++)
			charParsed[parsedIndex++] = charCourse[courseIndex++];
		
		
		
		return new String(charParsed);
	}
}
