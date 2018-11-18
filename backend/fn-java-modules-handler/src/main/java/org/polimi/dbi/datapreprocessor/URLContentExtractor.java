/**
 * 
 */
package org.polimi.dbi.datapreprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Map;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author harilal
 *  14-May-2018
 */
public class URLContentExtractor {
	private static final String FOLDER = "resources/url_crawling/";
	private static final String URL_ID_FILE = FOLDER + "url_tweet_id_matchings.txt";
	private static final String ID_ARTICLE_FILE = FOLDER + "tweet_id_articles.txt";
	private static final String URL_ID_EXCEPTION_FILE = FOLDER + "url_tweet_id_exceptions.txt";

	public static void main(String[] args) {
		try {
			//getTriedIds();
			//oldIdsExtractor();
			getURLContentsAndWriteToFile();

			/*String urlString = null, urlContent = null;

			String out = new Scanner(new URL("http://linkis.com/www.nbcnews.com/news/sG8g4").openStream(), "UTF-8").useDelimiter("\\A").next();

			System.out.println(out);*/

			/*urlString = "http://t.co/JEKfwBAkFq";

			Document doc = Jsoup.connect(urlString).get();
			Elements ps = doc.select("p");

			System.out.println(ps.text());*/

			//URL url = new URL(urlString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void getTriedIds() {
		try {
			BufferedReader br1 = new BufferedReader(new FileReader(FOLDER + "tweet_id_articles_1.txt"));
			BufferedReader br2 = new BufferedReader(new FileReader(FOLDER + "url_tweet_id_exceptions_1.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(FOLDER + "tried_ids.txt"));
			
			String line = null;
			
			Set<String> triedIdSet = Sets.newHashSet();
			
			while ((line = br1.readLine()) != null) {
				triedIdSet.add(line.split("\\|",2)[0].trim());
			}
			
			while ((line = br2.readLine()) != null) {
				triedIdSet.add(line.split("\\|",2)[1].trim());
			}
			
			for (String triedId : triedIdSet) {
				bw.write(triedId + "\n");
			}
			
			br1.close();
			br2.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void oldIdsExtractor() {
		try {
			BufferedReader br1 = new BufferedReader(new FileReader(FOLDER + "tried_ids.txt"));
			BufferedReader br2 = new BufferedReader(new FileReader(URL_ID_FILE));
			BufferedWriter bw = new BufferedWriter(new FileWriter(FOLDER + "url_tweet_id_matchings_remain.txt"));
			
			String line = null, lineArray[] = null;
			
			Set<String> triedIdSet = Sets.newHashSet();
			
			while ((line = br1.readLine()) != null) {
				triedIdSet.add(line.trim());
			}
			
			while ((line = br2.readLine()) != null) {
				lineArray = line.split("\\|");
				
				if (!triedIdSet.contains(lineArray[1])) {
					bw.write(line.trim() + "\n");
				}
				
			}
			
			br1.close();
			br2.close();
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getURLContentsAndWriteToFile() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(FOLDER + "output_file_a.txt"));
			BufferedWriter bw = new BufferedWriter(new FileWriter(FOLDER + "tweet_id_articles_2.txt"));
			BufferedWriter bwTwo = new BufferedWriter(new FileWriter(FOLDER + "url_tweet_id_exceptions_2.txt"));

			String line = null, lineArray[] = null, urlContent = null;

			Document doc = null;
			Elements ps = null;

			Set<String> tweetIdSet = null;
			Map<String, Set<String>> urlTweetIdSetMap = Maps.newHashMap();

			while ((line = br.readLine()) != null) {
				lineArray = line.split("\\|");

				if (urlTweetIdSetMap.containsKey(lineArray[0])) {
					tweetIdSet = urlTweetIdSetMap.get(lineArray[0]);
					tweetIdSet.add(lineArray[1]);
					urlTweetIdSetMap.put(lineArray[0], tweetIdSet);
				} else {
					tweetIdSet = Sets.newHashSet(lineArray[1]);
					urlTweetIdSetMap.put(lineArray[0], tweetIdSet);
				}
			}

			br.close();
			
			//System.out.println("urlTweetIdSetMap size : " + urlTweetIdSetMap.size());

			for (String url : urlTweetIdSetMap.keySet()) {
				try {
					doc = Jsoup.connect(url).get();
				} catch (Exception e) {
					for (String tweetId : urlTweetIdSetMap.get(url)) {
						bwTwo.write(url + "|" + tweetId + "\n");
					}

					continue;
				}
				ps = doc.select("p");

				urlContent = ps.text();
				urlContent = urlContent.replaceAll("\n", " ");
				urlContent = urlContent.replaceAll("\\|", " ");
				urlContent = urlContent.replaceAll("\\s+", " ");

				if (urlContent.length() > 0) {
					for (String tweetId : urlTweetIdSetMap.get(url)) {
						bw.write(tweetId + "|" + urlContent + "\n");
					}
				} else {
					for (String tweetId : urlTweetIdSetMap.get(url)) {
						bwTwo.write(url + "|" + tweetId + "\n");
					}
				}
			}

			bw.close();
			bwTwo.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}