package org.polimi.dbi.datapreprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;

import org.polimi.dbi.utils.Properties;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author harilal
 *  29-Apr-2018
 */
public class DataPreprocessor {
	private static final String DATA_DIR = Properties.FINAL_DATA_DIR;

	private static Map<String, String> tweetIdClassMap, placeCoordinateMap;

	static int locationCount = 0, polyLocCount = 0;

	static {
		getFakeInfoOfTweets();
		placeCoordinateMap = Maps.newHashMap();
	}

	public static void main(String[] args) {
		try {
			String input_file = DATA_DIR + "crawled_tweets.json";
			String output_file = DATA_DIR + "formatted_tweets_with_locations.txt";

			BufferedReader br = new BufferedReader(new FileReader(input_file));
			BufferedWriter bw = new BufferedWriter(new FileWriter(output_file));

			String line = null, formattedJson = null;

			while ((line = br.readLine()) != null) {
				formattedJson = getFormattedTweetJson(line);

				if (formattedJson != null) {
					bw.write(formattedJson + "\n");
				}
			}

			br.close();
			bw.close();

			//System.out.println("locationCount : " + locationCount);
			//System.out.println("polyLocCount : " + polyLocCount);
			//System.out.println("placeCoordinateMap size : " + placeCoordinateMap.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getFakeInfoOfTweets() {
		try {
			String inputFile = DATA_DIR + "tweetid_eventid_label.txt";

			tweetIdClassMap = Maps.newHashMap();

			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line = null, lineArray[] = null;

			while ((line = br.readLine()) != null) {
				lineArray = line.split("\\s+");

				tweetIdClassMap.put(lineArray[0], lineArray[2]);
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getFormattedTweetJson(String json) {
		String formattedJson = null;
		try {
			JsonParser parser = new JsonParser();

			JsonElement jsonTree = parser.parse(json);

			if (jsonTree.isJsonObject()) {
				JsonObject tweetJsonObject = jsonTree.getAsJsonObject();

				JsonElement tweetCreatedAt  = tweetJsonObject.get("created_at");
				JsonElement tweetId  = tweetJsonObject.get("id_str");
				JsonElement tweetText  = tweetJsonObject.get("full_text");
				JsonElement tweetRetweetCount  = tweetJsonObject.get("retweet_count");
				JsonElement tweetFavouriteCount  = tweetJsonObject.get("favorite_count");
				JsonElement tweetPossiblySensitive  = tweetJsonObject.get("possibly_sensitive");
				JsonElement tweetInReplyToStatusId = tweetJsonObject.get("in_reply_to_status_id_str");;

				TweetObj tweetObj = new TweetObj();

				tweetObj.created_at = tweetCreatedAt.getAsString();
				tweetObj.id = tweetId.getAsString();
				tweetObj.tweet_text = tweetText.getAsString();
				tweetObj.retweet_count = tweetRetweetCount.getAsInt();
				tweetObj.favorite_count = tweetFavouriteCount.getAsInt();

				if (!tweetInReplyToStatusId.isJsonNull()) {
					tweetObj.in_reply_to_status_id = tweetInReplyToStatusId.getAsString();
				} else {
					tweetObj.in_reply_to_status_id = "NULL";
				}

				if (tweetPossiblySensitive != null) {
					tweetObj.possibly_sensitive = tweetPossiblySensitive.getAsBoolean();
				} else {
					tweetObj.possibly_sensitive = false;
				}


				tweetObj.hash_tags = Lists.newArrayList();
				tweetObj.user_mentions = Lists.newArrayList();

				JsonElement entitiesElement = tweetJsonObject.get("entities");

				if (entitiesElement.isJsonObject()) {
					JsonObject entitiesJsonObject = entitiesElement.getAsJsonObject();

					JsonElement hashTagElement = entitiesJsonObject.get("hashtags");

					JsonArray hashTagJsonArray = hashTagElement.getAsJsonArray();

					if (hashTagJsonArray.size() > 0) {
						for (JsonElement jsonElement : hashTagJsonArray) {
							JsonObject hashTagJsonObject = jsonElement.getAsJsonObject();

							JsonElement hashTag = hashTagJsonObject.get("text");

							tweetObj.hash_tags.add(hashTag.getAsString());
						}
					}


					JsonElement userMentionElement = entitiesJsonObject.get("user_mentions");

					JsonArray userMentionJsonArray = userMentionElement.getAsJsonArray();

					if (userMentionJsonArray.size() > 0) {
						for (JsonElement jsonElement : userMentionJsonArray) {
							JsonObject userMentionJsonObject = jsonElement.getAsJsonObject();

							JsonElement hashTag = userMentionJsonObject.get("id_str");

							tweetObj.user_mentions.add(hashTag.getAsString());
						}
					}
				}

				JsonElement userElement = tweetJsonObject.get("user");

				UserObj userObj = new UserObj();

				if (userElement.isJsonObject()) {
					JsonObject userJsonObject = userElement.getAsJsonObject();

					JsonElement userId = userJsonObject.get("id_str");
					JsonElement userName = userJsonObject.get("name");
					JsonElement userScreeName = userJsonObject.get("screen_name");
					JsonElement userLocation = userJsonObject.get("location");
					JsonElement userDescription = userJsonObject.get("description");
					JsonElement userIsProtected = userJsonObject.get("protected");
					JsonElement userFollowersCount = userJsonObject.get("followers_count");
					JsonElement userFriendsCount = userJsonObject.get("friends_count");
					JsonElement userListedCount = userJsonObject.get("listed_count");
					JsonElement userCreatedAt = userJsonObject.get("created_at");
					JsonElement userFavouritesCount = userJsonObject.get("favourites_count");
					JsonElement userIsVerified = userJsonObject.get("verified");
					JsonElement userStatusesCount = userJsonObject.get("statuses_count");
					JsonElement userTimeZone = userJsonObject.get("time_zone");

					userObj.id = userId.getAsString();
					userObj.name = userName.getAsString();
					userObj.screen_name = userScreeName.getAsString();
					userObj.location = userLocation.getAsString();
					userObj.description = userDescription.getAsString();
					userObj.is_protected = userIsProtected.getAsBoolean();
					userObj.followers_count = userFollowersCount.getAsInt();
					userObj.friends_count = userFriendsCount.getAsInt();
					userObj.listed_count = userListedCount.getAsInt();
					userObj.created_at = userCreatedAt.getAsString();
					userObj.favourites_count = userFavouritesCount.getAsInt();
					userObj.is_verified = userIsVerified.getAsBoolean();
					userObj.statuses_count = userStatusesCount.getAsInt();
					if (userTimeZone != null && !userTimeZone.isJsonNull()) {
						userObj.time_zone = userTimeZone.getAsString();
					} else {
						userObj.time_zone = "NULL";
					}
				}

				tweetObj.user_id = userObj.id;
				
				JsonElement coordinatesElement = tweetJsonObject.get("coordinates");

				if (coordinatesElement != null && !coordinatesElement.isJsonNull()) {
					if (entitiesElement.isJsonObject()) {
						JsonObject coordinatesJsonObject = coordinatesElement.getAsJsonObject();

						JsonElement corrdinateElement = coordinatesJsonObject.get("coordinates");

						JsonArray coordinateJsonArray = corrdinateElement.getAsJsonArray();

						String longitude = coordinateJsonArray.get(0).getAsString();
						String latitude = coordinateJsonArray.get(1).getAsString();

						tweetObj.coordinates = longitude + "," + latitude;
						locationCount++;
					} else {
						tweetObj.coordinates = "NULL";
					}
				} else {
					tweetObj.coordinates = "NULL";
				}

				JsonElement placeElement = tweetJsonObject.get("place");

				if (placeElement != null && !placeElement.isJsonNull()) {
					JsonObject placeJsonObject = placeElement.getAsJsonObject();

					tweetObj.place_name = placeJsonObject.get("full_name").getAsString();
					tweetObj.place_type = placeJsonObject.get("place_type").getAsString();
					tweetObj.country_code = placeJsonObject.get("country_code").getAsString();

					if (tweetObj.coordinates.equals("NULL")) {
						JsonElement bBoxElement = placeJsonObject.get("bounding_box");
						JsonObject bBoxObject = bBoxElement.getAsJsonObject();
						JsonElement bboxCoordinateElement = bBoxObject.get("coordinates");
						JsonArray bboxCoordinateJsonArray = bboxCoordinateElement.getAsJsonArray();
						JsonArray bboxCoordinateNestedJsonArray = bboxCoordinateJsonArray.get(0).getAsJsonArray();
						JsonArray bboxCoordinateInnerJsonArray = bboxCoordinateNestedJsonArray.get(0).getAsJsonArray();
						String longitude = bboxCoordinateInnerJsonArray.get(0).getAsString();
						String latitude = bboxCoordinateInnerJsonArray.get(1).getAsString();
						tweetObj.coordinates = longitude + "," + latitude;
					}
					
					if (placeCoordinateMap.containsKey(tweetObj.place_name)) {
						tweetObj.coordinates = placeCoordinateMap.get(tweetObj.place_name);
					} else {
						placeCoordinateMap.put(tweetObj.place_name, tweetObj.coordinates);
					}

					polyLocCount++;
				} else {
					tweetObj.country_code = "NULL";
					tweetObj.place_name = "NULL";
					tweetObj.place_type = "NULL";
					tweetObj.coordinates = "NULL";
				}

				tweetObj.is_fake = Integer.parseInt(tweetIdClassMap.get(tweetObj.id));

				Gson newGsonObj = new Gson();
				formattedJson = newGsonObj.toJson(tweetObj);
			}
			return formattedJson;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formattedJson;
	}
}