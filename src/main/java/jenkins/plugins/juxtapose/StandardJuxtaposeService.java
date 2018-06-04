package jenkins.plugins.juxtapose;

import hudson.model.*;

import net.sf.json.JSONObject;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;

import jenkins.model.Jenkins;
import hudson.ProxyConfiguration;

import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;

public class StandardJuxtaposeService implements JuxtaposeService {

    private static final Logger logger = Logger.getLogger(StandardJuxtaposeService.class.getName());

    private String url;

    public StandardJuxtaposeService(String webhookUrl) {
        super();
        this.url = webhookUrl;
    }

    public boolean publish(String event) {
        // prepare base data
        JSONObject data = new JSONObject();
        data.put("version", 1);

        if (event != null && !event.isEmpty()) {
            data.put("event", event);
        }

        return publish(data);
    }

    public boolean publish(String event, Run build) {
        JSONObject data = new JSONObject();
        data.put("version", 1);
        data.put("event", event);

        // Project
        JSONObject projectData = new JSONObject();
        projectData.put("url", build.getParent().getAbsoluteUrl());
        projectData.put("name", build.getParent().getName());
        projectData.put("full_name", build.getParent().getFullName());
        projectData.put("display_name", build.getParent().getDisplayName());
        projectData.put("description", build.getParent().getDescription());

        // Build/Run
        JSONObject buildData = new JSONObject();
        buildData.put("number", build.getNumber());
        buildData.put("display_name", build.getDisplayName());
        buildData.put("url", build.getUrl());
        buildData.put("duration", build.getDuration());
        buildData.put("duration_string", build.getDurationString());
        buildData.put("getDescription", build.getDescription());
        buildData.put("start_time_ms", build.getStartTimeInMillis());
        buildData.put("time_ms", build.getTimeInMillis());
        buildData.put("has_artifacts", build.getHasArtifacts());

        CauseAction causeAction = build.getAction(CauseAction.class);
        if (causeAction != null) {
            buildData.put("cause", causeAction.getShortDescription());
        }

        try {
            buildData.put("log", build.getLog(20));
        } catch (IOException e) {
            buildData.put("log", null);
        }

        // Append
        data.put("project", projectData);
        data.put("build", buildData);

        return publish(data);
    }


    @Override
    public boolean publish(JSONObject data) {
        boolean result = true;

        // prepare post methods for both requests types
        if (!StringUtils.isEmpty(this.url)) {
            HttpPost post;
            post = new HttpPost(this.url);

            //logger.log(Level.INFO, "Send to Juxtapose using " + this.url + ": " + data.toString(2));
            logger.log(Level.INFO, "Send to Juxtapose using " + this.url);

            CloseableHttpClient client = getHttpClient();

            try {
                StringEntity params = new StringEntity(data.toString(2));
                post.addHeader("content-type", "application/json");
                post.setEntity(params);

                CloseableHttpResponse response = client.execute(post);

                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != HttpStatus.SC_OK) {
                    HttpEntity entity         = response.getEntity();
                    String     responseString = EntityUtils.toString(entity);
                    logger.log(Level.WARNING, "Juxtapose post may have failed. Response: " + responseString);
                    logger.log(Level.WARNING, "Response Code: " + responseCode);
                    result = false;
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error posting to Juxtapose", e);
                result = false;
            } finally {
                post.releaseConnection();
            }
        } else {
            logger.info("Juxtapose skipped due to empty endpoint url");
        }

        return result;
    }

    protected CloseableHttpClient getHttpClient() {
        final HttpClientBuilder   clientBuilder       = HttpClients.custom();
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

        clientBuilder.setDefaultCredentialsProvider(credentialsProvider);

        if (Jenkins.getInstance() != null) {
            ProxyConfiguration proxy = Jenkins.getInstance().proxy;

            if (proxy != null) {
                final HttpHost         proxyHost    = new HttpHost(proxy.name, proxy.port);
                final HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);
                clientBuilder.setRoutePlanner(routePlanner);

                String username = proxy.getUserName();
                String password = proxy.getPassword();
                // Consider it to be passed if username specified. Sufficient?
                if (username != null && !"".equals(username.trim())) {
                    logger.info("Using proxy authentication (user=" + username + ")");
                    credentialsProvider.setCredentials(new AuthScope(proxyHost),
                                                       new UsernamePasswordCredentials(username, password));
                }
            }
        }
        return clientBuilder.build();
    }

    void setUrl(String webhookUrl) {
        this.url = webhookUrl;
    }
}
