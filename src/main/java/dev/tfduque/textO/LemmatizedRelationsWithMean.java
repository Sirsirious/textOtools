package dev.tfduque.textO;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.tfduque.textO.textTools.Lemmatized500p500rRecovery;
import dev.tfduque.textO.textTools.aux.LemmatizedQA;

public class LemmatizedRelationsWithMean {

	public static void main(String[] args) {
		try {
			PrintWriter pw = new PrintWriter("MeanDistanceLemmatized.csv");
			StringBuffer sb = new StringBuffer();
			sb.append("Term1;");
			sb.append("Term2;");
			sb.append("Distance\n");
			Lemmatized500p500rRecovery data = new Lemmatized500p500rRecovery();
			LemmatizedQA[] questions = data.getQas();
			ArrayList<String[]> relations;
			for (String term : data.getLemmatizedTerms()) {
				for (int i = 0; i < questions.length; i++) {
					relations = questions[i].getAllDistances(term, data.getLemmatizedTerms());
					if (!relations.isEmpty()) {
						for(String[] relation: relations) {
							sb.append(relation[0]);
							sb.append(";");
							sb.append(relation[1]);
							sb.append(";");
							sb.append(relation[2]);
							sb.append("\n");
						}
					}
				}
			}
			pw.write(sb.toString());
			pw.close();
		} catch (FileNotFoundException e) {
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
