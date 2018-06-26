
package de.unibi.citec.clf.bonsai.behavioral.config;



import java.util.Map;

/**
 *
 * @author lruegeme
 */
public class Action {
    
    public String name;
    public String target;
    public Map<String,String> params;
    
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Action:").append(name).append(" target:").append(target).append(" Params:");
        if(params!=null) params.keySet().stream().forEach((key) -> {
            b.append("[").append(key).append("=").append(params.get(key)).append("]");
        });
        return b.toString();
    }
    
}
