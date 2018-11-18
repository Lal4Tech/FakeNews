package org.polimi.dbi.datapreprocessor;

/**
 * @author harilal
 *  29-Apr-2018
 */
public class UserObj {
	String id;
	String name;
	String screen_name;
	String location;
	String description;
	Boolean is_protected;
	Integer followers_count;
	Integer friends_count;
	Integer listed_count;//The number of public lists that this user is a member of
	String created_at;
	Integer favourites_count;//The number of Tweets this user has liked in the account’s lifetime
	Boolean is_verified;
	Integer statuses_count;
	String time_zone;//"NULL" if not available
}
