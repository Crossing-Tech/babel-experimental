Babel experimental
=================

This project contains future improvement of the [Babel]( https://github.com/Crossing-Tech/babel "Babel Sources") project:

* Babel Camel Lambda : a java 8 DSL for Apache Camel
  
Those projects are still into prototyping or into test phase. 

If you have any feedback or comment, please post it on the Babel google group: https://groups.google.com/forum/#!forum/babel-user 

Babel Camel Lambda
------------------

Babel Camel Lambda is a DSL for Apache Camel provided for Java users

In order to use the Babel DSL for Camel with Java, add the following to your maven dependencies:
```xml
<dependency>
    <groupId>io.xtech.babel</groupId>
    <artifactId>babel-camel-lambda</artifactId>
    <version>BABEL_VERSION</version>
</dependency>
```

Then, you may create your route using the DSL
 
```java

import io.xtech.babel.camel.lambda.RouteBuilder;

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

``` 
