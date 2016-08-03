package org.neo4j.tutorial;

import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import scala.collection.convert.Wrappers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * In this Koan we learn how to create, update, and erase properties and labels in Cypher.
 */
public class Koan4
{
    
    @ClassRule
    static public Neo4jEmbeddedResource neo4jResource = new Neo4jEmbeddedResource();
    
    @Test
    public void shouldCreateAnUnlabelledNodeWithActorPropertyToRepresentDavidTennant()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();

        String cql = null;

        cql="CREATE (a {actor:'David Tennant'}) return a.actor";

        db.execute( cql );

        final Result result = db.execute( "MATCH (a {actor: 'David Tennant'}) RETURN a.actor" );

        assertEquals( "David Tennant", result.columnAs("a.actor").next() );
    }

    @Test
    public void shouldAddOriginalNamePropertyForDavidTennantNode()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();

        db.execute( "CREATE ({actor: 'David Tennant'}) " );

        String cql = 	"MATCH (a {actor: 'David Tennant'})\n";
        cql += 			"SET a.original_name='David McDonald'";

        db.execute( cql );

        final Result result = db.execute( "MATCH (a {actor: 'David Tennant'}) RETURN a" +
                ".original_name" );

        assertEquals( "David McDonald", result.columnAs("a.original_name").next() );
    }

    @Test
    public void shouldChangeOriginalNamePropertyForDavidTennantNodeToSomethingComical()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();

        db.execute( "CREATE ({actor: 'David Tennant', original_name: 'David McDonald'}) " );

        String cql = "MATCH (a {actor: 'David Tennant'})\n";

        cql += "SET a.original_name='Ronald McDonald'";

        db.execute( cql );

        final Result result = db.execute( "MATCH (a {actor: 'David Tennant'}) RETURN a" +
                ".original_name" );

        assertEquals( "Ronald McDonald", result.columnAs("a.original_name").next() );
    }


    @Test
    public void shouldCreateAnActorLabelledNodeRepresentingDavidTennant()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();


        String cql = null;

        cql ="CREATE (a:Actor {actor: 'David Tennant'})";

        db.execute( cql );

        final Result result = db.execute( "MATCH (a:Actor {actor: 'David Tennant'}) RETURN a.actor" );

        assertEquals( "David Tennant", result.columnAs("a.actor").next() );
    }

    @Test
    public void shouldAddScottishNationalityLabelToAnExistingDavidTennantNode()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();

        db.execute( "CREATE (:Actor {actor: 'David Tennant'})" );

        String cql = "MATCH (a {actor: 'David Tennant'})\n";

        cql+="SET a:Scottish";

        db.execute( cql );

        final Result result = db.execute( "MATCH (a:Scottish {actor: 'David Tennant'}) RETURN " +
                "labels(a)" );

        Wrappers.SeqWrapper wrapper = (Wrappers.SeqWrapper) result.columnAs("labels(a)").next();
        assertTrue( wrapper.contains( "Scottish" ) );
    }

    @Test
    public void shouldAddActorMaleAndScottishLabelsToAnExistingDavidTennantNode()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();

        db.execute( "CREATE (:Actor {actor: 'David Tennant'})" );

        String cql = "MATCH (a:Actor {actor: 'David Tennant'})\n";

        cql+="SET a:Actor:Male:Scottish";

        db.execute( cql );

        final Result result = db.execute( "MATCH (a {actor: 'David Tennant'}) RETURN labels(a)" );

        Wrappers.SeqWrapper wrapper = (Wrappers.SeqWrapper) result.columnAs("labels(a)").next();
        assertTrue( wrapper.contains( "Male" ) );
        assertTrue( wrapper.contains( "Actor" ) );
        assertTrue( wrapper.contains( "Scottish" ) );
    }
}
