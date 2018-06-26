
package de.unibi.citec.clf.bonsai.behavioral.config;


import java.util.List;

/**
 *
 * @author lruegeme
 */
public class BehaviorConfiguration {

    public List<Statemachine> statemachines;
    public String autoload = "";

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Configuration:\n");
        if (statemachines != null) {
            b.append("Statemachines: \n");
            statemachines.stream().forEach((sm) -> {
                b.append(" - ").append(sm).append("\n");
            });
        }

        return b.toString();
    }

    public Statemachine statemachineByName(String name) {

        for (Statemachine s : statemachines) {
            if (s.name.equals(name)) {
                return s;
            }
        }

        return null;
    }

}
