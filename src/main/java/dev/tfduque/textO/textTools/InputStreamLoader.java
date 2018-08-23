package dev.tfduque.textO.textTools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class InputStreamLoader {

	public static InputStream txtToStream(String txtFileLocation) throws FileNotFoundException {
		return new FileInputStream(txtFileLocation);
	}
}
