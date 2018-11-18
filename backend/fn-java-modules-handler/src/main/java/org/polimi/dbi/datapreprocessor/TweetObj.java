package org.polimi.dbi.datapreprocessor;

import java.util.List;

/**
 * @author harilal
 *  14-May-2018
 */
public class TweetObj {
	String created_at;
	String id;
	String tweet_text;
	String user_id;
	String in_reply_to_status_id;//"NULL" if not available
	List<String> hash_tags;
	List<String> user_mentions;
	Integer retweet_count;
	Integer favorite_count;
	Boolean possibly_sensitive;//"NULL" if not available
	Integer is_fake;
	String coordinates;//"NULL" if not value, order : longitue,latitude
	String country_code;//"NULL" if not available
	String place_name; //"NULL" if not available
	String place_type;//"NULL" if not available
}
