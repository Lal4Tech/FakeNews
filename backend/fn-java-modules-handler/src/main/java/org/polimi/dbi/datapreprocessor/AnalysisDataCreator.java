/**
 * 
 */
package org.polimi.dbi.datapreprocessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.polimi.dbi.utils.Properties;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author harilal
 *  22-May-2018
 */
public class AnalysisDataCreator {
	private static final String CSV_FULL_FILE = Properties.FINAL_DATA_DIR + "csv_dataset_with_articles_and_locations.txt";

	public static void main(String[] args) {
		try {
			createEquallyDistributedDataset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createEquallyDistributedDataset() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(CSV_FULL_FILE));

			String line = null, lineArray[] = null;
			int tweetCount = 0, fakeCount = 0, nonFakeCount = 0, totalCount = 0;
			
			Map<String, Integer> eventIdTweetCountMap = Maps.newHashMap();
			
			br.readLine();
			while ((line = br.readLine()) != null) {
				lineArray = line.split("\\|");
				//lineArray[0] = id;
				//lineArray[1] = tweet_content;
				//lineArray[2] = url_content;
				//lineArray[3] = event_id;
				//lineArray[4] = created_at;
				//lineArray[5] = user_id;
				//lineArray[6] = in_reply_to_status_id;
				//lineArray[7] = hash_tags;
				//lineArray[8] = user_mentions;
				//lineArray[9] = retweet_count;
				//lineArray[10] = favorite_count;
				//lineArray[11] = possibly_sensitive;
				//lineArray[12] = place_name;
				//lineArray[13] = place_type;
				//lineArray[14] = country_code;
				//lineArray[15] = coordinates;
				//lineArray[16] = is_fake;
				
				if (eventIdTweetCountMap.containsKey(lineArray[3])) {
					tweetCount = eventIdTweetCountMap.get(lineArray[3]);
					tweetCount++;
					eventIdTweetCountMap.put(lineArray[3], tweetCount);
				} else {
					eventIdTweetCountMap.put(lineArray[3], 1);
				}
				
				if (lineArray[16].equals("1")) {
					fakeCount++;
				} else if(lineArray[16].equals("0")) {
					nonFakeCount++;
				}
				
				totalCount++;
			}
			
			br.close();
			
			System.out.println("totalCount : " + totalCount);
			System.out.println("fakeCount : " + fakeCount);
			System.out.println("nonFakeCount : " + nonFakeCount);
			
			System.out.println("eventIdTweetCountMap size : " + eventIdTweetCountMap.size());
			
			eventIdTweetCountMap.values().removeIf(value -> value < 5);
			
			System.out.println("eventIdTweetCountMap size : " + eventIdTweetCountMap.size());
			
			List<Integer> tweetCountList = Lists.newArrayList(eventIdTweetCountMap.values());
			
			Collections.sort(tweetCountList);
			
			System.out.println(tweetCountList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}