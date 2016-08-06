package org.neo4j.tutorial;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Test;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItems;

import static org.neo4j.helpers.collection.IteratorUtil.asIterable;
import static org.neo4j.tutorial.matchers.ContainsOnlySpecificStrings.containsOnlySpecificStrings;

/**
 * In this Koan we focus on aggregate functions from the Cypher graph pattern matching language
 * to process some statistics about the Doctor Who universe.
 */
public class Koan7
{
    @ClassRule
    static public DoctorWhoUniverseResource neo4jResource = new DoctorWhoUniverseResource();

    @Test
    public void shouldCountTheNumberOfActorsKnownToHavePlayedTheDoctor()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService(); 
        String cql = null;

        cql = "MATCH (a:Actor)-[:PLAYED]->(c:Character{character:'Doctor'})";
        cql+= "RETURN count(a) AS numberOfActorsWhoPlayedTheDoctor";

        Result result = db.execute( cql );

        Long actorsCount = (Long) result.columnAs("numberOfActorsWhoPlayedTheDoctor").next();

        assertEquals( (long) 13, actorsCount.longValue() );
    }

    @Test
    public void shouldFindEarliestAndLatestRegenerationYears()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService(); 
        String cql = null;

        cql = "MATCH (a1)-[r:REGENERATED_TO]->(a2)";
        cql+= "RETURN max(r.year) AS latest, min(r.year) AS earliest";

        Result result = db.execute( cql );

        Map<String, Object> map = result.next();
        assertEquals( 2013l, map.get( "latest" ) );
        assertEquals( 1966l, map.get( "earliest" ) );
    }

    @Test
    public void shouldFindTheEarliestEpisodeWhereFreemaAgyemanAndDavidTennantWorkedTogether() throws Exception
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService(); 
        String cql = null;
 
        cql ="MATCH (freeman:Actor{actor:'Freema Agyeman'}) ";
        cql+="MATCH (david:Actor{actor:'David Tennant'}) ";
        cql+="MATCH (episode:Episode) ";
        cql+="MATCH (freeman)-[:PLAYED]->()-[:APPEARED_IN]->(episode) ";
        cql+="MATCH (david)-[:APPEARED_IN]->(episode) ";
        cql+="RETURN min(episode.episode) as earliest";

        Result result = db.execute( cql );

        assertEquals( "177", result.columnAs("earliest").next() );
    }

    @Test
    public void shouldFindAverageSalaryOfActorsWhoPlayedTheDoctor()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService(); 
        String cql = null;

        cql ="MATCH (d:Actor)-[:PLAYED]->(c:Character{character:'Doctor'})";
        cql+="RETURN avg(d.salary) AS cash";

        Result result = db.execute( cql );

        assertEquals( 600000.0, result.columnAs("cash").next() );
    }

    @Test
    public void shouldListTheEnemySpeciesAndCharactersForEachEpisodeWithPeterDavisonOrderedByIncreasingEpisodeNumber()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService(); 
        String cql = null;

        cql =  "MATCH (davison:Actor {actor: 'Peter Davison'})-[:APPEARED_IN]->(episode:Episode)<-[:APPEARED_IN]-" +
                "(enemy)-[:ENEMY_OF]->(:Character {character: 'Doctor'})"
                + "RETURN episode.episode, episode.title, collect(enemy.species) AS species, "
                + "collect(enemy.character) AS characters "
                + "ORDER BY episode.episode";

        Result result = db.execute( cql );

        final List<String> columnNames = result.columns();
        assertThat( columnNames,
                containsOnlySpecificStrings( "episode.episode", "episode.title", "species", "characters" ) );

        assertDavisonEpisodesRetrievedCorrectly( result );
    }

    @Test
    public void shouldFindTheEnemySpeciesThatRoseTylerFought()
    {
        GraphDatabaseService db = neo4jResource.getGraphDatabaseService(); 
        String cql = null;

        cql = "MATCH (rose:Character {character: 'Rose Tyler'})-[:APPEARED_IN]->(episode:Episode), " +
                "(doctor:Character {character:'Doctor'})-[:ENEMY_OF]->(enemy:Species)-[:APPEARED_IN]->(episode:Episode) " +
                "RETURN DISTINCT enemy.species AS enemySpecies";
        
        Result result = db.execute( cql );
        Iterator<String> enemySpecies = result.columnAs("enemySpecies");

        assertThat( asIterable( enemySpecies ),
                containsOnlySpecificStrings( "Krillitane", "Sycorax", "Cyberman", "Dalek", "Auton", "Slitheen",
                        "Clockwork Android" ) );

    }

    @SuppressWarnings("unchecked")
    private void assertDavisonEpisodesRetrievedCorrectly( Iterator<Map<String, Object>> iterator )
    {
        Map<String, Object> next = iterator.next();
        assertEquals( Arrays.asList( "Master" ), next.get( "characters" ) );
        assertEquals( "116", next.get( "episode.episode" ) );
        assertEquals( "Castrovalva", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Monarch" ), next.get( "characters" ) );
        assertEquals( "117", next.get( "episode.episode" ) );
        assertEquals( "Four to Doomsday", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Mara" ), next.get( "characters" ) );
        assertEquals( "118", next.get( "episode.episode" ) );
        assertEquals( "Kinda", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Terileptils" ), next.get( "characters" ) );
        assertEquals( "119", next.get( "episode.episode" ) );
        assertEquals( "The Visitation", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "George Cranleigh" ), next.get( "characters" ) );
        assertEquals( "120", next.get( "episode.episode" ) );
        assertEquals( "Black Orchid", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Cyberman" ), next.get( "species" ) );
        assertEquals( "121", next.get( "episode.episode" ) );
        assertEquals( "Earthshock", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Master" ), next.get( "characters" ) );
        assertEquals( "122", next.get( "episode.episode" ) );
        assertEquals( "Time-Flight", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Omega" ), next.get( "characters" ) );
        assertEquals( "123", next.get( "episode.episode" ) );
        assertEquals( "Arc of Infinity", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Mara" ), next.get( "characters" ) );
        assertEquals( "124", next.get( "episode.episode" ) );
        assertEquals( "Snakedance", next.get( "episode.title" ) );

        next = iterator.next();
        final List chars = (List) next.get( "characters" );
        assertTrue( chars.contains( "Mawdryn" ) );
        assertTrue( chars.contains( "Black Guardian" ) );
        assertEquals( "125", next.get( "episode.episode" ) );
        assertEquals( "Mawdryn Undead", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Vanir" ), next.get( "characters" ) );
        assertEquals( "126", next.get( "episode.episode" ) );
        assertEquals( "Terminus", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Black Guardian" ), next.get( "characters" ) );
        assertEquals( "127", next.get( "episode.episode" ) );
        assertEquals( "Enlightenment", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Master" ), next.get( "characters" ) );
        assertEquals( "128", next.get( "episode.episode" ) );
        assertEquals( "The King's Demons", next.get( "episode.title" ) );

        next = iterator.next();
        assertThat( (Iterable<String>) next.get( "species" ), hasItems( "Dalek" ) );
        assertThat( (Iterable<String>) next.get( "characters" ), hasItems( "Master" ) );
        assertEquals( "129", next.get( "episode.episode" ) );
        assertEquals( "The Five Doctors", next.get( "episode.title" ) );

        next = iterator.next();
        assertThat( (Iterable<String>) next.get( "species" ), hasItems( "Sea Devil", "Silurian" ) );
        assertEquals( "130", next.get( "episode.episode" ) );
        assertEquals( "Warriors of the Deep", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Malus" ), next.get( "characters" ) );
        assertEquals( "131", next.get( "episode.episode" ) );
        assertEquals( "The Awakening", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Tractator" ), next.get( "species" ) );
        assertEquals( "132", next.get( "episode.episode" ) );
        assertEquals( "Frontios", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Dalek" ), next.get( "species" ) );
        assertEquals( "133", next.get( "episode.episode" ) );
        assertEquals( "Resurrection of the Daleks", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Master" ), next.get( "characters" ) );
        assertEquals( "134", next.get( "episode.episode" ) );
        assertEquals( "Planet of Fire", next.get( "episode.title" ) );

        next = iterator.next();
        assertEquals( Arrays.asList( "Master" ), next.get( "characters" ) );
        assertEquals( "135", next.get( "episode.episode" ) );
        assertEquals( "The Caves of Androzani", next.get( "episode.title" ) );
    }
}
