package jenkins.plugins.juxtapose;


import net.sf.json.JSONObject;
import hudson.model.Run;

public interface JuxtaposeService {
    boolean publish(String event);
    boolean publish(String event, Run build);
    boolean publish(JSONObject data);
}
