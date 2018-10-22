package dev.tfduque.textO.textTools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dev.tfduque.textO.textTools.aux.LemmatizedQA;
import dictionary.DictionaryLoadException;
import rank.WordRankingLoadException;

public class Lemmatized500p500rRecovery {

	private LemmatizedQA[] qas;
	private Set<String> lemmatizedTerms;

	public Lemmatized500p500rRecovery() {
		this.lemmatizedTerms = new HashSet<String>();

		try {
			FileInputStream fis = new FileInputStream("serialized500p500r.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.setQas((LemmatizedQA[])ois.readObject());
			ois.close();
			System.out.println("Questions and Answers File loaded successfully.");
			try {
				fis = new FileInputStream("serializedterms.ser");
				ois = new ObjectInputStream(fis);
				this.setLemmatizedTerms((Set<String>) ois.readObject());
				ois.close();
				System.out.println("Terms File loaded successfully.");
			} catch (FileNotFoundException e) {
				System.out.println("Serialized file terms not found, generating a new file from DB.");

				generateTermsObjectFileAndSave();
			} catch (IOException e) {
				System.out.println("Serialized file terms not found, generating a new file from DB.");
				generateTermsObjectFileAndSave();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Serialized file for Questions and Answers not found, generating a new file from DB.");
			generateObjectFileAndSave();
		} catch (IOException e) {
			System.out.println("Serialized file for Questions and Answers not found, generating a new file from DB.");
			generateObjectFileAndSave();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void generateTermsObjectFileAndSave() {
		PreparedStatement termstmt;
		ResultSet termResults;
		Connection connection = DatabaseConnection.getConnection();
		try {
			termstmt = connection.prepareStatement("Select term_name from lemmatizedTerms");

			termResults = termstmt.executeQuery();
			while (termResults.next()) {
				lemmatizedTerms.add(termResults.getString(1));
			}
			FileOutputStream fout;
			try {
				fout = new FileOutputStream("serializedterms.ser");
				ObjectOutputStream oos = new ObjectOutputStream(fout);
				oos.writeObject(this.lemmatizedTerms);
				oos.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	private void generateObjectFileAndSave() {
		this.qas = new LemmatizedQA[500];
		LemmatizedQA questionAndAnswer;
		PreparedStatement questionstmt, answerstmt;
		ResultSet questionResult, answerResult;
		Connection quinhentasConnection = DatabaseConnection.getQuinhentasPerguntasConnection();
		for (int i = 1; i <= 500; i++) {
			try {
				questionstmt = quinhentasConnection.prepareStatement("select texto from pergunta where id = ?");
				questionstmt.setInt(1, i);
				questionResult = questionstmt.executeQuery();
				questionResult.next();
				answerstmt = quinhentasConnection.prepareStatement("select texto from resposta where id = ?");
				answerstmt.setInt(1, i);
				answerResult = answerstmt.executeQuery();
				answerResult.next();
				questionAndAnswer = new LemmatizedQA(i, questionResult.getString(1), answerResult.getString(1));
				this.qas[i-1] = questionAndAnswer;

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
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
		FileOutputStream fout;
		try {
			fout = new FileOutputStream("serialized500p500r.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(this.qas);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Set<String> getLemmatizedTerms() {
		return lemmatizedTerms;
	}

	public void setLemmatizedTerms(Set<String> lemmatizedTerms) {
		this.lemmatizedTerms = lemmatizedTerms;
	}

	public LemmatizedQA[] getQas() {
		return qas;
	}

	public void setQas(LemmatizedQA[] qas) {
		this.qas = qas;
	}
	
	public LemmatizedQA getQuestionById(int id) {
		return this.qas[id];
	}

}
