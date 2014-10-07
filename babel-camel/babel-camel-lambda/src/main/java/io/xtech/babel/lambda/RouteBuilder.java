/*
 *
 *   Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *   All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.lambda;

import io.xtech.babel.camel.model.CamelSink;
import io.xtech.babel.camel.model.CamelSource;
import io.xtech.babel.fish.parsing.StepInformation;
import io.xtech.babel.fish.model.FromDefinition;
import io.xtech.babel.fish.model.RouteDefinition;
import io.xtech.babel.fish.model.Sink;
import io.xtech.babel.fish.model.Source;

import java.util.Optional;

/**
 * A Base to define Babel Camel routes.
 */
public abstract class RouteBuilder extends io.xtech.babel.camel.builder.RouteBuilder {

    private RouteDefinition routeDefinition = null;

    public Source<Object> source(String uri) {
        return new CamelSource(uri);
    }

    public Sink<Object, Object> sink(String uri) {
        return new CamelSink<>(uri);
    }

    public <I> DSL<I> from(Source<I> source) {

        FromDefinition fromDefinition = new FromDefinition(null, source);
        routeDefinition = new RouteDefinition(fromDefinition);
        return new DSL<I>(fromDefinition);
    }

    /**
     * Use this method to configure the routes.
     */
    public abstract void configure();

    /**
     * @return a Camel RouteBuilder
     */
    public org.apache.camel.builder.RouteBuilder getRouteBuilder() {
        configure();
        return new org.apache.camel.builder.RouteBuilder() {
            @Override
            public void configure() throws Exception {

                processSteps(new StepInformation<>(routeDefinition.from(), Optional.empty(), this));
            }
        };
    }
}
