package dev.tfduque.textO.textTools.aux;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

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

public class LemmatizedQA implements Serializable {

	private static final long serialVersionUID = 1L;
	private int questionNumber;
	private ArrayList<String> questionTerms;
	private ArrayList<String> questionPOS;
	private ArrayList<String> answerTerms;
	private ArrayList<String> answerPOS;

	public LemmatizedQA(int qnum, String question, String answer) throws IOException, NumberFormatException,
			ParserConfigurationException, SAXException, DictionaryLoadException, WordRankingLoadException {

		this.questionNumber = qnum;
		Lemmatizer lemmat = new Lemmatizer();

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

		String[] tokens, tags;
		this.questionTerms = new ArrayList<String>();
		this.questionPOS = new ArrayList<String>();
		Span[] spans = detector.sentPosDetect(question);
		for (Span span : spans) {
			String currentSent = question.substring(span.getStart(), span.getEnd());
			tokens = tokenizer.tokenize(currentSent);
			tags = tagger.tag(tokens);
			this.questionPOS.addAll((Arrays.asList(tags)));
			for (int k = 0; k < tokens.length; k++) {
				this.questionTerms.add(lemmat.lemmatize(tokens[k], tags[k]));
			}

		}
		this.answerPOS = new ArrayList<String>();
		this.answerTerms = new ArrayList<String>();
		spans = detector.sentPosDetect(answer);
		for (Span span : spans) {
			String currentSent = answer.substring(span.getStart(), span.getEnd());
			tokens = tokenizer.tokenize(currentSent);
			tags = tagger.tag(tokens);
			this.answerPOS.addAll((Arrays.asList(tags)));
			for (int k = 0; k < tokens.length; k++) {
				this.answerTerms.add(lemmat.lemmatize(tokens[k], tags[k]));
			}

		}
	}

	public LemmatizedQA() {
	}

	public ArrayList<String> getQuestionPOS() {
		return questionPOS;
	}

	public void setQuestionPOS(ArrayList<String> questionPOS) {
		this.questionPOS = questionPOS;
	}

	public ArrayList<String> getAnswerTerms() {
		return answerTerms;
	}

	public void setAnswerTerms(ArrayList<String> answerTerms) {
		this.answerTerms = answerTerms;
	}

	public ArrayList<String> getAnswerPOS() {
		return answerPOS;
	}

	public void setAnswerPOS(ArrayList<String> answerPOS) {
		this.answerPOS = answerPOS;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

	public int[] getAllPositionsInQuestion(String term) {
		int[] positions = new int[0];
		for (int i = 0; i < this.questionTerms.size(); i++) {
			if (questionTerms.get(i).equals(term)) {
				positions = copyArrayIncrease(positions);
				positions[positions.length - 1] = i;
			}
		}
		return positions;
	}

	public int[] getAllPositionsInAnswer(String term) {
		int[] positions = new int[0];
		for (int i = 0; i < this.answerTerms.size(); i++) {
			if (answerTerms.get(i).equals(term)) {
				positions = copyArrayIncrease(positions);
				positions[positions.length - 1] = i;
			}
		}
		return positions;
	}

	private int[] copyArrayIncrease(int[] array) {
		int[] newArray = new int[array.length + 1];
		for (int i = 0; i < array.length; i++) {
			newArray[i] = array[i];
		}
		return newArray;
	}

	public int meanDistancefromTermToTerm(String mainterm, String compare) {
		int[] maintermposQ = getAllPositionsInQuestion(mainterm);
		int[] maintermposA = getAllPositionsInAnswer(mainterm);
		int[] comparetermposQ = getAllPositionsInQuestion(compare);
		int[] comparetermposA = getAllPositionsInAnswer(compare);
		int count = comparetermposQ.length + comparetermposA.length;
		int total = 0;
		if(count == 0) return 0;
		if (!(maintermposQ.length == 0 || comparetermposQ.length == 0)) {

			for (int i = 0; i < comparetermposQ.length; i++) {
				int closestDistance = (int) Double.POSITIVE_INFINITY;
				for (int j = 0; j < maintermposQ.length; j++) {
					int currentDistance = Math.abs(comparetermposQ[i] - maintermposQ[j]);
					if (closestDistance > currentDistance) {
						closestDistance = currentDistance;
					}
				}
				total += closestDistance;
			}
		}
		if(!(maintermposA.length == 0 || comparetermposA.length == 0))
		for (int i = 0; i < comparetermposA.length; i++) {
			int closestDistance = (int) Double.POSITIVE_INFINITY;
			for (int j = 0; j < maintermposA.length; j++) {
				int currentDistance = Math.abs(comparetermposA[i] - maintermposA[j]);
				if (closestDistance > currentDistance) {
					closestDistance = currentDistance;
				}
			}
			total += closestDistance;
		}
		return total / count;

	}

	public ArrayList<String[]> getAllDistances(String term, Set<String> termset) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		Set<String> traversedTerms = new HashSet<String>();
		ArrayList<String> allTerms = this.answerTerms;
		allTerms.addAll(this.answerTerms);
		for (String other : allTerms) {
			if (!termset.contains(other)) {
				continue;
			}
			if (!term.equals(other) && !traversedTerms.contains(other)) {
				int termdist = meanDistancefromTermToTerm(term, other);
				if (termdist > 0) {
					result.add(new String[] { term, other, Integer.toString(termdist) });
					traversedTerms.add(other);
				}
			}
		}
		return result;

	}

	public ArrayList<String> getQuestionTerms() {
		return questionTerms;
	}

	public void setQuestionTerms(ArrayList<String> questionTerms) {
		this.questionTerms = questionTerms;
	}

}
