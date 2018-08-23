package dev.tfduque.textO.NLU.relations;

import java.util.Set;

public class WordRelations {

	private int maxPerimeter;
	private boolean userStemmer;
	private Set<String> wordSet;

	public int getMaxPerimeter() {
		return maxPerimeter;
	}

	public void setMaxPerimeter(int maxPerimeter) {
		this.maxPerimeter = maxPerimeter;
	}

	public boolean isUserStemmer() {
		return userStemmer;
	}

	public void setUserStemmer(boolean userStemmer) {
		this.userStemmer = userStemmer;
	}
	
	

}
