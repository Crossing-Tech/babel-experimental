/*
 *
 *   Copyright 2010-2014 Crossing-Tech SA, EPFL QI-J, CH-1015 Lausanne, Switzerland.
 *   All rights reserved.
 *
 * ==================================================================================
 */

package io.xtech.babel.lambda;

import io.xtech.babel.fish.BodyExpression;
import io.xtech.babel.fish.ScalaHelper;
import io.xtech.babel.fish.model.*;
import scala.None$;
import scala.Some;
import scala.Option$;

import java.util.function.Consumer;
import java.util.function.Function;

public class DSL<I> {

    // fields

    protected StepDefinition definition;

    // constructors

    public DSL(StepDefinition defintion) {
        this.definition = defintion;
    }

    //keywords

    /**
     * Defines a transformation based on the body of the exchange.
     * @param function applied on each received exchange body
     * @param <O> is the type of the ouput of this transformation
     * @return the possibility to add other steps to the current DSL
     */
    public <O> DSL<O> processBody(Function<I, O> function) {

        TransformerDefinition<I, O> transformerDefinition = new TransformerDefinition<>(new BodyExpression<>(ScalaHelper.scalaFunction(function::apply)), Option$.MODULE$.empty());
        definition.next_$eq(new Some<>(transformerDefinition));
        return new DSL<O>(transformerDefinition);
    }

    /**
     * Defines the type of the rest of the dsl. If the input type does not match the required one, Camel applies a
     *  transformation toward this type.
     * @param clazz which defines the output of this keyword
     * @param <O> the type of the output
     * @return the possibility to add other steps to the current DSL
     */
    public <O> DSL<O> as(Class<O> clazz) {
        BodyConvertorDefinition<I, O> bodyConvertorDefinition = new BodyConvertorDefinition<>(null, clazz);

        definition.next_$eq(new Some<>(bodyConvertorDefinition));

        return new DSL<O>(bodyConvertorDefinition);
    }

    /**
     * Defines a sink of the dsl. The to keyword sends messages to the endpoint provided as argument.
     * @param sink the endpoint which will receive exchanges
     * @param <O> type of the output of the sink
     * @return the possibility to add other steps to the current DSL
     */
    public <O> DSL<O> to(Sink<? super I, ? extends O> sink) {
        EndpointDefinition<? super I, ? extends O> endpointDefinition = new EndpointDefinition<>(sink, None$.empty());

        definition.next_$eq(new Some<>(endpointDefinition));

        return new DSL<O>(endpointDefinition);
    }

    /**
     * Defines a content base router whcih sends received exchange to sub routes depending on its value.
     * @param function which defines where the received exchanges are routed
     * @param <O> the output of the router
     * @return the possibility to add other steps to the current DSL
     */
    public <O> DSL<O> choice(Consumer<ChoiceDSL<I>> function) {

        ChoiceDefinition<I> choiceDefinition = new ChoiceDefinition<>();

        definition.next_$eq(new Some<>(choiceDefinition));

        ChoiceDSL<I> choiceDSL = new ChoiceDSL<>(choiceDefinition);

        function.accept(choiceDSL);

        return new DSL<O>(choiceDefinition);
    }
}
