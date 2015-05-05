Qualia appnexus Neo4j unmanaged extension
================================

This is an unmanaged extension. 

0. Stop neo4j

1. Build it:

        mvn clean package

2. Copy target/cookie-extension-1.0.jar to the plugins/qualia directory of your Neo4j server.

3. Configure Neo4j by adding a line to conf/neo4j-server.properties:

        org.neo4j.server.thirdparty_jaxrs_classes=org.neo4j.qualia.unmanaged=/qualia

4. Start Neo4j server.

5. Query it over HTTP -ideally in a screen and using the -o command which writes the response to disk:

        curl -o output http://localhost:7474/qualia/cookies/adnxs_uids

Next steps: automation