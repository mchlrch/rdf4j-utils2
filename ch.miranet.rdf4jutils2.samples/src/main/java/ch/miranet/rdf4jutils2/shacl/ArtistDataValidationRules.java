package ch.miranet.rdf4jutils2.shacl;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.FOAF;

import ch.miranet.rdfstructure.RdfStructureBuilder;

public class ArtistDataValidationRules {

    public static Iterable<Statement> buildShaclRules() {
        final RdfStructureBuilder builder = new RdfStructureBuilder();

        builder.getModelBuilder()        
                .setNamespace(FOAF.NS)
                .setNamespace("ex", "http://example.org/");

        builder.nodeShape("ex:ArtistShape")
        		.targetClass("ex:Artist")
                .property("ex:creatorOf", propertyShape -> {
                    propertyShape.minCount(1);
                    propertyShape.clazz("ex:Painting");                    
                });

        final Model result = builder.getModelBuilder().build();

        return result;
    }

}
