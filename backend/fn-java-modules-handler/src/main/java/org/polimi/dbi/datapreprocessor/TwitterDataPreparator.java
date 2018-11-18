package org.polimi.dbi.datapreprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.omg.Messaging.SyncScopeHelper;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.gson.Gson;

/**
 * @author harilal
 *  29-Apr-2018
 */
public class TwitterDataPreparator {
	public static void main(String[] args) {
		try {
			getFromSecondDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getFromSecondDataSet() {
		try {
			String folder = "resources/second_dataset/";
			String input_file = folder + "new_dataset.txt";
			String input_formatted_file = folder + "tweetid_eventid_label.txt";
			String output_file = folder + "second_twitter_id.json";
			
			BufferedReader br = null;
			BufferedWriter bw = null;
			
			String line = null;
			/********** Formatting Input File **********/
			
			/*br = new BufferedReader(new FileReader(input_file));
			bw = new BufferedWriter(new FileWriter(input_formatted_file));

			int count = 0;
			String lineArray[] = null, idString = null, idArray[] = null, eventId = null, isFake = null;
			br.readLine();//skipping the first line
			while ((line = br.readLine()) != null) {
				lineArray = line.split(";");
				eventId = Math.abs(lineArray[0].hashCode())+"";
				
				if (lineArray[1].equals("True")) {
					isFake = 1+"";
				} else {
					isFake = 0+"";
				}

				idString = lineArray[3];
				idString = idString.replaceAll("\\[|\\]|\'|\\s+", "");
				
				for (String id : idString.split(",")) {
					bw.write(id + " " + eventId + " " + isFake + "\n");
				}
			}

			br.close();
			bw.close();*/
			
			/********** Creating output File **********/
			
			br = new BufferedReader(new FileReader(input_formatted_file)); 
			
			bw = new BufferedWriter(new FileWriter(output_file));
			
			//{"current_ix": 0, "tweet_ids": ["911333326765441025", "890608763698200577"]}
			
			Set<String> tweetIdSet = new HashSet<String>();
			
			while ((line = br.readLine()) != null) {
				tweetIdSet.add(line.split("\\s+")[0]);
			}
			
			//Map<String, List<String>> eventidTweetIdListMap = Maps.newHashMap();

			System.out.println("tweetIdSet size : " + tweetIdSet.size());

			List<String> tweetIdList = Lists.newArrayList(tweetIdSet);

			tweetIdSet.clear();

			CrawlInputJson inputJson = new CrawlInputJson();
			inputJson.current_ix = 0;
			inputJson.tweet_ids = Lists.newArrayList(tweetIdList);

			Gson gson = new Gson();
			bw.write(gson.toJson(inputJson));

			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getFromRumdect() {
		try {
			String input_file = "resources/tweet_event_label_data.txt";
			String output_file = "resources/Twitter_id.json";

			String line = null;
			Set<String> tweetIdSet = new HashSet<String>();
			BufferedReader br = new BufferedReader(new FileReader(input_file));
			BufferedWriter bw = new BufferedWriter(new FileWriter(output_file));

			//{"current_ix": 0, "tweet_ids": ["911333326765441025", "890608763698200577"]}

			//Map<String, List<String>> eventidTweetIdListMap = Maps.newHashMap();

			while ((line = br.readLine()) != null) {
				tweetIdSet.add(line.split("\\s+")[0]);
				//eventidTweetIdListMap.put(line.split("\\s+")[1], Lists.newArrayList());
			}

			br.close();

			System.out.println("tweetIdSet size : " + tweetIdSet.size());
			//System.out.println("eventidTweetIdListMap size : " + eventidTweetIdListMap.size());

			List<String> tweetIdList = Lists.newArrayList(tweetIdSet);

			tweetIdSet.clear();

			CrawlInputJson inputJson = new CrawlInputJson();
			inputJson.current_ix = 0;
			inputJson.tweet_ids = Lists.newArrayList(tweetIdList);

			Gson gson = new Gson();
			bw.write(gson.toJson(inputJson));

			/*for (List<String> subList : Iterables.partition(tweetIdList, 200)) {
				CrawlInputJson inputJson = new CrawlInputJson();
				inputJson.current_ix = 0;
				inputJson.tweet_ids = Lists.newArrayList(subList);

				Gson gson = new Gson();
				bw.write(gson.toJson(inputJson));
				//break;
			}*/

			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}