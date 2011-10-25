package com.cazcade.culvert;

import java.net.URI;

/**
 * Handle shortened strings to create a bit.ly / tinyURL style service.
 */
public interface Shortener {
    
    public URI getShortenedURI(String fullURI);

    public String getFullURI(URI shortURI);
}
