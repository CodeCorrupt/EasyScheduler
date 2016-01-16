

public class TestParser {

	public static void main (String[] args)
	{
		//Scanner input = new Scanner(System.in);
		
		String myString = "M W 12:00 AM - 12:55 AM";
		
		System.out.println(myString);
		char[] charTimes = myString.toCharArray();
		
		int i = 4;
		
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
			i += 2;
			offsets[z] = findOffset(time, isPM);
		}
		
		
		System.out.println("S = " + offsets[0] + "\nE = " + offsets[1]);
	}
	
	static int findOffset(int[] time, boolean isItPM)
	{
		if ((isItPM) && (time[0] == 1) && (time[1] == 2)) //12:xx PM is really 12:xx military so we just set PM to false so that the extra 12 hours isn't added
		{
			isItPM = false;
		}
		else if ((!isItPM) && (time[0] == 1) && (time[1] == 2)) //12:xx AM is really 00:xx military so we just set the time to 00:xx
		{
			System.out.println("blah");
			time[0] = 0;
			time[1] = 0;
		}
		//Using startTime and the isPM to determine the first true (start time) in the boolTimes array
		int Offset = 0;
		//increment the offset by 12 * 10 * startTime[0] since there are 12 sets of 5 minutes in an hour and the 1st is the 10s place
		Offset += (12 * 10 * time[0]);
		//increment the offset by 12 * 1 * startTime[1] since there are 12 sets of 5 minutes in an hour and the 2nd is in the 1s place
		Offset += (12 * time[1]);
		//increment the offset by (startTime[2] * 10) / 5 since the 2nd digit is the 10s place of the minutes and we are dividing the minutes into groups of 5
		Offset += ((time[2] * 10) / 5);
		//increment the offset by startTime[2] / 5 since the 3nd digit is the 1s place of the minutes and we are dividing the minutes into groups of 5
		Offset += (time[3] / 5);
		//if it is PM then add 12 hours to the start time ((60/5)*12 units)
		if (isItPM) 
			Offset += (12 * (24 / 2));
		return Offset;
	}
}
