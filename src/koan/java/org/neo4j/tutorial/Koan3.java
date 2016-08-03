package org.neo4j.tutorial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.neo4j.helpers.collection.IteratorUtil.asIterable;
import static org.neo4j.tutorial.matchers.ContainsOnlySpecificTitles.containsOnlyTitles;
import static org.neo4j.tutorial.matchers.ContainsWikipediaEntries.containsExactlyWikipediaEntries;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Result;
import org.neo4j.helpers.collection.IteratorUtil;

/**
 * In this Koan we learn the basics of the Cypher query language, focusing on the
 * MATCH clause to RETURN subgraphs of information about the Doctor Who
 * universe.
 */
public class Koan3
{

    @ClassRule
    static public DoctorWhoUniverseResource neo4jResource = new DoctorWhoUniverseResource();

    @Test
    
    public void shouldFindAndReturnTheDoctor()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();
        String cql = null;

        cql="MATCH(doctor:Character {character: 'Doctor'})  RETURN doctor";

        Result result = db.execute( cql );
        Iterator<Node> containsTheDoctor = result.columnAs("doctor" );

        assertEquals( containsTheDoctor.next(), neo4jResource.theDoctor() );
    }

    @Test
    public void shouldFindAllTheEpisodesUsingLabels()
    {
        // The number of episodes is not the same as the highest episode number.
        // Some episodes are two-parters with the same episode number, others use schemes like
        // 218a and 218b as their episode numbers seemingly just to be difficult!

        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();
        String cql = null;

        cql ="MATCH (episode:Episode) RETURN episode";

        Result result = db.execute( cql );

        Iterator<String> iterator = result.columnAs("episode");
        assertEquals( 266l, IteratorUtil.count(iterator) );
    }

    @Test
    public void shouldFindAllTheEpisodesInWhichTheCybermenAppeared() throws Exception
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();
        String cql = null;

        cql="MATCH(episode:Episode)<-[:APPEARED_IN]-(specie:Species{species:'Cyberman'}) return episode";

        Result result = db.execute( cql );

        Iterator<Node> episodes = result.columnAs("episode");

        assertThat( asIterable( episodes ), containsOnlyTitles( db,
                "Closing Time",
                "A Good Man Goes to War",
                "The Pandorica Opens",
                "The Next Doctor",
                "Doomsday",
                "Army of Ghosts",
                "The Age of Steel",
                "Rise of the Cybermen",
                "Silver Nemesis",
                "Earthshock",
                "Revenge of the Cybermen",
                "The Wheel in Space",
                "The Tomb of the Cybermen",
                "The Moonbase",
                "The Time of the Doctor",
                "Nightmare in Silver",
                "The Tenth Planet") );
    }

    @Test
    public void shouldFindEpisodesWhereTennantAndRoseBattleTheDaleks() throws Exception
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();
        String cql = null;


        cql = "MATCH (episode:Episode) \n";
        cql+= "MATCH (episode)<-[:APPEARED_IN]-(specie:Species{species:'Dalek'}) \n";
        cql+= "MATCH (episode)<-[:APPEARED_IN]-(actor:Actor{actor:'David Tennant'}) \n";
        cql+= "MATCH (episode)<-[:APPEARED_IN]-(char:Character{character:'Rose Tyler'}) \n";
        cql+= "RETURN episode \n";

        Result result = db.execute( cql );

        Iterator<Node> episodes = result.columnAs("episode");

        assertThat( asIterable( episodes ),
                containsOnlyTitles( db,
                        "Journey's End",
                        "The Stolen Earth",
                        "Doomsday",
                        "Army of Ghosts",
                        "The Parting of the Ways" ) );
    }

    @Test
    public void shouldReturnAnyWikpediaEntriesForCompanions()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();
        String cql = null;

        cql = "MATCH (companion:Character)-[:COMPANION_OF]->(doctor:Character{character:'Doctor'}) \n";
        cql+= "where has(companion.wikipedia) \n";
        cql+= "RETURN companion.wikipedia \n";

        Result result = db.execute( cql );
        Iterator<String> iterator = result.columnAs("companion.wikipedia");

        assertThat( iterator, containsExactlyWikipediaEntries( "http://en.wikipedia.org/wiki/Rory_Williams",
                "http://en.wikipedia.org/wiki/Amy_Pond",
                "http://en.wikipedia.org/wiki/River_Song_(Doctor_Who)",
                "http://en.wikipedia.org/wiki/Clara_Oswald" ) );
    }

    @Test
    public void shouldFindIndividualCompanionsAndEnemiesOfTheDoctor()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService();
        

        String cql = "MATCH (doctor:Character{character:'Doctor'})"
        +"MATCH (other:Character)-[:COMPANION_OF|ENEMY_OF]->(doctor)"
        +"WHERE has(other.character)"
        +"RETURN DISTINCT other.character";

        Result result = db.execute(cql);

        assertEquals(162, IteratorUtil.count(result));
    }
}
