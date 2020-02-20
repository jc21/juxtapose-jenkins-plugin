package jenkins.plugins.juxtapose;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;

@SuppressWarnings("rawtypes")
public class ActiveNotifier implements FineGrainedNotifier {

    JuxtaposeNotifier notifier;
    BuildListener listener;

    public ActiveNotifier(JuxtaposeNotifier notifier, BuildListener listener) {
        super();
        this.notifier = notifier;
        this.listener = listener;
    }

    private JuxtaposeService getJuxtapose(AbstractBuild build) {
        return notifier.newJuxtaposeService(build, listener);
    }

    public void deleted(AbstractBuild build) {
    }

    public void started(AbstractBuild build) {
    }

    public void finalized(AbstractBuild build) {
    }

    public void completed(AbstractBuild build) {
        Result result = build.getResult();

        if (result == Result.ABORTED) {
            getJuxtapose(build).publish("aborted", build);
        } else if (result == Result.FAILURE) {
            getJuxtapose(build).publish("failure", build);
        } else if (result == Result.UNSTABLE) {
            getJuxtapose(build).publish("unstable", build);
        } else if (result == Result.SUCCESS) {
            getJuxtapose(build).publish("success", build);
        }
    }

}
