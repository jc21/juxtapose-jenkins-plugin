package jenkins.plugins.juxtapose;

public class JuxtaposeNotifierStub extends JuxtaposeNotifier {

    public JuxtaposeNotifierStub(String url) {
        super(url);
    }

    public static class DescriptorImplStub extends JuxtaposeNotifier.DescriptorImpl {

        private JuxtaposeService juxtaposeService;

        @Override
        public synchronized void load() {
        }

        @Override
        JuxtaposeService getJuxtaposeService(final String url) {
            return juxtaposeService;
        }

        public void setJuxtaposeService(JuxtaposeService juxtaposeService) {
            this.juxtaposeService = juxtaposeService;
        }
    }
}
