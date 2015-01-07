package cz.kinst.jakub.diploma.deecooffload;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.engine.connector.HttpClientHelper;
import org.restlet.ext.gson.GsonConverter;
import org.restlet.ext.simple.HttpServerHelper;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

/**
 * Created by jakubkinst on 07/01/15.
 */
public class ResourceImpl extends ServerResource {

    private Component serverComponent;
    private int mPort;
    private String mUri;


    public ResourceImpl() {
    }

    public ResourceImpl(String uri, int port) throws Exception {
        Engine.getInstance().getRegisteredClients().clear();
        Engine.getInstance().getRegisteredClients().add(new HttpClientHelper(null));
        Engine.getInstance().getRegisteredConverters().add(new GsonConverter());
        Engine.getInstance().getRegisteredServers().clear();
        Engine.getInstance().getRegisteredServers().add(new HttpServerHelper(null)); // Simple Server Connector

        this.mPort = port;
        this.mUri = uri;

        serverComponent = new Component();
        serverComponent.getServers().add(Protocol.HTTP, port);
        final Router router = new Router(serverComponent.getContext().createChildContext());
        router.attach(uri, ResourceImpl.this.getClass());
        serverComponent.getDefaultHost().attach(router);
    }

    public void startServing() throws Exception {
        serverComponent.start();
    }

    public void stopServing() throws Exception {
        serverComponent.stop();
    }

    public <T> T getProxy(Class<T> type, String host) {
        ClientResource cr = new ClientResource(getUrl(host));
        return cr.wrap(type);
    }

    public String getUrl(String host) {
        return "http://" + host + ":" + getPort() + getUri();
    }

    public Component getServerComponent() {
        return serverComponent;
    }

    public int getPort() {
        return mPort;
    }

    public String getUri() {
        return mUri;
    }
}
