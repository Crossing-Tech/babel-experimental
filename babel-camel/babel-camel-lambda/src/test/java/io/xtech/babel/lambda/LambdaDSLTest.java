/*
 *
 *   Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *   All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.lambda;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;

import org.junit.Test;

public class LambdaDSLTest {

    @Test
    public void simpleRoute() throws Exception {
        //#doc:babel-lambda-simple-example

        RouteBuilder routeBuilder = new RouteBuilder() {
            @Override
            public void configure() {
                //sending messages from input to output
                from(source("direct:input")).to(sink("mock:output1"));
            }
        };
        //#doc:babel-lambda-simple-example
        DefaultCamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(routeBuilder.getRouteBuilder());
        camelContext.start();

        MockEndpoint mockEndpoint1 = (MockEndpoint) camelContext.getEndpoint("mock:output1");
        mockEndpoint1.expectedBodiesReceived("123");

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        producerTemplate.sendBody("direct:input", "123");

        mockEndpoint1.assertIsSatisfied();

        camelContext.shutdown();
    }


    @Test
    public void routeWithProcessor() throws Exception {

        RouteBuilder routeBuilder = new RouteBuilder() {
            @Override
            public void configure() {
                from(source("direct:input")).as(String.class).processBody((str) -> str.length()).to(sink("mock:output1"));
            }
        };

        DefaultCamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(routeBuilder.getRouteBuilder());
        camelContext.start();

        MockEndpoint mockEndpoint1 = (MockEndpoint) camelContext.getEndpoint("mock:output1");

        mockEndpoint1.expectedBodiesReceived(3);
        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();

        producerTemplate.sendBody("direct:input", "123");
        mockEndpoint1.assertIsSatisfied();

        camelContext.shutdown();
    }

    @Test
    public void routeWithContentRouter() throws Exception {

        //#doc:babel-lambda-example
        RouteBuilder routeBuilder = new RouteBuilder() {
            @Override
            public void configure() {
                //take messages from direct input, transforming their body into String
                from(source("direct:input")).as(String.class).
                        //computes the lenght of the string
                        processBody((str) -> str.length()).
                        //routing the exchanges
                        choice((w) -> {
                                //if the length is 3 to output1
                                w.when((O) -> O == 3).to(sink("mock:output1"));
                                //otherwise to output2
                                w.otherwise().to(sink("mock:output2"));
                });
            }
        };
        //#doc:babel-lambda-example



        DefaultCamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(routeBuilder.getRouteBuilder());
        camelContext.start();

        MockEndpoint mockEndpoint1 = (MockEndpoint) camelContext.getEndpoint("mock:output1");
        mockEndpoint1.expectedBodiesReceived(3);

        ProducerTemplate producerTemplate = camelContext.createProducerTemplate();
        producerTemplate.sendBody("direct:input", "123");

        mockEndpoint1.assertIsSatisfied();
        camelContext.shutdown();
    }
}
