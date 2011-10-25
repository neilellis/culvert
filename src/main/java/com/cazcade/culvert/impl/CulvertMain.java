package com.cazcade.culvert.impl;

import com.cazcade.culvert.Shortener;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import java.net.URI;
import java.net.URISyntaxException;

public class CulvertMain {
    private static final int MAX_ITER = 1000;

    public static void main(String[] args) throws URISyntaxException {
        BeanFactory factory = new XmlBeanFactory(new ClassPathResource("/spring/culvert-service.xml"));
        Shortener shortener = (Shortener) factory.getBean("shortener");
        URI shortURI = shortener.getShortenedURI("http://static.springsource.org/spring/docs/3.0.x/spring-framework-reference/html/transaction.html#tx-resource-synchronization-high");
        System.out.println("Shortened: " + shortURI);
        String longURI = shortener.getFullURI(shortURI);
        System.out.println("Lengthened: " + longURI);

        performanceTest(shortener);
    }

    private static void performanceTest(Shortener shortener) throws URISyntaxException {
        long startTime = System.currentTimeMillis();

        for(int i = 0; i< MAX_ITER; i++){
            shortener.getShortenedURI("http://static.a"+i+".springsource.org/spring/docs/3.0.x/spring-framework-reference/html/transaction.html#tx-resource-synchronization-high");
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Initial generation of "+MAX_ITER+" entries took: " + (endTime - startTime) + "ms.");

        startTime = System.currentTimeMillis();

        for(int i = 0; i< MAX_ITER; i++){
            shortener.getShortenedURI("http://static.a"+i+".springsource.org/spring/docs/3.0.x/spring-framework-reference/html/transaction.html#tx-resource-synchronization-high");
        }

        endTime = System.currentTimeMillis();

        System.out.println("Retrieval generation of "+MAX_ITER+" entries took: " + (endTime - startTime) + "ms.");

    }
}
