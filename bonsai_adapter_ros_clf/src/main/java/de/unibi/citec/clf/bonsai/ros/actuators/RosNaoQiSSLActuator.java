package de.unibi.citec.clf.bonsai.ros.actuators;

import de.unibi.citec.clf.bonsai.actuators.SSLActuator;
import de.unibi.citec.clf.bonsai.actuators.data.SoundSourceLocalizationResult;
import de.unibi.citec.clf.bonsai.core.configuration.IObjectConfigurator;
import de.unibi.citec.clf.bonsai.core.exception.ConfigurationException;
import de.unibi.citec.clf.bonsai.ros.RosNode;
import de.unibi.citec.clf.bonsai.ros.helper.ResponseFuture;
import de.unibi.citec.clf.btl.data.common.Timestamp;
import de.unibi.citec.clf.btl.units.TimeUnit;
import org.ros.exception.ServiceNotFoundException;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import pepper_clf_msgs.SynchronizedSSL;
import pepper_clf_msgs.SynchronizedSSLRequest;
import pepper_clf_msgs.SynchronizedSSLResponse;
import org.ros.node.service.ServiceClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * @author ffriese
 */
public class RosNaoQiSSLActuator extends RosNode implements SSLActuator {

    String topic;
    private GraphName nodeName;
    private ServiceClient<SynchronizedSSLRequest, SynchronizedSSLResponse> sc;
    private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());

    public RosNaoQiSSLActuator(GraphName gn) {
        initialized = false;
        this.nodeName = gn;
    }

    @Override
    public void configure(IObjectConfigurator conf) throws ConfigurationException {
        this.topic = conf.requestValue("topic");
    }


    @Override
    public GraphName getDefaultNodeName() {
        return nodeName;
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        try {
            sc = connectedNode.newServiceClient(this.topic, SynchronizedSSL._TYPE);
        } catch (ServiceNotFoundException e) {
            e.printStackTrace();
        }
        initialized = true;
        logger.debug("on start, RosNaoQiSSLActuator done");
    }

    @Override
    public void destroyNode() {
        if(sc != null) sc.shutdown();
    }


    @Override
    public Future<SoundSourceLocalizationResult> getAverageAngle(Timestamp begin, Timestamp end){
        SynchronizedSSLRequest req = sc.newMessage();

        req.setStart(Time.fromMillis(begin.getCreated(TimeUnit.MILLISECONDS)));
        req.setEnd(Time.fromMillis(end.getCreated(TimeUnit.MILLISECONDS)));

        final ResponseFuture<SynchronizedSSLResponse> res = new ResponseFuture<>();

        if (sc.isConnected()) {
            sc.call(req, res);
        }else{
            return null; //TODO: better exception handling
        }

        return new Future<SoundSourceLocalizationResult>() {
            @Override
            public boolean cancel(boolean b) {
                return res.cancel(b);
            }

            @Override
            public boolean isCancelled() {
                return res.isCancelled();
            }

            @Override
            public boolean isDone() {
                return res.isDone();
            }

            @Override
            public SoundSourceLocalizationResult get() throws InterruptedException, ExecutionException {
                SynchronizedSSLResponse r = res.get();
                return new SoundSourceLocalizationResult(r.getValid(), r.getAngle());
            }

            @Override
            public SoundSourceLocalizationResult get(long l, java.util.concurrent.TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                SynchronizedSSLResponse r = res.get(l, timeUnit);
                return new SoundSourceLocalizationResult(r.getValid(), r.getAngle());
            }
        };



    }
}
