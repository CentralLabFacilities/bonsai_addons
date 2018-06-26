package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.TWMActuator;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.service.ServiceClient;
import std_srvs.TriggerRequest;
import std_srvs.TriggerResponse;

import tobi_world_msgs.*;
import uuid_msgs.UniqueID;

/**
 *
 * @author lruegeme
 */
public class RosTWMActuator extends RosNode implements TWMActuator {

    String serverTopic;
    private GraphName nodeName;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    ServiceClient<FindEntitiesRequest, FindEntitiesResponse> clientFindEntities;
    ServiceClient<GetEntityRequest, GetEntityResponse> clientGetEntity;
    ServiceClient<MoveToRequest, MoveToResponse> clientNavigation;
    ServiceClient<MoveToRequest, MoveToResponse> clientNavigationView;
    ServiceClient<LoadPluginRequest, LoadPluginResponse> clientPlugin;
    ServiceClient<TriggerRequest, TriggerResponse> clientEntityCutter;

    public RosTWMActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }
    
    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.serverTopic = conf.requestValue("topic");
    }

    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        logger.fatal("on start, RosGuidingActuator done");
        try {
            clientPlugin = connectedNode.newServiceClient(serverTopic + "/load_plugin", LoadPlugin._TYPE);
            clientFindEntities = connectedNode.newServiceClient(serverTopic + "/find_entities", FindEntities._TYPE);
            clientGetEntity = connectedNode.newServiceClient(serverTopic + "/get_entity", GetEntity._TYPE);
            clientNavigation = connectedNode.newServiceClient(serverTopic + "/NavigationServer/moveTo", MoveTo._TYPE);
            clientNavigationView = connectedNode.newServiceClient(serverTopic + "/NavigationServer/getView", MoveTo._TYPE);
            //clientEntityCutter = connectedNode.newServiceClient(serverTopic + "/EntityCutter/computeAndSave", MoveTo._TYPE);
        } catch (ServiceNotFoundException e) {
            throw new RosRuntimeException(e);
        }
        initialized = true;
    }

    @Override
    public void destroyNode() {
        if(clientFindEntities!=null) clientFindEntities.shutdown();
        if(clientGetEntity!=null) clientGetEntity.shutdown();
        if(clientNavigation!=null) clientNavigation.shutdown();
        if(clientNavigationView!=null) clientNavigationView.shutdown();
        if(clientPlugin!=null) clientPlugin.shutdown();
    }

    private UniqueID nameToID(String name) {
        List<UniqueID> ids = getAllIds();
        UniqueID ret;
        for (UniqueID id : ids) {
            Entity e = getEntity(id);
            if (e.getName().equals(name)) {
                return id;
            }
        }
        logger.error("name: '" + name + "' not found, names:");
        for (UniqueID id : ids) {
            logger.error(" - '" + getEntity(id).getName() + "'");
        }
        return null;

    }

    private Entity getEntity(UniqueID id) {
        final GetEntityRequest requestGet = clientGetEntity.newMessage();
        requestGet.setId(id);
        ResponseFuture<GetEntityResponse> ret = new ResponseFuture<>();
        clientGetEntity.call(requestGet, ret);
        try {
            return ret.get().getEntity();
        } catch (InterruptedException | ExecutionException ex) {
            logger.error(ex);
            return null;
        }

    }

    private List<UniqueID> getAllIds() {

        final FindEntitiesRequest request = clientFindEntities.newMessage();
        request.setReq("");

        ResponseFuture<FindEntitiesResponse> ret = new ResponseFuture<>();
        //logger.debug("calling find_entities");
        clientFindEntities.call(request, ret);

        try {
            return ret.get().getIds();
        } catch (InterruptedException | ExecutionException ex) {
            logger.error(ex);
            return null;
        }

    }

    @Override
    public List<String> getAllEntityNames() {
        List<String> names = new LinkedList<>();

        try {
            List<UniqueID> ids = getAllIds();
            //logger.debug("have #ids: " + ids.size());
            for (UniqueID id : ids) {
                final GetEntityRequest requestGet = clientGetEntity.newMessage();
                requestGet.setId(id);
                ResponseFuture<GetEntityResponse> retGet = new ResponseFuture<>();
                clientGetEntity.call(requestGet, retGet);
                names.add(retGet.get().getEntity().getName());
                //logger.debug("added: " + retGet.get().getEntity().getName());
            }
        } catch (InterruptedException | ExecutionException ex) {
            logger.error(ex);
        }

        return names;
    }

    @Override
    public List<String> getAllEntityViews(String entity) {
        UniqueID id = nameToID(entity);
        if(id == null) {
            return null;
        }
        
        Entity e = getEntity(id);
        if(e == null) {
            return null;
        }
        
        List<String> views = new LinkedList<>();
        for(Spirit s : e.getSpirits()) {
            views.add(s.getName());
        }
        
        return views;
    }
    
    /**
     * Blocking drive using simple goal (same as rviz nav)
     * returns before nav is finished
     * 
     * @param entity
     * @param view
     * @return MoveToResponse
     */
    public Future<MoveToResponse> simpleDriveToViewGetConfig(String entity, String view) {
        final MoveToRequest req = clientNavigation.newMessage();
        req.setId(nameToID(entity));
        req.setView(view);
        final ResponseFuture<MoveToResponse> res = new ResponseFuture<>();
        clientNavigation.call(req, res);
        
        return res;
        
    }
    
    /**
     * fetch goal and config for view
     * 
     * @param entity
     * @param view
     * @return MoveToResponse
     */
    public Future<MoveToResponse> getViewConfig(String entity, String view) {
        final MoveToRequest req = clientNavigation.newMessage();
        req.setId(nameToID(entity));
        req.setView(view);
        req.setNoNav(true);
        req.setForceMove(false);
        final ResponseFuture<MoveToResponse> res = new ResponseFuture<>();
        clientNavigation.call(req, res);
        
        return res;
        
    }
    
    /**
     * fetch goal and config for view
     * 
     * @param entity
     * @param view
     * @return MoveToResponse
     */
    public Future<MoveToResponse> getViewConfigCurrentPose(String entity, String view) {
        final MoveToRequest req = clientNavigationView.newMessage();
        req.setId(nameToID(entity));
        req.setView(view);
        req.setNoNav(true);
        req.setForceMove(false);
        final ResponseFuture<MoveToResponse> res = new ResponseFuture<>();
        clientNavigationView.call(req, res);
        
        return res;
        
    }

    /**
     * Starts navigation by simple sending of goal.
     * returns immediately
     * 
     * @param entity
     * @param view
     * @return 
     */
    @Override
    public Future<Boolean> simpleDriveToView(String entity, String view) {
        final MoveToRequest req = clientNavigation.newMessage();
        req.setId(nameToID(entity));
        req.setView(view);
        req.setForceMove(true);
        final ResponseFuture<MoveToResponse> res = new ResponseFuture<>();
        clientNavigation.call(req, res);
        
        return res.toBooleanFuture();
    }

    @Override
    public Future<Boolean> simpleTriggerMatching(String entity, String view) {
        final LoadPluginRequest req = clientPlugin.newMessage();
        req.setPkg("tobi_world_plugins");
        req.setRunOnce(true);
        req.setType("twm::GICPPlugin");
        req.setParam("cloud.topic=/xtion/depth/points match.max_dist=5 align.max_score=99 align.type=icp align.convergence=false align.max_iterations=100 pose.max_dist=2");
        final ResponseFuture<LoadPluginResponse> res = new ResponseFuture<>();
        clientPlugin.call(req, res);
        return res.toBooleanFuture();
    }
    
    public Future<Boolean> entityCutterTrigger() {
        final TriggerRequest req = clientEntityCutter.newMessage();
        final ResponseFuture<TriggerResponse> res = new ResponseFuture<>();
        clientEntityCutter.call(req, res);
        return res.toBooleanFuture();
    }

}
