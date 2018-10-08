package dev.tfduque.textO;

import java.io.FileInputStream;
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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dev.tfduque.textO.textTools.DatabaseConnection;
import dictionary.DictionaryLoadException;
import lemma.Lemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import rank.WordRankingLoadException;

public class LemmatizedRelations {

	public static void main(String[] args) {
		System.out.println("Currently running.");
		Connection connection = DatabaseConnection.getConnection();
		Connection quinhentasConnection;
		PreparedStatement termstmt, questionstmt, answerstmt;
		String currentData;
		ResultSet termResults, questionResult, answerResult;
		try {
			PrintWriter pw = new PrintWriter("LemmatizedRelations.csv");
			StringBuffer sb = new StringBuffer();
			ArrayList<Integer> mainTermPos;
			ArrayList<Span> listOfspans = new ArrayList<Span>();
			TreeMap<String, Integer> mappedRelations = new TreeMap<String, Integer>();
			Set<String> allTerms = new HashSet<String>();

			Lemmatizer lemmat = new Lemmatizer();

			//
			InputStream is = new FileInputStream(
					"/home/tiago_duque/Área de Trabalho/eclipse/textOtools/src/main/resources/pt-sent.bin");
			SentenceModel modelSent = new SentenceModel(is);
			SentenceDetectorME detector = new SentenceDetectorME(modelSent);

			//

			InputStream isl = new FileInputStream(
					"/home/tiago_duque/Área de Trabalho/eclipse/textOtools/src/main/resources/pt-token.bin");
			TokenizerModel tokenModel = new TokenizerModel(isl);
			Tokenizer tokenizer = new TokenizerME(tokenModel);

			//

			InputStream inputStream = new FileInputStream(
					"/home/tiago_duque/Área de Trabalho/eclipse/textOtools/src/main/resources/pt-pos-maxent.bin");
			POSModel model = new POSModel(inputStream);
			POSTaggerME tagger = new POSTaggerME(model);

			//

			String[] tokens, tags, lemmatizedTerms;
			Span[] spans;

			// For time counting
			Long time = System.currentTimeMillis();
			// Collects all terms;
			termstmt = connection.prepareStatement("Select term_name from lemmatizedTerms");
			termResults = termstmt.executeQuery();

			while (termResults.next()) {
				allTerms.add(termResults.getString(1));
			}
			connection.close();
			for (String mainTerm : allTerms) {
				Set<String> lemmas = new HashSet<String>();
				lemmas.add(lemmat.lemmatize(mainTerm, "N"));
				lemmas.add(lemmat.lemmatize(mainTerm, "V"));
				lemmas.add(lemmat.lemmatize(mainTerm, "ADJ"));

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
						String currentSent = currentData.substring(span.getStart(), span.getEnd());
						tokens = tokenizer.tokenize(currentSent);
						tags = tagger.tag(tokens);
						lemmatizedTerms = new String[tokens.length];
						for (int k = 0; k < tokens.length; k++) {
							lemmatizedTerms[k] = lemmat.lemmatize(tokens[k], tags[k]);
						}
						mainTermPos = indexOfAll(lemmas, lemmatizedTerms);
						if (!mainTermPos.isEmpty()) {
							for (int i = 0; i < lemmatizedTerms.length; i++) {
								if (lemmatizedTerms[i].equals(mainTerm)
										|| !allTerms.contains(lemmatizedTerms[i]))
									continue;
								
								if (mappedRelations.containsKey(lemmatizedTerms[i])) {
									mappedRelations.put(lemmatizedTerms[i], mappedRelations.get(lemmatizedTerms[i]) + 1);
								} else {
									mappedRelations.put(lemmatizedTerms[i], 1);
								}

							}
						}

					}

				}
				quinhentasConnection.close();
				if (!mappedRelations.isEmpty()) {
					sb.append(mainTerm);
					for (Map.Entry<String, Integer> entry : mappedRelations.entrySet()) {
						sb.append(", " + entry.getKey() + "[" + entry.getValue() + "]");
					}
					sb.append("\n");
				}

			}
			System.out.println(
					"Finished. Total elapsed time (in seconds): " + (System.currentTimeMillis() - time) / 1000);
			pw.write(sb.toString());
			pw.close();
		} catch (FileNotFoundException e) {
			System.out.println("Could not open file.");
		} catch (IOException e) {
			System.out.println("Problem in input out put.");
		} catch (SQLException e) {
			System.out.println("Sql error.");
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DictionaryLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WordRankingLoadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static ArrayList<Integer> indexOfAll(Set<String> lemmas, String[] lemmatizedTerms) {
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		for (int i = 0; i < lemmatizedTerms.length; i++)
			if (lemmas.contains(lemmatizedTerms[i]))
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
