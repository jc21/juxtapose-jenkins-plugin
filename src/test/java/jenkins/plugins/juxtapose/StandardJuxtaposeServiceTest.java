package jenkins.plugins.juxtapose;

import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StandardJuxtaposeServiceTest {

    @Test
    public void successfulPublishReturnsTrue() {
        StandardJuxtaposeServiceStub service = new StandardJuxtaposeServiceStub("");
        CloseableHttpClientStub httpClientStub = new CloseableHttpClientStub();
        httpClientStub.setHttpStatus(HttpStatus.SC_OK);
        service.setHttpClient(httpClientStub);
        assertTrue(service.publish("message"));
    }

    @Test
    public void failedPublishReturnsFalse() {
        StandardJuxtaposeServiceStub service = new StandardJuxtaposeServiceStub("invalidurl");
        CloseableHttpClientStub httpClientStub = new CloseableHttpClientStub();
        httpClientStub.setHttpStatus(HttpStatus.SC_NOT_FOUND);
        service.setHttpClient(httpClientStub);
        assertFalse(service.publish("message"));
    }
}
