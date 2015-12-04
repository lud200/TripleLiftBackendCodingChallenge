package advertiserPerformance;
/*
 * **************************************************************************************************************************************************
 * 
 * Author: Duvvuri
 * 
 * **************************************************************************************************************************************************
 * The program requests the URL. 
 * 
 * If the response comes back in 200 milliseconds, then the data is collected in to a string and parsed into a JSON object. 
 * 
 * If the data is not collected within 200 milliseconds for an advertiser, a note will be displayed in the console
 * and the advertiser ID is ignored.
 * 
 *  If the connection is not timed out, the program takes a list of advertiser ID's from an array,
 *  and aggregates the number of clicks and impressions over the received date.
 *  
 *  The result is stored into a JSON Object.
 *  
 *  **************************************************************************************************************************************************
 *  
 *  Run time Analysis:
 *  
 *  **************************************************************************************************************************************************
 *  
 *  The program runs over the number of ID's and stores the list of entries for each object. 
 *  
 *  Suppose say there are three advertiser ID's (i, j, k) and each has (a, b, c) rows respectively,
 *  the final length of our JSON array would be (a+b+c) rows.
 *  
 *  If we say (a+b+c)=n, the program iterates over n elements once. 
 *  
 *  Hence the program runs in linear time complexity i.e., O(n) time complexity.
 *  
 *  
 *  PS: The output is displayed on the console for convenience.
 *  
 *  For JSON Parsing the java-json.jar is included in the zip file.
 *  **************************************************************************************************************************************************
 * */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class httpRequest {
	
	public static void main(String[] args) throws JSONException{
		//Calling the method
		JSONObject data = adCaller(new long[]{123, 456, 789});
		System.out.println(data);
		
	}
	
	public static JSONObject adCaller(long[] ids) throws JSONException{
		
		StringBuilder sb = new StringBuilder();
		InputStreamReader in = null;
		
		for(long id : ids){
			
			//Collect the website address for each id passed in the long array of ID's
			URLConnection conn = null;
			String website = "http://dan.triplelift.net/code_test.php?advertiser_id=";
			website = website+id;
			
			try {
				//get the URL and establish the connection
				URL url = new URL(website);
				conn = url.openConnection();
				
				//If the connection is established, then set the time out to 200 ms as mentioned in the problem description
				if (conn != null)
					conn.setReadTimeout(200);
				
				if (conn != null && conn.getInputStream() != null) {
					
					//If the timer is not expired, then read the json input into string buffer
					in = new InputStreamReader(conn.getInputStream(),Charset.defaultCharset());
					BufferedReader bufferedReader = new BufferedReader(in);
					
					if (bufferedReader != null) {
						int cp;
					
						while ((cp = bufferedReader.read()) != -1) {
							sb.append((char) cp);
						}
						
						bufferedReader.close();
					}
				}
				
				in.close();
			}
			catch (Exception e) {
				//For all exceptions(Illegal Argument exception, IO exception etc.,) used default exception
				System.out.println("Advertiser "+id+" is excluded");
			}
		}
		
		//Store the string buffer into a string. 
		String result = sb.toString();
		result = result.replaceAll("\\]",",");
		result = result.replaceAll("\\[", "");
		result = "["+result+"]";
		
		//Convert the string to json array
		JSONArray jsonArray = new JSONArray(result);
		JSONObject jsonObj = null;
		int num_clicks = 0;
		int num_impressions = 0;
		
		HashMap<String, List<Integer>> clicks = new HashMap<String, List<Integer>>();
		
		//Calculate the count of clicks and impressions based on date value. 
		for(int i = 0; i < jsonArray.length(); i++){
			jsonObj = jsonArray.getJSONObject(i);
			String date = jsonObj.getString("ymd");
			List<Integer> count = new ArrayList<Integer>();
		
			if(!clicks.containsKey(date)){
				num_clicks = jsonObj.getInt("num_clicks");
				num_impressions = jsonObj.getInt("num_impressions");
			}
			else{
				count = clicks.get(date);
				num_clicks = count.get(0)+jsonObj.getInt("num_clicks");
				num_impressions = count.get(1)+jsonObj.getInt("num_impressions");
			}
			count.clear();
			count.add(num_clicks);
			count.add(num_impressions);
			clicks.put(date, count);
		}
		
		//Save the map to Json object.
		JSONObject jsonClicks = new JSONObject(clicks);
		return jsonClicks;
		
	}
}
