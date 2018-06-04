package jenkins.plugins.juxtapose.workflow;

import hudson.AbortException;
import hudson.Extension;
import hudson.Util;
import hudson.model.Run;
import hudson.model.TaskListener;
import jenkins.model.Jenkins;
import jenkins.plugins.juxtapose.Messages;
import jenkins.plugins.juxtapose.JuxtaposeNotifier;
import jenkins.plugins.juxtapose.JuxtaposeService;
import jenkins.plugins.juxtapose.StandardJuxtaposeService;
import org.jenkinsci.plugins.workflow.steps.AbstractStepDescriptorImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractStepImpl;
import org.jenkinsci.plugins.workflow.steps.AbstractSynchronousNonBlockingStepExecution;
import org.jenkinsci.plugins.workflow.steps.StepContextParameter;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import javax.inject.Inject;

/**
 * Workflow step to send a Juxtapose notification.
 */
public class JuxtaposeStep extends AbstractStepImpl {

    private String  event;
    private String  url;
    private boolean failOnError;

    @DataBoundConstructor
    public JuxtaposeStep(String event) {
        this.setEvent(event);
    }

    @DataBoundSetter
    public void setEvent(String event) {
        this.event = Util.fixEmpty(event);
    }

    public String getEvent() {
        return event;
    }

    @DataBoundSetter
    public void setUrl(String url) {
        this.url = Util.fixEmpty(url);
    }

    public String getUrl() {
        return url;
    }

    @DataBoundSetter
    public void setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
    }

    public boolean isFailOnError() {
        return failOnError;
    }

    @Extension
    public static class DescriptorImpl extends AbstractStepDescriptorImpl {

        public DescriptorImpl() {
            super(JuxtaposeStepExecution.class);
        }

        @Override
        public String getFunctionName() {
            return "juxtapose";
        }

        @Override
        public String getDisplayName() {
            return Messages.JuxtaposeStepDisplayName();
        }
    }

    public static class JuxtaposeStepExecution extends AbstractSynchronousNonBlockingStepExecution<Void> {

        private static final long serialVersionUID = 1L;

        @Inject
        transient JuxtaposeStep step;

        @StepContextParameter
        transient TaskListener listener;

        @StepContextParameter
        transient Run build;

        @Override
        protected Void run() throws Exception {

            // default to global config values if not set in step, but allow step to override all global settings
            Jenkins jenkins;

            // Jenkins.getInstance() may return null, no event sent in that case
            try {
                jenkins = Jenkins.getInstance();
            } catch (NullPointerException ne) {
                listener.error(Messages.NotificationFailedWithException(ne));
                return null;
            }

            JuxtaposeNotifier.DescriptorImpl juxtaposeDesc = jenkins.getDescriptorByType(JuxtaposeNotifier.DescriptorImpl.class);

            String url = step.url != null ? step.url : juxtaposeDesc.getUrl();

            if (step.getEvent() == null || step.getEvent().isEmpty()) {
                listener.getLogger().println(Messages.JuxtaposeStepNoEvent());
            } else if (url == null || url.isEmpty()) {
                // placing in console log to simplify testing of retrieving values from global config or from step field; also used for tests
                listener.getLogger().println(Messages.JuxtaposeStepSkipped());
            } else {
                JuxtaposeService juxtaposeService = getJuxtaposeService(url);
                boolean          publishSuccess   = juxtaposeService.publish(step.getEvent(), build);

                if (!publishSuccess && step.failOnError) {
                    throw new AbortException(Messages.NotificationFailed());
                } else if (!publishSuccess) {
                    listener.error(Messages.NotificationFailed());
                }
            }

            return null;
        }

        // streamline unit testing
        JuxtaposeService getJuxtaposeService(String url) {
            return new StandardJuxtaposeService(url);
        }
    }
}
