package dev.tfduque.textO;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import dev.tfduque.textO.textTools.InputStreamLoader;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

public class EntryPoint {

	public static void main(String[] args) {
		
		try {
			InputStream is = InputStreamLoader.txtToStream("C:\\Users\\Tiago.Duque\\Desktop\\Corpus\\500perguntasgadoleite.txt");
			System.out.println(is);
			SentenceModel model = new SentenceModel(is);
			SentenceDetectorME detector = new SentenceDetectorME(model);
			Span[] spans = detector.sentPosDetect("vaca");
			for(int i = 0; i < spans.length; i++) {
				System.out.println(spans[i].toString());
			}
			System.out.println("All went well!");
		} catch (FileNotFoundException e) {
			System.out.println("Could not open file.");
		} catch (IOException e) {
			System.out.println("Problem in input out put.");
		}
		
	}
	
}
