package semantics;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.driver.v1.*;
import org.neo4j.harness.junit.Neo4jRule;

import static org.junit.Assert.assertEquals;


/**
 * Created by jbarrasa on 21/03/2016.
 */
public class LiteOntologyImporterTest {

    @Rule
    public Neo4jRule neo4j = new Neo4jRule()
            .withProcedure( LiteOntologyImporter.class );

    @Test
    public void liteOntoImport() throws Exception {
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig())) {

            Session session = driver.session();

            StatementResult importResults =  session.run("CALL semantics.liteOntoImport('" +
                    LiteOntologyImporterTest.class.getClassLoader().getResource("moviesontology.owl").toURI()
                    + "','RDF/XML')");


            assertEquals(16L, importResults.next().get("elementsLoaded").asLong());

            assertEquals(2L, session.run("MATCH (n:Class) RETURN count(n) AS count").next().get("count").asLong());

            assertEquals(5L, session.run("MATCH (n:Property)-[:DOMAIN]->(:Class)  RETURN count(n) AS count").next().get("count").asLong());

            assertEquals(3L, session.run("MATCH (n:Property)-[:DOMAIN]->(:Relationship) RETURN count(n) AS count").next().get("count").asLong());

            assertEquals(6L, session.run("MATCH (n:Relationship) RETURN count(n) AS count").next().get("count").asLong());
        }

    }


    @Test
    public void liteOntoImportSchemaOrg() throws Exception {
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig())) {

            Session session = driver.session();

            StatementResult importResults = session.run("CALL semantics.liteOntoImport('" +
                    LiteOntologyImporterTest.class.getClassLoader().getResource("schema.rdf").toURI() +
                    "','RDF/XML')");

            assertEquals(596L, session.run("MATCH (n:Class) RETURN count(n) AS count").next().get("count").asLong());

            assertEquals(371L, session.run("MATCH (n:Property)-[:DOMAIN]->(:Class)  RETURN count(n) AS count").next().get("count").asLong());

            assertEquals(0L, session.run("MATCH (n:Property)-[:DOMAIN]->(:Relationship) RETURN count(n) AS count").next().get("count").asLong());

            assertEquals(416L, session.run("MATCH (n:Relationship) RETURN count(n) AS count").next().get("count").asLong());
        }

    }

    @Test
    public void liteOntoImportClassHierarchy() throws Exception{
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig())) {

            Session session = driver.session();

            StatementResult importResults = session.run("CALL semantics.liteOntoImport('" +
                    LiteOntologyImporterTest.class.getClassLoader().getResource("class-hierarchy-test.rdf").toURI() +
                    "','RDF/XML')");

            assertEquals(1L, session.run("MATCH p=(:Class{name:'Code'})-[:SCO]->(:Class{name:'Intangible'})" +
                    " RETURN count(p) AS count").next().get("count").asLong());
        }
    }


    @Test
    public void liteOntoImportPropHierarchy() throws Exception{
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig())) {

            Session session = driver.session();

            StatementResult importResults = session.run("CALL semantics.liteOntoImport('" +
                    LiteOntologyImporterTest.class.getClassLoader().getResource("SPOTest.owl").toURI() +
                    "','RDF/XML')");

            assertEquals(1L, session.run("MATCH p=(:Property{name:'prop1'})-[:SPO]->(:Property{name:'superprop'})" +
                    " RETURN count(p) AS count").next().get("count").asLong());
        }
    }

}
