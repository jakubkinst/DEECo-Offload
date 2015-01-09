package cz.kinst.jakub.offloading;

import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.engine.connector.HttpClientHelper;
import org.restlet.ext.gson.GsonConverter;
import org.restlet.ext.simple.HttpServerHelper;
import org.restlet.resource.ClientResource;
import org.restlet.routing.Router;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakubkinst on 09/01/15.
 */
public class OffloadingManager {
    private final int mPort;
    private final Component mServerComponent;
    private final Router mRouter;
    private List<OffloadingResourceImpl> mResources = new ArrayList<>();

    public OffloadingManager(int port) {
        Engine.getInstance().getRegisteredClients().clear();
        Engine.getInstance().getRegisteredClients().add(new HttpClientHelper(null));
        Engine.getInstance().getRegisteredConverters().add(new GsonConverter());
        Engine.getInstance().getRegisteredServers().clear();
        Engine.getInstance().getRegisteredServers().add(new HttpServerHelper(null)); // Simple Server Connector

        this.mPort = port;

        mServerComponent = new Component();
        mServerComponent.getServers().add(Protocol.HTTP, port);
        mRouter = new Router(mServerComponent.getContext().createChildContext());
        mServerComponent.getDefaultHost().attach(mRouter);
    }

    public void startServing() throws Exception {
        mServerComponent.start();
    }

    public void stopServing() throws Exception {
        mServerComponent.stop();
    }

    public void attachResource(OffloadingResourceImpl resource) {
        mResources.add(resource);
        mRouter.attach(resource.getPath(), resource.getClass());
    }

    public Component getServerComponent() {
        return mServerComponent;
    }

    public int getPort() {
        return mPort;
    }

    public <T> T getResourceProxy(Class<T> resourceInterface, String host) {
        for (OffloadingResourceImpl res : mResources) {
            if (resourceInterface.isAssignableFrom(res.getClass())) {
                ClientResource cr = new ClientResource(getUrl(host, res.getPath()));
                return cr.wrap(resourceInterface);
            }
        }
        throw new IllegalArgumentException("No Resource of implementing " + resourceInterface.getName() + " was registered.");
    }

    public String getUrl(String host, String resourcePath) {
        return "http://" + host + ":" + getPort() + resourcePath;
    }
}
