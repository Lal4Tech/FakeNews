/**
 * 
 */
package org.polimi.dbi.datapreprocessor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.polimi.dbi.utils.Properties;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author harilal
 *  14-May-2018
 */
public class StatsBuilder {
	private static final String CSV_FULL_FILE = Properties.FINAL_DATA_DIR + "csv_dataset_with_locations.txt";

	public static void main(String[] args) {
		try {
			createStats();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createStats() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(CSV_FULL_FILE));

			String line = null, lineArray[] = null;
			
			List<String> placeList = null, placeTypeList = null, countryList = null, coordinateList = null;
			
			placeList = Lists.newArrayList();
			placeTypeList = Lists.newArrayList();
			countryList = Lists.newArrayList();
			coordinateList = Lists.newArrayList();
			
			Set<String> hashtagSet = Sets.newHashSet();
			int hashtag_post_count = 0;
			
			br.readLine();
			while ((line = br.readLine()) != null) {
				lineArray = line.split("\\|");
				//lineArray[0] = id;
				//lineArray[1] = full_text;
				//lineArray[2] = formatted_text;
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
				
				if (!lineArray[12].equals("NULL")) {
					placeList.add(lineArray[12]);
				}
				
				if (!lineArray[13].equals("NULL")) {
					placeTypeList.add(lineArray[13]);
				}
				
				if (!lineArray[14].equals("NULL")) {
					countryList.add(lineArray[14]);
				}
				if (!lineArray[15].equals("NULL")) {
					coordinateList.add(lineArray[15]);
				}
				
				if (!lineArray[7].equals("NULL") || !lineArray[7].isEmpty()) {
					hashtag_post_count++;
					
					hashtagSet.addAll(Sets.newHashSet(lineArray[7].split(",")));
				}
				
				
			}

			br.close();
			
			System.out.println("placeList size : " + placeList.size());
			System.out.println("placeTypeList size : " + placeTypeList.size());
			System.out.println("countryList size : " + countryList.size());
			System.out.println("coordinateList size : " + coordinateList.size());
			
			System.out.println("unique places: " + Sets.newHashSet(placeList).size());
			System.out.println("unique placeTypes: " + Sets.newHashSet(placeTypeList).size());
			System.out.println("unique countries: " + Sets.newHashSet(countryList).size());
			System.out.println("unique coordinates : " + Sets.newHashSet(coordinateList).size());
			
			System.out.println("place types : " + Sets.newHashSet(placeTypeList));
			
			System.out.println("hashtag post count :" + hashtag_post_count);
			System.out.println("unique hashtag count : " + hashtagSet.size());
			
			Map<String, Integer> countryCountMap = Maps.newHashMap();
			
			/*int count = 0;
			
			for (String country : countryList) {
				if (countryCountMap.containsKey(country)) {
					count = countryCountMap.get(country);
					count++;
					countryCountMap.put(country, count);
				} else {
					countryCountMap.put(country, 1);
				}
			}
			
			countryCountMap.entrySet().stream()
            .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
            .forEach(k -> System.out.println(k.getKey() + ": " + k.getValue()));*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}