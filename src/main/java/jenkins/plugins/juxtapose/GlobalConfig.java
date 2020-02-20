package jenkins.plugins.juxtapose;

import hudson.Extension;
import net.sf.json.JSONObject;
import jenkins.model.GlobalConfiguration;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import hudson.util.FormValidation;

@Extension
public class GlobalConfig extends GlobalConfiguration {

    private String juxtaposeWebhookURL;

    public GlobalConfig() {
        load();
    }

    public String getJuxtaposeWebhookURL() {
        return juxtaposeWebhookURL;
    }

    public void setJuxtaposeWebhookURL(String juxtaposeWebhookURL) {
        this.juxtaposeWebhookURL = juxtaposeWebhookURL;
    }

    public FormValidation doCheckJuxtaposeWebhookURL(@QueryParameter String value) {
        if (value == null || value.trim().isEmpty()) {
            return FormValidation.warning("Please set a url endpoint");
        }

        return FormValidation.ok();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }
}
