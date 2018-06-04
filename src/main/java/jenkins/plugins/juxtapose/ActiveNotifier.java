package jenkins.plugins.juxtapose;

import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Result;

@SuppressWarnings("rawtypes")
public class ActiveNotifier implements FineGrainedNotifier {

    JuxtaposeNotifier notifier;
    BuildListener     listener;

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
        /*
        AbstractProject<?, ?> project       = build.getProject();
        Result                result        = build.getResult();
        AbstractBuild<?, ?>   previousBuild = project.getLastBuild();

        do {
            previousBuild = previousBuild.getPreviousCompletedBuild();
        } while (previousBuild != null && previousBuild.getResult() == Result.ABORTED);

        Result previousResult = (previousBuild != null) ? previousBuild.getResult() : Result.SUCCESS;

        if ((result.isWorseThan(previousResult) || moreTestFailuresThanPreviousBuild(build, previousBuild))) {
            // Regression:
            getJuxtapose(build).publish("regression", build);
        }
        */
    }

    public void completed(AbstractBuild build) {
        Result result = build.getResult();
        /*
        AbstractProject<?, ?> project       = build.getProject();

        AbstractBuild<?, ?>   previousBuild = project.getLastBuild();

        do {
            try {
                previousBuild = previousBuild.getPreviousCompletedBuild();
            } catch (NullPointerException e) {
                previousBuild = null;
            }
        } while (previousBuild != null && previousBuild.getResult() == Result.ABORTED);

        Result previousResult = (previousBuild != null) ? previousBuild.getResult() : Result.SUCCESS;
        */
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
