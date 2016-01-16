/*

	Easy Scheduler
	Jessica Carter
	Tyler Hoyt
	COP 3930H

*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

class Classes
{
	private static final int DAYS_IN_WEEK = 5;
	private static final int HOURS_IN_DAY = 24;
	private static final int MINUTES_IN_HOUR = 60;
	private static final int MINUTES_IN_UNIT = 5;
	
	private boolean[] boolTime;
	private int course_id;
	private String section_num;
	private String type;
	private int class_num;
	private String professor;
	private String room;
	private int room_cap;
	private String times;
	private int class_cap;
	private int num_enrolled;

	//These values are just copied from the course class
	private String course;
	private String title;
	private int credit_hrs;
		
	public Classes(int ClassNum) throws SQLException
	{		
		// Establish a connection
		Connection connection = DriverManager.getConnection
		("jdbc:mysql://easyschedule0.db.10711786.hostedresource.com", "easyschedule0", "MATH@3930h");
		
		// Create a statement
		Statement classStmt = connection.createStatement();
		classStmt.execute("use easyschedule0");	// use database name
				
		// Execute takenCourses
		ResultSet courseSel = classStmt.executeQuery("SELECT course, type, title, section_num, class_num, credit_hrs, professor, times, room  FROM " + Student.semesterString + " WHERE class_num = '" + ClassNum + "'"); //ONLY LOOKING AT LECTURES and looking at any time!!!!!!!!!

		if(courseSel.next())
		{
			class_num = ClassNum;
			course = courseSel.getString("course");
			type = courseSel.getString("type");
			title = courseSel.getString("title");
			section_num = courseSel.getString("section_num");
			class_num = courseSel.getInt("class_num");
			credit_hrs = courseSel.getInt("credit_hrs");;
			professor = courseSel.getString("professor");
			times =  courseSel.getString("times");
			room =  courseSel.getString("room");
		}
		
		//Parse the string time into useable information
		boolTime = parseTimes(times);

	}
	
	public String getCourse()
	{
		return course;
	}
	
	public int getNum()
	{
		return class_num;
	}
	
	public int getCreditHrs()
	{
		return credit_hrs;
	}
	
	
	public String getType()
	{
		return type;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getTimes()
	{
		return times;
	}

	public boolean[] getBoolTime()
	{
		return boolTime;
	}
	
	public boolean seatsOpen()
	{
		return (num_enrolled < class_cap);
	}
	
	//Checks to see if Time string provided conflicts with the time of this course
	public static boolean classFits(String timeStringA, String timeStringB)
	{
		//parse the string provided into a boolean array
		boolean[] timeBoolA = parseTimes(timeStringA);
		boolean[] timeBoolB = parseTimes(timeStringB);
		
		//loop through each time slot and if any overlap then set return value to false
		for (int i = 0; i < ((MINUTES_IN_HOUR / MINUTES_IN_UNIT) * HOURS_IN_DAY * DAYS_IN_WEEK); i++)
		{
			if (timeBoolA[i] && timeBoolB[i])
				return false;
		}
		return true;
		
	}
	
	
	//Chance the string of "times" into an Boolean array.
	//The resulting array will consist of 1440 elements with each representing a 5 minuet chunk
	//starting at 12:00 AM Monday and ending at 11:55 PM Friday
	private static boolean[] parseTimes(String times)
	{
		//Change the String into a char array
		char[] charTimes = times.toCharArray();
		
		int i = 0; //index of current char
		
		
		
		//Scan in and store the days of the class into a Boolean Array
		boolean[] days = new boolean[DAYS_IN_WEEK];
		Arrays.fill(days, false);
		while (!Character.isDigit(charTimes[i]))
		{
			if (!Character.isWhitespace(charTimes[i]))
			{
				switch(Character.toLowerCase(charTimes[i]))
				{
					case 'm': days[0] = true;
						break;
					case 't': days[1] = true;
						break;
					case 'w': days[2] = true;
						break;
					case 'r': days[3] = true;
						break;
					case 'f': days[4] = true;
						break;
				}
					
			}
			i++;
		}
		
		int[] offsets = new int[2]; // offsets[0] is starting offset, offset[1] is ending
		for (int z = 0; z < 2; z++)
		{
		
			//Scan in the starting hour and minute
			int[] time = new int[4];
			int j = 0;
			while (!Character.isAlphabetic(charTimes[i]))
			{
				if (Character.isDigit(charTimes[i]))
				{
					time[j] = Character.getNumericValue(charTimes[i]);
					j++;
				}
				i++;
			}
			
			//Scan in if it is AM or PM
			Boolean isPM = false;
			if (Character.toLowerCase(charTimes[i]) == 'p') //All we care about is if there is a P, then skip over the M
			{
				isPM = true;
			}
			i += 2; //Increment by two to skip the M of AM or PM.
			
			//Run the find offset formula on the information parsed.
			offsets[z] = findOffset(time, isPM);
		}
		
		boolean[] booleanTime = new boolean[(DAYS_IN_WEEK * HOURS_IN_DAY * MINUTES_IN_HOUR) / MINUTES_IN_UNIT];
		for (int z = 0; z < DAYS_IN_WEEK; z++)
		{
			if (days[z]) //(((HOURS_IN_DAY * MINUTES_IN_HOUR) / MINUTES_IN_UNIT) * z) is the offset to day z where Monday is day 0
				Arrays.fill(booleanTime, (((HOURS_IN_DAY * MINUTES_IN_HOUR) / MINUTES_IN_UNIT) * z) + offsets[0], (((HOURS_IN_DAY * MINUTES_IN_HOUR) / MINUTES_IN_UNIT) * z) + offsets[1], true);
		}
		return booleanTime;
	}
	
	//Converts the 4 digit time and AM/PM into an offset from the beginning of a day
	private static int findOffset(int[] time, boolean isItPM)
	{
		if ((isItPM) && (time[0] == 1) && (time[1] == 2)) //12:xx PM is really 12:xx military so we just set PM to false so that the extra 12 hours isn't added
		{
			isItPM = false;
		}
		else if ((!isItPM) && (time[0] == 1) && (time[1] == 2)) //12:xx AM is really 00:xx military so we just set the time to 00:xx
		{
			time[0] = 0;
			time[1] = 0;
		}
		//Using startTime and the isPM to determine the first true (start time) in the boolTimes array
		int Offset = 0;
		//increment the offset by 12 * 10 * startTime[0] since there are 12 sets of 5 minutes in an hour and the 1st is the 10s place
		Offset += ((MINUTES_IN_HOUR/MINUTES_IN_UNIT) * 10 * time[0]);
		//increment the offset by 12 * 1 * startTime[1] since there are 12 sets of 5 minutes in an hour and the 2nd is in the 1s place
		Offset += ((MINUTES_IN_HOUR/MINUTES_IN_UNIT) * time[1]);
		//increment the offset by (startTime[2] * 10) / 5 since the 2nd digit is the 10s place of the minutes and we are dividing the minutes into groups of 5
		Offset += ((time[2] * 10) / MINUTES_IN_UNIT);
		//increment the offset by startTime[2] / 5 since the 3nd digit is the 1s place of the minutes and we are dividing the minutes into groups of 5
		Offset += (time[3] / MINUTES_IN_UNIT);
		//if it is PM then add 12 hours to the start time ((60/5)*12 units)
		if (isItPM) 
			Offset += ((MINUTES_IN_HOUR/MINUTES_IN_UNIT) * (HOURS_IN_DAY / 2));
		return Offset;
	}
}