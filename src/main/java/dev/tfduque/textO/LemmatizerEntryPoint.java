package dev.tfduque.textO;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

public class LemmatizerEntryPoint {

	public static void main(String[] args) {

		try {
			String driverName = "com.mysql.cj.jdbc.Driver";
			Class.forName(driverName);
			String serverName = "localhost"; // caminho do servidor do BD
			String mydatabase = "quinhentas_perguntas"; // nome do seu banco de dados
			String url = "jdbc:mysql://" + serverName + "/" + mydatabase + "?useTimezone=true&serverTimezone=UTC";
			String url2 = "jdbc:mysql://" + serverName + "/texto?useTimezone=true&serverTimezone=UTC";
			String username = "root"; // nome de um usu�rio de seu BD
			String password = ""; // sua senha de acesso
			Connection connection = DriverManager.getConnection(url, username, password);
			Connection terms = DriverManager.getConnection(url2, username, password);

			StringBuffer sbf = new StringBuffer();

			Set<String> allTerms = new HashSet<String>();
			Set<String> filteredTerms = new HashSet<String>();
			PreparedStatement termstmt;
			ResultSet termResults;
			termstmt = terms.prepareStatement("Select term_name from term_filter");
			termResults = termstmt.executeQuery();
			while (termResults.next()) {
				allTerms.add(termResults.getString(1));
			}

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

			Span[] spans;
			ArrayList<Span> listOfspans = new ArrayList<Span>();
			System.out.println("STATUS--->Conectado com sucesso!");
			PreparedStatement stmt = connection.prepareStatement(
					"Select p.texto as ptext, r.texto as rtext from pergunta as p, resposta as r where p.id = ? and r.id = ?");
			for (int i = 1; i <= 500; i++) {
				stmt.setInt(1, i);
				stmt.setInt(2, i);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					sbf.append(rs.getString("ptext"));
					sbf.append(" ");
					sbf.append(rs.getString("rtext"));
					spans = detector.sentPosDetect(sbf.toString());
					listOfspans.clear();
					listOfspans.addAll(Arrays.asList(spans));
					for (Span span : listOfspans) {
						String[] tokens = tokenizer.tokenize(sbf.toString().substring(span.getStart(), span.getEnd()));
						String[] tags = tagger.tag(tokens);
						for (int j = 0; j < tokens.length; j++) {
							if (allTerms.contains(tokens[j])) {
//								System.out.println(tokens[j] + "-> pos:" + tags[j]);
//								System.out.println(lemmat.lemmatize(tokens[j], tags[j]));
								filteredTerms.add(lemmat.lemmatize(tokens[j], tags[j]));
							}
						}

					}
					sbf.setLength(0);

				}
				stmt.clearParameters();

			}
			System.out.println(filteredTerms.size());
			connection.close();
			String CSV_Separator = ";";
			BufferedWriter bw = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream("filteredTerms.csv"), "UTF-8"));
			for (String filteredTerm : filteredTerms) {
				StringBuffer termLine = new StringBuffer();
				termLine.append(filteredTerm);
				//termLine.append(CSV_Separator);
				bw.write(termLine.toString());
				bw.newLine();
			}
			bw.flush();
			bw.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

}
