/**
 * 
 */
package org.polimi.dbi.datapreprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.polimi.dbi.utils.Properties;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * @author harilal
 *  01-May-2018
 */
public class CSVDatasetCreator {
	private static final String TWEET_ID_EVENT_CLASS_FILE = Properties.FINAL_DATA_DIR + "tweetid_eventid_label.txt";
	private static final String FORMATTED_TWEET_JSON_FILE = Properties.FINAL_DATA_DIR + "formatted_tweets_with_locations.txt";
	private static final String TWEET_ID_ARTICLE_FILE = Properties.FINAL_DATA_DIR + "tweet_id_articles.txt";
	private static final String CSV_FULL_FILE = Properties.FINAL_DATA_DIR + "csv_dataset_with_articles_and_locations_copy.csv";
	
	
	private static final String TWEET_ID_EVENT_LABEL_EQUAL_DISTRI_FILE = Properties.FINAL_DATA_DIR + "tweet_event_label_equal_distri_data.txt";

	private static final String FILE_HEADER_LINE = "id|event_id|created_at|user_id|in_reply_to_status_id|hash_tags|user_mentions|retweet_count|favorite_count|possibly_sensitive|place_name|place_type|country_code|coordinates|is_fake";
	
	private static Map<String, String> tweetIdEventMap, tweetIdClassMap, tweetIdArticleMap;
	private static Map<String, Set<String>> eventTweetSetMap;

