package org.neo4j.tutorial;

import org.junit.Rule;
import org.junit.Test;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.helpers.collection.IteratorUtil;

import static org.junit.Assert.assertEquals;

/**
 * In this Koan we learn how to create, update, and delete nodes and relationships in the
 * database using the Cypher language.
 */
public class Koan2
{

    @Rule
    public Neo4jEmbeddedResource neo4jResource = new Neo4jEmbeddedResource();

    @Test
    public void shouldCreateASingleNode()
    {
        GraphDatabaseService graphDatabaseService = neo4jResource.getGraphDatabaseService();
        String cql = null;

        cql="CREATE (n)";

        graphDatabaseService.execute(cql);

        final Result result = graphDatabaseService.execute("START n=node(*) return n");

        assertEquals( 1, IteratorUtil.count(result) );
    }

    @Test
    public void shouldCreateASingleNodeWithSomeProperties()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();
        String cql = null;

        cql="CREATE (n:Person{firstname: 'Tom', lastname: 'Baker'})";

        db.execute(cql);

        final Result result = db.execute(
                "MATCH (n {firstname: 'Tom', lastname: 'Baker'}) RETURN n" );

        /* Geek question: if you've read ahead, what's the Big O cost of this match? */

        assertEquals( 1, IteratorUtil.count(result) );
    }

    @Test
    public void shouldCreateASimpleConnectedGraph()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();
        String cql = null;

        cql="CREATE (a {character: 'Doctor'}) \n ";
        cql+="CREATE (b {character: 'Master'}) \n ";
        cql+="CREATE a<-[r:ENEMY_OF]-b  \n";

        db.execute(cql);

        final Result result = db.execute(
                "MATCH (a {character: 'Doctor'})<-[:ENEMY_OF]-(b {character: 'Master'}) \n" +
                        "RETURN a, b \n" );

        /* Same geek question: if you've read ahead, what's the Big O cost of this match? */

        assertEquals( 2, result.columns().size() );
        assertEquals( 1, IteratorUtil.count(result) );
    }
}
