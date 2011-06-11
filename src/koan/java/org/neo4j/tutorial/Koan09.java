package org.neo4j.tutorial;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.server.rest.domain.JsonHelper;
import org.neo4j.tutorial.server.rest.RelationshipDescription;
import org.neo4j.tutorial.server.rest.TraversalDescription;
import org.neo4j.tutorial.server.rest.domain.EpisodeSearchResult;
import org.neo4j.tutorial.server.rest.domain.EpisodeSearchResults;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * In this Koan we use the REST API to explore the Doctor Who universe.
 */
public class Koan09 {

	private static ServerDoctorWhoUniverse universe;

	@BeforeClass
	public static void createDatabase() throws Exception {
		universe = new ServerDoctorWhoUniverse();
	}

	@AfterClass
	public static void closeTheDatabase() {
		universe.stop();
	}

	@Test
	public void shouldCountTheEnemiesOfTheDoctor() throws Exception {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);	
		
		String response = null;
		
		// SNIPPET_START
		
		WebResource resource = client.resource(universe.theDoctor().get("incoming_relationships") + "/ENEMY_OF");
		response = resource.accept(MediaType.APPLICATION_JSON).get(String.class);
		
		// SNIPPET_END
		
		List<Map<String,Object>> json = JsonHelper.jsonToList(response);
		assertEquals(140, json.size());
	}
	
	@Test
	public void shouldIdentifyWhichDoctorsTookPartInInvasionStories() throws Exception {
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);	
		
		String response = null;
		
		// SNIPPET_START
		
		TraversalDescription traversal = new TraversalDescription();
		traversal.setOrder("depth first");
		traversal.setUniqueness("node path");
		traversal.setRelationships(new RelationshipDescription("PLAYED", RelationshipDescription.IN), 
				new RelationshipDescription("APPEARED_IN", RelationshipDescription.OUT));
		traversal.setReturnFilter("position.endNode().hasProperty('title') && position.endNode().getProperty('title').contains('Invasion')");
		traversal.setMaxDepth(3);
		
		WebResource resource = client.resource(universe.theDoctor().get("traverse").toString().replace("{returnType}", "fullpath"));
		response = resource.accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).post(String.class, traversal.toJson());

		// SNIPPET_END
		
		EpisodeSearchResults results = new EpisodeSearchResults(JsonHelper.jsonToList(response));
		for (EpisodeSearchResult result : results){
			System.out.println(result.getActor() + ": " + result.getEpisode());
		}
	}
	

}
		