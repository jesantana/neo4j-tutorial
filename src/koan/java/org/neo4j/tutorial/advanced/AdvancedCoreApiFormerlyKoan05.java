package org.neo4j.tutorial.advanced;

import java.util.HashSet;
import java.util.Set;

import org.junit.ClassRule;
import org.junit.Test;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.tutorial.DoctorWhoRelationships;
import org.neo4j.tutorial.DoctorWhoUniverseResource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;
import static org.neo4j.tutorial.DoctorWhoLabels.CHARACTER;
import static org.neo4j.tutorial.DoctorWhoLabels.SPECIES;
import static org.neo4j.tutorial.DoctorWhoRelationships.APPEARED_IN;
import static org.neo4j.tutorial.matchers.ContainsOnlyHumanCompanions.containsOnlyHumanCompanions;
import static org.neo4j.tutorial.matchers.ContainsOnlySpecificTitles.containsOnlyTitles;

/**
 * In this Koan we start to mix indexing and core API to perform more targeted
 * graph operations. We'll mix indexes and core graph operations to explore the
 * Doctor's universe.
 */

// TODO: Move this to the core API koans

public class AdvancedCoreApiFormerlyKoan05
{
    @ClassRule
    static public DoctorWhoUniverseResource neo4jResource = new DoctorWhoUniverseResource();

    @Test
    public void shouldCountTheNumberOfDoctorsRegeneratedForms()
    {
        GraphDatabaseService database = neo4jResource.getGraphDatabaseService();

        try ( Transaction tx = database.beginTx() )
        {
            Node doctor = neo4jResource.theDoctor();

            int numberOfRegenerations = 0;

            // YOUR CODE GOES HERE

            assertEquals( 12, numberOfRegenerations );
            tx.success();
        }
    }

    @Test
    public void shouldFindHumanCompanionsUsingCoreApi()
    {
        Set<Node> humanCompanions = new HashSet<>();

        GraphDatabaseService database = neo4jResource.getGraphDatabaseService();

        try ( Transaction tx = database.beginTx() )
        {

            // YOUR CODE GOES HERE

            int numberOfKnownHumanCompanions = 40;
            assertEquals( numberOfKnownHumanCompanions, humanCompanions.size() );
            assertThat( humanCompanions, containsOnlyHumanCompanions() );
            tx.success();
        }
    }

    @Test
    public void shouldFindAllEpisodesWhereRoseTylerFoughtTheDaleks()
    {
        GraphDatabaseService database = neo4jResource.getGraphDatabaseService();

        try ( Transaction tx = database.beginTx() )
        {
            HashSet<Node> episodesWhereRoseFightsTheDaleks = new HashSet<>();

            // YOUR CODE GOES HERE

            tx.success();
            assertThat( episodesWhereRoseFightsTheDaleks,
                    containsOnlyTitles( neo4jResource.getGraphDatabaseService(), "Army of Ghosts", "The Stolen Earth", "Doomsday",
                            "Journey's End", "Bad Wolf",
                            "The Parting of the Ways", "Dalek" ) );
        }
    }
}
