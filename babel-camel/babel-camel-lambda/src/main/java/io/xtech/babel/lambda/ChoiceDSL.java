package io.xtech.babel.lambda;

import io.xtech.babel.fish.BodyPredicate;
import io.xtech.babel.fish.ScalaHelper;
import io.xtech.babel.fish.model.ChoiceDefinition;
import io.xtech.babel.fish.model.OtherwiseDefinition;
import io.xtech.babel.fish.model.WhenDefinition;
import scala.Some;

import java.util.function.Predicate;

public class ChoiceDSL<I> extends DSL<I> {

    private ChoiceDefinition<I> choiceDefinition = null;

    public ChoiceDSL(ChoiceDefinition<I> definition) {
        super(definition);
        this.choiceDefinition = definition;
    }

    /**
     * Defines a conditional branch of the choice.
     * @param predicate if evaluated to true, the message would be provided to the next step of the dsl
     * @return the possibility to add other steps to the current DSL
     */
    public DSL<I> when(Predicate<I> predicate) {

        WhenDefinition<I> whenDef = new WhenDefinition<>(new BodyPredicate<I>(ScalaHelper.scalaPredicate(predicate::test)));
        choiceDefinition.addWhen(whenDef);
        return new DSL<I>(whenDef);
    }

    /**
     * Defines the fallback branch of the choice.
     * @return the possibility to add other steps to the current DSL
     */
    public DSL<I> otherwise() {

        OtherwiseDefinition otherwiseDefinition = new OtherwiseDefinition();
        choiceDefinition.otherwise_$eq(new Some<>(otherwiseDefinition));
        return new DSL<I>(otherwiseDefinition);
    }
}
