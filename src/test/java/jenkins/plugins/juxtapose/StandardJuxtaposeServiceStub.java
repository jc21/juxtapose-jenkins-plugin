package jenkins.plugins.juxtapose;

public class StandardJuxtaposeServiceStub extends StandardJuxtaposeService {

    private CloseableHttpClientStub httpClientStub;

    public StandardJuxtaposeServiceStub(String url) {
        super(url);
    }

    @Override
    public CloseableHttpClientStub getHttpClient() {
        return httpClientStub;
    }

    public void setHttpClient(CloseableHttpClientStub httpClientStub) {
        this.httpClientStub = httpClientStub;
    }
}
