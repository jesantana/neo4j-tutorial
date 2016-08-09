package org.neo4j.tutorial;

import org.junit.ClassRule;
import org.junit.Test;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;

import static org.junit.Assert.assertEquals;

/**
 * In this koan we call out to the (small!) algorithm library exposed to Cypher to peform shortest path calculations.
 */
public class Koan11
{
    @ClassRule
    static public DoctorWhoUniverseResource neo4jResource = new DoctorWhoUniverseResource();

    @Test
    public void shouldFindLengthOfTheShortestPathBetweenSarahJaneSmithAndSkaro() throws Exception
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();
        String cql = null;

        cql = "MATCH (sarah:Character{character:'Sarah Jane Smith'}) ";
        cql+= "MATCH (skaro:Planet{planet:'Skaro'}) ";
        cql+= "RETURN length(shortestPath((sarah)-[*..50]-(skaro))) as length";

        Result result = db.execute( cql );

        assertEquals( 3, result.columnAs( "length" ).next() );
    }
}