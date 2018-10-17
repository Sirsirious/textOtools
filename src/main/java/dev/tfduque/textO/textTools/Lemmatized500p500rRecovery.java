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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import dev.tfduque.textO.textTools.aux.LemmatizedQA;
import dictionary.DictionaryLoadException;
import rank.WordRankingLoadException;

public class Lemmatized500p500rRecovery {

	private LemmatizedQA[] qas;

	public Lemmatized500p500rRecovery() {

		try {
			FileInputStream fis = new FileInputStream("sreialized500p500r.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			this.qas = (LemmatizedQA[]) ois.readObject();
			ois.close();
		} catch (FileNotFoundException e) {
			generateObjectFileAndSave();
		} catch (IOException e) {
			generateObjectFileAndSave();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
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
				this.qas[i] = questionAndAnswer;

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
			fout = new FileOutputStream("sreialized500p500r.ser");
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

}
