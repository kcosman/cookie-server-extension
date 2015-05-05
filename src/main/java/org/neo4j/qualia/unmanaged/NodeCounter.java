package org.neo4j.qualia.unmanaged;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.neo4j.graphdb.*;
import org.neo4j.helpers.collection.MapUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Path("/cookies")
public class NodeCounter {

    private static final Label COOKIE = DynamicLabel.label( "Cookie" );
    private static final String COOKIE_PID = "adnxs";
    private static final String PID = "pid";
    private static final String UID = "uid";

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                graphDb.shutdown();
            }
        });
    }

    private final ObjectMapper objectMapper;
    private GraphDatabaseService graphDb;

    public NodeCounter(@Context GraphDatabaseService graphDb) {
        this.graphDb = graphDb;
        this.objectMapper = new ObjectMapper();
        registerShutdownHook(graphDb);
    }

    @GET
    @Path("adnxs_uids")
    public Response findAllAdnxsCookieUids() {
        final Map<String, Object> params = MapUtil.map();

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write( OutputStream os) throws IOException, WebApplicationException {
                JsonGenerator jg = objectMapper.getJsonFactory().createJsonGenerator(os, JsonEncoding.UTF8);
                jg.writeStartObject();
                jg.writeFieldName(UID);
                jg.writeStartArray();

                try(Transaction tx = graphDb.beginTx();
                    ResourceIterator<Node> cookies =
                            graphDb.findNodesByLabelAndProperty(COOKIE, PID, COOKIE_PID).iterator())
                {
                    while (cookies.hasNext()) {
                        Node cookie = cookies.next();
                        jg.writeString(cookie.getProperty(UID).toString());
                    }
                    tx.success();
                }
                jg.writeEndArray();
                jg.writeEndObject();
                jg.flush();
                jg.close();
            }
        };
        return Response.ok().entity(stream).type(MediaType.TEXT_PLAIN).build();
    }
}