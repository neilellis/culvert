package com.cazcade.culvert.impl;

import com.cazcade.culvert.Shortener;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * Redirect the shortened URI to the correct URI.
 */
public class RedirectServlet extends HttpServlet {

    private final Shortener shortener;

    public RedirectServlet(Shortener shortener) {
        this.shortener = shortener;
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            URI requestURI = new URI(req.getRequestURI());
            resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            resp.setHeader("Location", shortener.getFullURI(requestURI).toString());
        } catch (Exception e) {
            //TODO add proper logging.
            e.printStackTrace();
            throw new UnavailableException("The requested URI is not available.");
        }
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
