package jenkins.plugins.juxtapose;

import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;

public class JuxtaposeNotifier extends Notifier {

    private String url;

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public String getUrl() {
        return url;
    }

    @DataBoundConstructor
    public JuxtaposeNotifier() {
        super();
    }

    public JuxtaposeNotifier(final String url) {
        super();
        this.url = url;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    public JuxtaposeService newJuxtaposeService(AbstractBuild build, BuildListener listener) {
        String webhookUrl = this.url;
        EnvVars env;

        if (StringUtils.isEmpty(webhookUrl)) {
            webhookUrl = getDescriptor().getUrl();
        }

        try {
            env = build.getEnvironment(listener);
        } catch (Exception e) {
            listener.getLogger().println("Error retrieving environment vars: " + e.getMessage());
            env = new EnvVars();
        }

        webhookUrl = env.expand(webhookUrl);

        return new StandardJuxtaposeService(webhookUrl);
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        return true;
    }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        return super.prebuild(build, listener);
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            load();
        }

        public String getUrl() {
            return GlobalConfiguration.all().get(GlobalConfig.class).getJuxtaposeWebhookURL();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public JuxtaposeNotifier newInstance(StaplerRequest sr, JSONObject json) {
            return new JuxtaposeNotifier(GlobalConfiguration.all().get(GlobalConfig.class).getJuxtaposeWebhookURL());
        }

        @Override
        public boolean configure(StaplerRequest sr, JSONObject formData) throws FormException {
            save();
            return super.configure(sr, formData);
        }

        JuxtaposeService getJuxtaposeService(final String webhookUrl) {
            return new StandardJuxtaposeService(webhookUrl);
        }

        @Override
        public String getDisplayName() {
            return "Juxtapose";
        }

    }

    @Deprecated
    public static class JuxtaposeJobProperty extends hudson.model.JobProperty<AbstractProject<?, ?>> {
        /*
         * @DataBoundConstructor public JuxtaposeJobProperty() { }
         */
        @Override
        public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
            return super.prebuild(build, listener);
        }
    }
}
