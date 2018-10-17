package dev.tfduque.textO.textTools.aux;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

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
	private String[] questionTerms;
	private String[] questionPOS;
	private String[] answerTerms;
	private String[] answerPOS;

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

		String[] tokens;

		Span[] spans = detector.sentPosDetect(question);
		for (Span span : spans) {
			String currentSent = question.substring(span.getStart(), span.getEnd());
			tokens = tokenizer.tokenize(currentSent);
			this.questionPOS = tagger.tag(tokens);
			this.questionTerms = new String[tokens.length];
			for (int k = 0; k < tokens.length; k++) {
				this.questionTerms[k] = lemmat.lemmatize(tokens[k], this.questionPOS[k]);
			}

		}
		spans = detector.sentPosDetect(answer);
		for (Span span : spans) {
			String currentSent = question.substring(span.getStart(), span.getEnd());
			tokens = tokenizer.tokenize(currentSent);
			this.answerPOS = tagger.tag(tokens);
			this.answerTerms = new String[tokens.length];
			for (int k = 0; k < tokens.length; k++) {
				this.answerTerms[k] = lemmat.lemmatize(tokens[k], this.answerPOS[k]);
			}

		}
	}

	public LemmatizedQA() {
	}

	public String[] getQuestionPOS() {
		return questionPOS;
	}

	public void setQuestionPOS(String[] questionPOS) {
		this.questionPOS = questionPOS;
	}

	public String[] getAnswerTerms() {
		return answerTerms;
	}

	public void setAnswerTerms(String[] answerTerms) {
		this.answerTerms = answerTerms;
	}

	public String[] getAnswerPOS() {
		return answerPOS;
	}

	public void setAnswerPOS(String[] answerPOS) {
		this.answerPOS = answerPOS;
	}

	public int getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(int questionNumber) {
		this.questionNumber = questionNumber;
	}

}
