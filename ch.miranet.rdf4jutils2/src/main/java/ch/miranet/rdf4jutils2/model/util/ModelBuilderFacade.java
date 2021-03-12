package ch.miranet.rdf4jutils2.model.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.ModelBuilder;

/**
 * (Partial) facade over RDF4J {@link ModelBuilder}.
 * <p>
 * Includes convenience methods for adding collections and for skipping over nullable values.
 * 
 * Usage example:
 *
 * <pre>
 * <code>
 *   Namespace nsData = new SimpleNamespace("", "http://data.example.org/");
 *
 *   ModelBuilderFacade mbf = new ModelBuilderFacade(nsData);
 *
 *   // shaka is likely, but not guaranteed
 *   String shaka = Math.random() >= 0.1 ? "hang loose" : null;
 *
 *   mbf.subject(":Hawaii")
 *      .addEach("rdfs:label", Arrays.asList("Hawaiʻi", "Big Island"))
 *      .addNullable("rdfs:comment", shaka);
 * </code>
 * </pre>
 */
public class ModelBuilderFacade {
	
	protected final Model model;
	protected final ModelBuilder modelBuilder;
	protected final ValueFactory valueFactory;
	
	public ModelBuilderFacade() {
		this(null, new Namespace[0]);
	}
	
	public ModelBuilderFacade(Namespace... namespaces) {
		this(null, namespaces);
	}

	protected ModelBuilderFacade(Model model, Namespace... namespaces) {
		this.model = (model == null) ? new LinkedHashModel() : model;
		this.modelBuilder = new ModelBuilder(this.model);
		this.valueFactory = SimpleValueFactory.getInstance();

		for (Namespace ns : namespaces) {
			this.model.setNamespace(ns);
		}
	}

	public ModelBuilder getModelBuilder() {
		return modelBuilder;
	}

	public ValueFactory getValueFactory() {
		return valueFactory;
	}

    ModelBuilderFacade subject(String prefixedNameOrIri) {
        modelBuilder.subject(prefixedNameOrIri);
        return this;
    }

    ModelBuilderFacade subject(IRI iri) {
        modelBuilder.subject(iri);
        return this;
    }

    ModelBuilderFacade add(String predicate, Object object) {
        modelBuilder.add(predicate, object);
        return this;
    }

    ModelBuilderFacade add(IRI predicate, Object object) {
        modelBuilder.add(predicate, object);
        return this;
    }

    /** Ignores null values */
    ModelBuilderFacade addNullable(String predicate, Object object) {
        if (object != null) {
            add(predicate, object);
        }
        return this;
    }

    ModelBuilderFacade addEach(Map<IRI, Object> propertyValues) {
        propertyValues.forEach(this::addPropertyValue);
        return this;
    }

    private void addPropertyValue(IRI predicate, Object propValue) {
        if (propValue != null && propValue.getClass().isArray()) {
            convertAnyArrayToList(propValue).forEach(val -> add(predicate, val));

        } else if (propValue instanceof Iterable) {
            ((Iterable<?>) propValue).forEach(val -> add(predicate, val));

        } else if (propValue != null) {
            add(predicate, propValue);
        }
    }

    ModelBuilderFacade addEach(String predicate, Collection<?> objectValues) {
        objectValues.forEach(o -> modelBuilder.add(predicate, o));
        return this;
    }

    public Model build() {
        return modelBuilder.build();
    }

    // classic array copy implemented in a for-loop, in order to support copy of Object[] as well as Arrays of primitive Types (eg. int[])
    static List<Object> convertAnyArrayToList(Object arrayObject) {
        final List<Object> list = new ArrayList<>();
        for (int i = 0; i < Array.getLength(arrayObject); i++) {
            list.add(Array.get(arrayObject, i));
        }
        return list;
    }
}
