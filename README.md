# rdf4j-utils2

Utililty code related to [rdf4j](https://rdf4j.org/).

## ModelBuilderFacade

The `ModelBuilderFacade` is a (partial) facade over the [rdf4j ModelBuilder](https://rdf4j.org/javadoc/latest/org/eclipse/rdf4j/model/util/ModelBuilder.html).
It includes some convenience methods for adding collections and for skipping over nullable values.

Usage example:

```
Namespace nsData = new SimpleNamespace("", "http://data.example.org/");

ModelBuilderFacade mbf = new ModelBuilderFacade(nsData);

// shaka is likely, but not guaranteed
String shaka = Math.random() >= 0.1 ? "hang loose" : null;

mbf.subject(":Hawaii")
   .addEach("rdfs:label", Arrays.asList("Hawaiʻi", "Big Island"))
   .addNullable("rdfs:comment", shaka);
```

For more examples, have a look at the [unit tests](https://github.com/mchlrch/rdf4j-utils2/tree/main/ch.miranet.rdf4jutils2/src/test/java/ch/miranet/rdf4jutils2/model/util).
