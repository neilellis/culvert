package com.cazcade.culvert.impl;

import com.cazcade.culvert.Shortener;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Default implement of shortener that uses a database table to manage the shortening. Note use of Statement based SQL
 * to defend against injection attacks.
 */
public class DefaultShortener implements Shortener {

    private final JdbcTemplate jdbcTemplate;
    private final String shortenedRoot;

    public DefaultShortener(JdbcTemplate jdbcTemplate, String shortenedRoot) {
        this.jdbcTemplate = jdbcTemplate;
        this.shortenedRoot = shortenedRoot;
    }

    public URI getShortenedURI(String fullURI) {
        try {
            if(fullURI.startsWith("pool///")){
                return getShortenedURIInternal("pool///", fullURI.substring("pool///".length()));
            } else {
                URI uri = new URI(fullURI);
                String uriRoot = getUriRoot(uri);

                String uriDetail = uri.toString().substring(uriRoot.length());

                return getShortenedURIInternal(uriRoot, uriDetail);
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private URI getShortenedURIInternal(String uriRoot, String uriDetail) throws URISyntaxException {
        List<Map<String,Object>> results = jdbcTemplate.queryForList(
                "SELECT uri_code FROM core WHERE uri_root = ? AND uri_detail = ?", new Object[]{uriRoot, uriDetail});
        if(results.size() == 0){
            //does not exist so go create one.
            int rowCount = jdbcTemplate.update("INSERT INTO core (uri_root, uri_detail) VALUES (? , ?)", new Object[]{uriRoot, uriDetail});
            if(rowCount != 1 ){
                throw new RuntimeException("Unexpected failure in creating row.");
            }
            return getShortenedURIInternal(uriRoot, uriDetail);
        } else {
            //does exist so use the first one found.
            return new URI(shortenedRoot + toBase36(Long.valueOf(results.get(0).get("uri_code").toString())));
        }
    }

    public static String getUriRoot(URI fullURI) throws URISyntaxException {
        //break up the full URI to get the necessary components fot the lookup.
        String scheme = fullURI.getScheme();
        String host = fullURI.getHost();
        int port = fullURI.getPort();
        URI root = null;
        root = new URI(scheme, null, host, port, null, null, null);
        String rootString = root.toString();
        return rootString;
    }

    public String getFullURI(URI shortURI) {
        if(!shortURI.toString().toLowerCase().startsWith(shortenedRoot.toLowerCase())){
            throw new RuntimeException("Not from our domain: " + shortURI);
        }
        String code = shortURI.toString().substring(shortenedRoot.length());
        List<Map<String,Object>> results = jdbcTemplate.queryForList(
                "SELECT uri_root, uri_detail FROM core WHERE uri_code = ?", new Object[]{fromBase36(code)});
        if(results.size() == 1){
            return results.get(0).get("uri_root").toString() + results.get(0).get("uri_detail").toString();
        } else {
            throw new RuntimeException("Could not identify short URI: " + shortURI);
        }
    }

    public static String toBase36(long number) {
        return Long.toString(number, 36);
    }

    public static long fromBase36(String number) {
        return Long.valueOf(number, 36);
    }

}
