package dev.tfduque.textO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import dev.tfduque.textO.textTools.DatabaseConnection;
import dev.tfduque.textO.textTools.InputStreamLoader;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

public class EntryPoint {

	public static void main(String[] args) {
		System.out.println("Currently running.");
		Connection connection = DatabaseConnection.getConnection();
		Connection quinhentasConnection;
		PreparedStatement termstmt, questionstmt, answerstmt;
		String currentData;
		ResultSet termResults, questionResult, answerResult;
		long maxBytes = Runtime.getRuntime().maxMemory();
		try {

			InputStream is = InputStreamLoader
					.txtToStream("C:\\Users\\Tiago.Duque\\Desktop\\Corpus\\OpenNLPModels\\pt-sent.bin");
			PrintWriter pw = new PrintWriter("Relations101to200.csv");
			StringBuffer sb = new StringBuffer();
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME detector = new SentenceDetectorME(model);
			SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
			ArrayList<String> listOfTags = new ArrayList<String>();
			ArrayList<Integer> mainTermPos;
			ArrayList<Integer> relationPos;
			ArrayList<Span> listOfspans = new ArrayList<Span>();
			TreeMap<String, Integer> mappedRelations = new TreeMap<String, Integer>();
			Set<String> allTerms = new HashSet<String>();
			int minimum, mean;
			String[] tags;
			Span[] spans;
			// For time counting
			Long time = System.currentTimeMillis();
			// Collects all terms;
			termstmt = connection.prepareStatement("Select term_name from term");
			termResults = termstmt.executeQuery();
			
			while (termResults.next()) {
				allTerms.add(termResults.getString(1));
			}
			connection.close();
			for (String mainTerm : allTerms) {

				mappedRelations.clear();
				System.gc();
				quinhentasConnection = DatabaseConnection.getQuinhentasPerguntasConnection();
				for (int currentDataPos = 1; currentDataPos <= 500; currentDataPos++) {
					
					questionstmt = quinhentasConnection.prepareStatement("select texto from pergunta where id = ?");
					questionstmt.setInt(1, currentDataPos);
					questionResult = questionstmt.executeQuery();
					questionResult.next();
					currentData = questionResult.getString(1);
					answerstmt = quinhentasConnection.prepareStatement("select texto from resposta where id = ?");
					answerstmt.setInt(1, currentDataPos);
					answerResult = answerstmt.executeQuery();
					answerResult.next();
					currentData = currentData + answerResult.getString(1);
					spans = detector.sentPosDetect(currentData);
					listOfspans.clear();
					listOfspans.addAll(Arrays.asList(spans));
					for (Span span : listOfspans) {
						tags = tokenizer.tokenize(currentData.substring(span.getStart(), span.getEnd()));
						listOfTags.clear();
						listOfTags.addAll(Arrays.asList(tags));
						mainTermPos = indexOfAll(mainTerm, listOfTags);
						if (!mainTermPos.isEmpty()) {
							for (String currentTerm : listOfTags) {
								if (!allTerms.contains(currentTerm) || currentTerm.equals(mainTerm))
									continue;
								mean = 0;
								relationPos = indexOfAll(currentTerm, listOfTags);
								int occurrences = relationPos.size();
								if (occurrences == 0)
									continue;
								for (Integer pos : relationPos) {
									minimum = (int) Double.POSITIVE_INFINITY;
									for (Integer termPos : mainTermPos) {
										if (Math.abs(termPos - pos) < minimum) {
											minimum = Math.abs(termPos - pos);
										}
									}
									mean += minimum;
								}
								mappedRelations.put(currentTerm, (mean / occurrences));

							}
						}

					}

				}
				quinhentasConnection.close();
				if (!mappedRelations.isEmpty()) {
					sb.append(mainTerm);
					for (Map.Entry<String, Integer> entry : sortByValue(mappedRelations).entrySet()) {
						sb.append(", " + entry.getKey() + "[" + entry.getValue() + "]");
					}
					sb.append("\n");
				}

			}
			System.out.println(
					"Finished. Total elapsed time (in seconds): " + (System.currentTimeMillis() - time) / 1000);
			pw.write(sb.toString());
			pw.close();
			// InputStream is = InputStreamLoader
			// .txtToStream("C:\\Users\\Tiago.Duque\\Desktop\\Corpus\\OpenNLPModels\\pt-sent.bin");
			// InputStream inputStream = InputStreamLoader
			// .txtToStream("C:\\Users\\Tiago.Duque\\Desktop\\Corpus\\OpenNLPModels\\pt-pos-maxent.bin");
			// InputStream sentences = InputStreamLoader
			// .txtToStream("C:\\Users\\Tiago.Duque\\Desktop\\Corpus\\500perguntasgadoleite.txt");
			// BufferedReader br = new BufferedReader(new InputStreamReader(sentences,
			// "UTF-8"));
			// POSModel posmodel = new POSModel(inputStream);
			// POSTagger tagger = new POSTaggerME(posmodel);
			// SentenceModel model = new SentenceModel(is);
			// SentenceDetectorME detector = new SentenceDetectorME(model);
			// SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
			// int iteration = 0;
			// String sentence;
			// while ((sentence = br.readLine()) != null) {
			// Span[] spans = detector.sentPosDetect(sentence);
			// for (Span span : spans) {
			//// String frase = "{";
			// String tokens[] = tokenizer.tokenize(sentence.substring(span.getStart(),
			// span.getEnd()));
			// String tags[] = tagger.tag(tokens);
			// POSSample sample = new POSSample(tokens, tags);
			//// for (String token : tokens) {
			//// frase += "[" + token + "]";
			//// }
			//// frase += "}";
			// System.out.println("Sentença " + iteration + ": " + sample.toString());
			// iteration++;
			// }
			// }
			//
			// System.out.println("All went well!");
		} catch (FileNotFoundException e) {
			System.out.println("Could not open file.");
		} catch (IOException e) {
			System.out.println("Problem in input out put.");
		} catch (SQLException e) {
			System.out.println("Sql error.");
			e.printStackTrace();
		}
	}

	static ArrayList<Integer> indexOfAll(Object obj, ArrayList list) {
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for (int i = 0; i < list.size(); i++)
			if (obj.equals(list.get(i)))
				indexList.add(i);
		return indexList;
	}

	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> unsortMap) {

		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}

		return result;

	}
}
