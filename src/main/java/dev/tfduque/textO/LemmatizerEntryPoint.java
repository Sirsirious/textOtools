package dev.tfduque.textO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import dev.tfduque.textO.textTools.InputStreamLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

public class LemmatizerEntryPoint {

	public static void main(String[] args) {

		try {
			String driverName = "com.mysql.cj.jdbc.Driver";
			Class.forName(driverName);
			String serverName = "localhost"; // caminho do servidor do BD

			String mydatabase = "quinhentas_perguntas"; // nome do seu banco de dados

			String url = "jdbc:mysql://" + serverName + "/" + mydatabase+"?useTimezone=true&serverTimezone=UTC";

			String username = "root"; // nome de um usuário de seu BD

			String password = ""; // sua senha de acesso

			Connection connection = DriverManager.getConnection(url, username, password);
			
			InputStream is = InputStreamLoader
					.txtToStream("E:\\Google Drive\\Mestrado Computação\\00 - Dissertação\\OpenNLP Models\\pt-sent.bin");
			InputStream inputStream = new FileInputStream("E:\\\\Google Drive\\\\Mestrado Computação\\\\00 - Dissertação\\\\OpenNLP Models\\\\pt-sent.bin"); 
			POSModel model = new POSModel(inputStream); 
			StringBuffer sb = new StringBuffer();
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME detector = new SentenceDetectorME(model);
			SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
			Span[] spans; 
			ArrayList<Span> listOfspans = new ArrayList<Span>();
			if (connection != null) {

				System.out.println("STATUS--->Conectado com sucesso!");

			} else {

				System.out.println("STATUS--->Não foi possivel realizar conexão");

			}
			PreparedStatement stmt = connection.prepareStatement("Select p.texto as ptext, r.texto as rtext from pergunta as p, resposta as r where p.id = ? and r.id = ?");
			for(int i = 1; i <= 500; i++) {
				stmt.setInt(1, i);
				stmt.setInt(2, i);
				ResultSet rs = stmt.executeQuery();
				String query = "";
				while(rs.next()) {
					String pergunta = rs.getString("ptext");
					String resposta = rs.getString("rtext");
					spans= detector.sentPosDetect(pergunta+" "+resposta);
					listOfspans.clear();
					listOfspans.addAll(Arrays.asList(spans));
					for (Span span : listOfspans) {
						span.get
					}
					
				}
				stmt.clearParameters();
				
				
				
			}
			connection.close();

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
		}

	}

}
