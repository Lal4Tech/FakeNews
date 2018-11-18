/**
 * 
 */
package org.polimi.dbi.analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import org.polimi.dbi.utils.Properties;

import com.github.jfasttext.JFastText;

/**
 * @author harilal
 *  01-May-2018
 */
public class FastText {
	private static final String SUFFIX = "20_threshold_1500";
	private static final String ROW_TRAIN_DATA = Properties.FAST_TEXT_DIR + "final_training_dataset_" + SUFFIX + ".csv";
	private static final String ROW_TEST_DATA = Properties.FAST_TEXT_DIR + "final_test_dataset_" + SUFFIX + ".csv";
	private static final String TRAIN_DATA = Properties.FAST_TEXT_DIR + "labelled_train_data_" + SUFFIX + ".txt";
	private static final String TEST_DATA = Properties.FAST_TEXT_DIR + "labelled_test_data_" + SUFFIX + ".txt";
	private static final String MODEL = Properties.FAST_TEXT_DIR + "supervised_" + SUFFIX + ".model";

	private static JFastText jft;

	public static void main(String[] args) {
		try {
			createTrainingData();
			createModel();
			getAccuracy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void createTrainingData() {
		try {
			BufferedReader brRowTrainingData = new BufferedReader(new FileReader(ROW_TRAIN_DATA));
			BufferedReader brRowTestData = new BufferedReader(new FileReader(ROW_TEST_DATA));
			BufferedWriter bwTrainData = new BufferedWriter(new FileWriter(TRAIN_DATA));
			BufferedWriter bwTestData = new BufferedWriter(new FileWriter(TEST_DATA));

			String line = null, lineArray[] = null;

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

				bwTrainData.write(lineArray[2] + " " + "__label__" + lineArray[16] + "\n");
			}

			brRowTrainingData.close();
			bwTrainData.close();

			brRowTestData.readLine();

			while ((line = brRowTestData.readLine()) != null) {
				lineArray = line.split("\\|");

				bwTestData.write(lineArray[2] + " " + "__label__" + lineArray[16] + "\n");
			}

			brRowTestData.close();
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

			System.out.println("Number of tweets per event : " + SUFFIX);
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