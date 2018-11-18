/**
 * 
 */
package org.polimi.dbi.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Set;

import org.polimi.dbi.utils.Properties;

import com.github.jfasttext.JFastText;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author harilal
 *  01-May-2018
 */
public class FastTextGen {
	private static final String ROW_TRAIN_DATA = Properties.FINAL_DATA_DIR + "csv_dataset_with_articles_and_locations.txt";
	private static final String TRAIN_DATA = Properties.FAST_TEXT_DIR + "labelled_train_data_generic.txt";
	private static final String TEST_DATA = Properties.FAST_TEXT_DIR + "labelled_test_data_generic.txt";
	private static final String MODEL = Properties.FAST_TEXT_DIR + "supervised_generic.model";

	private static JFastText jft;

	public static void main(String[] args) {
		try {
			//createTrainingDataWithAll();
			createModel();
			getAccuracy();
			//System.out.println(getPrediction("Snopes: Seth Boyden Elementary Halloween Ban: No, \"offended Muslims\" didn't demand that Halloween be canceled ..."));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void createTrainingDataWithAll() {
		try {
			BufferedReader brRowTrainingData = new BufferedReader(new FileReader(ROW_TRAIN_DATA));
			BufferedWriter bwTrainData = new BufferedWriter(new FileWriter(TRAIN_DATA));
			BufferedWriter bwTestData = new BufferedWriter(new FileWriter(TEST_DATA));

			String line = null, lineArray[] = null;

			Set<String> labelOneSet = null, labelZeroSet = null;
			labelOneSet = Sets.newHashSet();
			labelZeroSet = Sets.newHashSet();
			
			brRowTrainingData.readLine();
			
			while ((line = brRowTrainingData.readLine()) != null) {
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

				//bwTrainData.write(lineArray[2] + " " + "__label__" + lineArray[16] + "\n");
				
				if (lineArray[16].equals("1")) {
					labelOneSet.add(lineArray[2]);
				} else {
					labelZeroSet.add(lineArray[2]);
				}
			}

			brRowTrainingData.close();
			//bwTrainData.close();
			
			int labelOneSetSize = labelOneSet.size();
			int labelZeroSetSize = labelZeroSet.size();
			
			int trainSetOneSize = (labelOneSetSize * 80)/100;
			int trainSetZeroSize = (labelZeroSetSize * 80)/100;
			
			List<List<String>> listOfListForOne = Lists.partition(Lists.newArrayList(labelOneSet), trainSetOneSize);
			List<List<String>> listOfListForZero = Lists.partition(Lists.newArrayList(labelZeroSet), trainSetZeroSize);
			
			for (String article : listOfListForOne.get(0)) {
				bwTrainData.write(article + " " + "__label__" + 1 + "\n");
			}
			
			for (String article : listOfListForZero.get(0)) {
				bwTrainData.write(article + " " + "__label__" + 0 + "\n");
			}

			bwTrainData.close();
			
			for (String article : listOfListForOne.get(1)) {
				bwTestData.write(article + " " + "__label__" + 1 + "\n");
			}
			
			for (String article : listOfListForZero.get(1)) {
				bwTestData.write(article + " " + "__label__" + 0 + "\n");
			}
			
			bwTestData.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void createModel() {
		try {
			jft = new JFastText();

			// Train supervised model
			jft.runCmd(new String[] {
					"supervised",
					"-input", TRAIN_DATA,
					"-output", MODEL
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getPrediction(String text) {
		String predictedLabel = null;
		try {
			JFastText.ProbLabel probLabel = jft.predictProba(text);
			predictedLabel = probLabel.label.substring(9);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return predictedLabel;
	}

	private static void getAccuracy() {
		try {
			jft = new JFastText();
			String model = MODEL + ".bin";
			jft.loadModel(model);

			BufferedReader br = new BufferedReader(new FileReader(TEST_DATA));

			String line = null, lineArray[] = null, prediction = null;

			int total = 0, correctCount = 0, exceptionCount = 0, oneOneCount = 0, oneZeroCount = 0, zeroOneCount = 0, zeroZeroCount = 0;

			br.readLine();
			while ((line = br.readLine()) != null) {
				lineArray = line.split("__");

				String text = lineArray[0].trim();

				//System.out.println(text);

				prediction = getPrediction(text);

				total++;

				try {
					if (prediction.equals(lineArray[2].trim())) {
						correctCount++;
					} 
					
					if (lineArray[2].trim().equals("1")) {
						if (prediction.equals("1")) {
							oneOneCount++;
						} else {
							oneZeroCount++;
						}
					} else {
						if (prediction.equals("1")) {
							zeroOneCount++;
						} else {
							zeroZeroCount++;
						}
					}
				} catch (Exception e) {
					exceptionCount++;
					continue;
				}
			}

			//System.out.println("correct count : " + correctCount);
			//System.out.println("total : " + total);
			//System.out.println("exception count : " + exceptionCount);

			System.out.println("Total Number of posts : " + (total - exceptionCount));
			System.out.println("Accuracy : " + (correctCount/(float)(total-exceptionCount))*100);
			System.out.println("1 predicted as 1 : " + oneOneCount);
			System.out.println("1 predicted as 0 : " + oneZeroCount);
			System.out.println("0 predicted as 0 : " + zeroZeroCount);
			System.out.println("0 predicted as 1 : " + zeroOneCount);

			br.close();

			/*
			 * 5 - 69.803925%
			 * 10 - 73.49715%
			 * 15 - 74.1162%
			 * 20 - 71.84562%
			 * */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}