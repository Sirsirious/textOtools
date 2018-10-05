package dev.tfduque.textO.textTools;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

public class GDBConnection {

	public static Session getGraphDBConnection() {
		Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "123456"));
		Session session = driver.session();
		return session;

	}

}
