
package de.unibi.citec.clf.bonsai.behavioral.config;




import java.util.List;
import java.util.Map;
import java.util.regex.*;

/**
 *
 * @author lruegeme
 */
public class Statemachine {

    public String name;
    public String task;
    public String config;
    public List<Action> actions;

    private boolean expanded = false;
    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Statemachine.class);

    public String getConfig() {
        if(!expanded) {
            ExpandEnv();
        }
        return config;
    }

    public String getTask() {
        if(!expanded) {
            ExpandEnv();
        }
        return task;
    }

    private void ExpandEnv() {
        config = ExpandEnv(config);
        task = ExpandEnv(task);
        expanded = true;
        logger.debug("Expanded env " + "\ntask: " + task + "\nconfig: " + config);
    }

    private String ExpandEnv(String in) {
        String out = in;
        Map<String, String> envMap = System.getenv();
        String pattern = "\\$\\{([A-Za-z0-9_-]+)\\}";
        Pattern expr = Pattern.compile(pattern);
        Matcher matcher = expr.matcher(out);
        while (matcher.find()) {
            String envName = matcher.group(1).toUpperCase();
            String envValue = envMap.get(envName);
            logger.debug("replaced " + envName + " with " + envValue);
            if (envValue == null) {
                envValue = "";
            }
            Pattern subexpr = Pattern.compile(Pattern.quote(matcher.group(0)));
            out = subexpr.matcher(out).replaceAll(envValue);
        }
        return out;
    }
    
    public Action byName(String name) {
        
        for(Action a : actions) {
            if(a.name.equals(name)) {
                return a;
            }
        }
        
        return null;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Name:").append(name).append(" ");
        b.append("Actions: \n");
        if(actions != null)
        actions.stream().forEach((a) -> {
            b.append("   - ").append(a).append("\n");
        });
        return b.toString();
    }

}
