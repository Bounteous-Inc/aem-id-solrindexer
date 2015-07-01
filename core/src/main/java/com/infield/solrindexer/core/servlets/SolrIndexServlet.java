package com.infield.solrindexer.core.servlets;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@SlingServlet(paths = { "/bin/services/infield/solrindex" })
public class SolrIndexServlet extends SlingAllMethodsServlet {

    private final Logger LOG = LoggerFactory.getLogger(SolrIndexServlet.class);

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        LOG.info("---------> Solr Index Servlet: POST");

        String solrUrl = request.getParameter("solrurl");
        String content = request.getParameter("content");
        JSONObject reqData = new JSONObject();

        OutputStreamWriter osw = null;
        BufferedReader br = null;

        try {

            // simple is empty checks:
            if (solrUrl == null || "".equals(solrUrl)) {
                reqData.put("success", false);
                reqData.put("message", "Error: No Solr URL specified.");
            }
            if (content == null || "".equals(content)) {
                reqData.put("success", false);
                reqData.put("message", "Error: No content posted.");
            }

            // set up connection:
            URL test = new URL(solrUrl + "?commit=true");
            LOG.info("POST Calling URL..." + solrUrl);
            HttpURLConnection httpCon = (HttpURLConnection) test.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setDoInput(true);
            httpCon.setUseCaches(false);
            httpCon.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            httpCon.setRequestProperty("Accept", "application/json");
            httpCon.setRequestMethod("POST");
            httpCon.connect();

            // send message:
            OutputStream os = httpCon.getOutputStream();
            osw = new OutputStreamWriter(os, "UTF-8");
            osw.write(content);
            osw.flush();

            // retrieve message
            br = new BufferedReader(new InputStreamReader( httpCon.getInputStream(), StandardCharsets.UTF_8));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            reqData.put("success", true);
            reqData.put("message", "Solr index successfully updated.");

        } catch (Exception e) {

            LOG.error(e.getMessage());
            try {
                reqData.put("success", false);
                reqData.put("message", "Error: " + e.getMessage());
            } catch (Exception e1) {
                LOG.error(e1.getMessage());
            }

        } finally {
            if (osw != null) {
                osw.close();
            }
            if (br != null) {
                br.close();
            }
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(reqData.toString());
    }

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        LOG.info("---------> Solr Index Servlet: GET");

        String indexPath = request.getParameter("indexPath");
        String path = request.getParameter("path");
        String aemUser = request.getParameter("aemUser");
        String aemPassword = request.getParameter("aemPassword");

        String solrUrl = path + "?path=" + indexPath;
        JSONObject reqData = new JSONObject();

        LOG.debug("Path passed: " + indexPath);

        try {
            // simple is empty checks:
            if (path == null || "".equals(path)) {
                reqData.put("success", false);
                reqData.put("message", "Error: No Update handler URL specified.");
            }
            if (indexPath == null || "".equals(indexPath)) {
                reqData.put("success", false);
                reqData.put("message", "Error: No indexPath specified.");
            }
            if (aemUser == null || "".equals(aemUser) || aemPassword == null || "".equals(aemPassword)) {
                reqData.put("success", false);
                reqData.put("message", "Error: No AEM user and/or password specified.");
            }
        } catch (Exception e) {
            LOG.info(e.getMessage());
        }

        // Create an instance of HttpClient.
        HttpClient client = new HttpClient();

        // Create a method instance.
        GetMethod method = new GetMethod(solrUrl);
        LOG.debug("GET Calling URL..." + solrUrl);
        method.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

        // handle authentication:
        Credentials defaultcreds = new UsernamePasswordCredentials(aemUser, aemPassword);
        client.getState().setCredentials(AuthScope.ANY, defaultcreds);

        // Provide custom retry handler is necessary
        method.getParams()
            .setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

        BufferedReader bufferedReader = null;

        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);

            bufferedReader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            if (statusCode != HttpStatus.SC_OK) {

                reqData.put("success", false);
                reqData.put("message", "Error: " + method.getStatusLine() + " - " + method.getResponseBodyAsString());
                LOG.info("GET request failed: " + method.getStatusLine() + " - " + method
                    .getResponseBodyAsString());

            } else {
                reqData.put("success", true);
                reqData.put("message", "Collected data for new index with pages under '" + indexPath + "'.");
                reqData.put("content", sb.toString());
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());

            try {
                reqData.put("success", false);
                reqData.put("message", "Error: " + e.getMessage());

            } catch (Exception e1) {
                LOG.error(e.getMessage());
            }

        } finally {

            // Release the connection.
            method.releaseConnection();

            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(reqData.toString());
    }

    @Override
    protected void doDelete(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {

        LOG.info("---------> Solr Index Servlet: DELETE");

        String solrUrl = request.getParameter("solrurl");
        String indexPath = request.getParameter("indexPath");
        JSONObject reqData = new JSONObject();
        OutputStreamWriter osw = null;
        BufferedReader br = null;

        LOG.debug("Path passed: " + indexPath);

        try {

            // basic validation:
            if (solrUrl == null || "".equals(solrUrl)) {
                reqData.put("success", false);
                reqData.put("message", "Error: No Solr URL specified.");
                return;
            }
            if (indexPath == null || "".equals(indexPath)) {
                reqData.put("success", false);
                reqData.put("message", "Error: No indexPath specified.");
                return;
            }
            if (!indexPath.startsWith("/content") || indexPath.endsWith("/")) {
                reqData.put("success", false);
                reqData.put("message", "Error: Invalid path provided.");
                return;
            }

            // Only delete with matching indexPath
            String requestBody = "<delete><query>id:" + indexPath.replace("/", "\\/") + "*</query></delete>";

            // set up connection:
            URL test = new URL(solrUrl + "?commit=true" + "&stream.body=" + URLEncoder.encode(requestBody, "UTF-8"));
            LOG.debug("DELETE Calling URL..." + solrUrl);
            HttpURLConnection httpCon = (HttpURLConnection) test.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setDoInput(true);
            httpCon.setUseCaches(false);
            httpCon.setRequestMethod("GET");
            httpCon.connect();

            // retrieve message
            br = new BufferedReader(new InputStreamReader( httpCon.getInputStream(), StandardCharsets.UTF_8));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }

            reqData.put("success", true);
            reqData.put("message", "Pages under '" + indexPath + "' successfully removed from index.");

        } catch (Exception e) {
            LOG.info(e.getMessage());
        } finally {
            if (osw != null) {
                osw.close();
            }
            if (br != null) {
                br.close();
            }
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(reqData.toString());
    }

}