	public static void main(String[] args) {
		try {
			//getTweetIdArticleMap();
			//System.out.println("tweetIdArticleMap : " + tweetIdArticleMap.size());
			
			createSupportingMaps();
			System.out.println("tweetIdEventMap size : " + tweetIdEventMap.size());
			createCSVDataset();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void getTweetIdArticleMap() {
		try {
			tweetIdArticleMap = Maps.newHashMap();
			
			BufferedReader br = new BufferedReader(new FileReader(TWEET_ID_ARTICLE_FILE));
			
			String line = null, lineArray[] = null;
			
			while ((line = br.readLine()) != null) {
				lineArray = line.split("\\|");
				
				lineArray[1] = lineArray[1].replaceAll("\\r", " ").replaceAll("\\n", " ");
				lineArray[1] = lineArray[1].replaceAll("\\|", " ");
				lineArray[1] = lineArray[1].replaceAll("\"", " ");
				//lineArray[1] = lineArray[1].replaceAll("'", " ");
				lineArray[1] = lineArray[1].replaceAll("\\s+", " ");
				lineArray[1] = lineArray[1].trim();
				
				if (tweetIdArticleMap.containsKey(lineArray[0])) {
					if (tweetIdArticleMap.get(lineArray[0]).length() < lineArray[1].length()) {
						tweetIdArticleMap.put(lineArray[0], lineArray[1]);
					}
				} else {
					tweetIdArticleMap.put(lineArray[0], lineArray[1]);
				}
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createCSVDataset() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(FORMATTED_TWEET_JSON_FILE));
			BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FULL_FILE));
			bw.write(FILE_HEADER_LINE + "\n");

			String line = null;
			StringBuilder sb = null;
			//String regex = "(?i)\\b((?:[a-z][\\w-]+:(?:\\/{1,3}|[a-z0-9%])|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}\\/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))\\)|[^\\s`!()\\[\\]{};:\'\".,<>?Â«Â»\"\"\'\']))";
			String url_regex = "(http|https)://[a-zA-Z0-9./?=_-]*";
			while ((line = br.readLine()) != null) {
				Gson gson = new Gson();
				TweetObj tweetObj = gson.fromJson(line, TweetObj.class);

				if (tweetIdEventMap.containsKey(tweetObj.id) /*&& tweetIdArticleMap.containsKey(tweetObj.id)*/) {
					String formattedText = tweetObj.tweet_text;
					String fullText = formattedText.replaceAll("\\|", " ");
					fullText = fullText.replaceAll("\\r", " ").replaceAll("\\n", " ");
					fullText = fullText.replaceAll("\\|", " ");
					fullText = fullText.replaceAll("\"", " ");
					//fullText = fullText.replaceAll("'", " ");
					fullText = fullText.replaceAll("\\s+", " ");
					fullText = fullText.trim();
					
					/*formattedText = formattedText.replaceAll(url_regex," ");*/
					
					sb = new StringBuilder();
					sb.append(tweetObj.id + "|");
					//sb.append(fullText + "|");
					//sb.append(tweetObj.tweet_text + "|");
					//sb.append(tweetIdArticleMap.get(tweetObj.id) + "|");
					sb.append(tweetIdEventMap.get(tweetObj.id) + "|");
					sb.append(tweetObj.created_at + "|");
					sb.append(tweetObj.user_id + "|");
					sb.append(tweetObj.in_reply_to_status_id + "|");
					sb.append(StringUtils.join(tweetObj.hash_tags, ",") + "|");
					sb.append(StringUtils.join(tweetObj.user_mentions, ",") + "|");
					sb.append(tweetObj.retweet_count + "|");
					sb.append(tweetObj.favorite_count + "|");
					sb.append(tweetObj.possibly_sensitive + "|");
					sb.append(tweetObj.place_name + "|");
					sb.append(tweetObj.place_type + "|");
					sb.append(tweetObj.country_code + "|");
					sb.append(tweetObj.coordinates + "|");
					sb.append(tweetObj.is_fake);
					bw.write(sb.toString() + "\n");
				}
			}

			br.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createEqualyDistributedEventFile() {
		try {
			createSupportingMaps();

			/*List<Integer> eventTweetsCountList = Lists.newArrayList();

			for (Set<String> tweetSet : eventTweetSetMap.values()) {
				eventTweetsCountList.add(tweetSet.size());
			}

			Collections.sort(eventTweetsCountList);

			System.out.println("min count " + eventTweetsCountList.get(0));*/

			Map<String, Set<String>> eventTweetSetEqualDistributedMap = Maps.newHashMap();
			Set<String> tempSet = null;

			for (Entry<String, Set<String>> entry : eventTweetSetMap.entrySet()) {
				tempSet = FluentIterable.from(entry.getValue()).limit(10).toSet();

				eventTweetSetEqualDistributedMap.put(entry.getKey(), tempSet);
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter(TWEET_ID_EVENT_LABEL_EQUAL_DISTRI_FILE));

			for (Entry<String, Set<String>> entry : eventTweetSetEqualDistributedMap.entrySet()) {
				for (String tweet : entry.getValue()) {
					bw.write(tweet + " " + entry.getKey() + " " + tweetIdClassMap.get(tweet) + "\n");
				}
			}

			bw.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	private static void createSupportingMaps() {
		try {
			tweetIdEventMap = Maps.newHashMap();
			tweetIdClassMap = Maps.newHashMap();
			eventTweetSetMap = Maps.newHashMap();

			BufferedReader br = new BufferedReader(new FileReader(TWEET_ID_EVENT_CLASS_FILE));
			String line = null, lineArray[] = null, eventId = null, tweetId = null, label = null;
			Set<String> tweetIdSet = null;

			int count = 0;

			while ((line = br.readLine()) != null) {
				lineArray = line.split("\\s+");

				tweetId = lineArray[0].trim();
				eventId = lineArray[1].trim();
				label = lineArray[2];

				if (tweetIdEventMap.containsKey(tweetId)) {
					count++;
				}

				tweetIdEventMap.put(tweetId, eventId);

				//tweetIdClassMap.put(tweetId, label);

				/*if (eventTweetSetMap.containsKey(eventId)) {
					tweetIdSet = eventTweetSetMap.get(eventId);
					tweetIdSet.add(tweetId);
					eventTweetSetMap.put(eventId, tweetIdSet);
				} else {
					tweetIdSet = Sets.newHashSet(tweetId);
					eventTweetSetMap.put(eventId, tweetIdSet);
				}*/
			}

			System.out.println("count : " + count);
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}