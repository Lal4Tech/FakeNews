/**
 * 
 */
package org.polimi.dbi.datapreprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.polimi.dbi.utils.Properties;

/**
 * @author harilal
 *  14-May-2018
 */
public class URLExtractor {
	private static final String CSV_FULL_FILE = Properties.FINAL_DATA_DIR + "csv_dataset_with_locations.txt";
	private static final String URL_ID_FILE = Properties.FINAL_DATA_DIR + "url_tweet_id_matchings.txt";
	
	public static void main(String[] args) {
		try {
			getURLsAndWriteToFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void getURLsAndWriteToFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(CSV_FULL_FILE));
			BufferedWriter bw = new BufferedWriter(new FileWriter(URL_ID_FILE));

			String line = null, lineArray[] = null;
			
			String url_regex = "(http|https)://[a-zA-Z0-9./?=_-]*";
			
			Pattern url_pattern = Pattern.compile(url_regex);
			Matcher matcher = null;
			
			br.readLine();
			while ((line = br.readLine()) != null) {
				lineArray = line.split("\\|");
				//lineArray[1] = full_text;
				
				matcher = url_pattern.matcher(lineArray[1]);
				
				while (matcher.find()) {
					bw.write(matcher.group().trim() + "|" + lineArray[0] + "\n");
				}
				
			}
			
			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
