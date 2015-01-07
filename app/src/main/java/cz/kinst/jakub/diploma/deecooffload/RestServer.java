package cz.kinst.jakub.diploma.deecooffload;


import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.engine.connector.HttpClientHelper;
import org.restlet.ext.gson.GsonConverter;
import org.restlet.ext.simple.HttpServerHelper;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import java.util.Date;

public class RestServer extends ServerResource {

    public static void start() throws Exception {
        Engine.getInstance().getRegisteredClients().clear();
        Engine.getInstance().getRegisteredClients().add(new HttpClientHelper(null));
        Engine.getInstance().getRegisteredConverters().add(new GsonConverter());
        Engine.getInstance().getRegisteredServers().clear();
        Engine.getInstance().getRegisteredServers().add(new HttpServerHelper(null)); // Simple

        // Create the HTTP server and listen on port 8182
        Server server = new Server(Protocol.HTTP, 8182, RestServer.class);
        server.start();
    }

    class Message {
        public String message;
        public long timestamp;

        Message(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
        }
    }

    @Get
    public Message getMessage() {
        return new Message("Hello from " + android.os.Build.MODEL + "!", new Date().getTime());
    }

}
