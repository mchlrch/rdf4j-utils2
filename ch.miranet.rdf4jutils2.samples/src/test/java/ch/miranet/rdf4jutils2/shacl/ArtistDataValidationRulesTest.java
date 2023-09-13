package ch.miranet.rdf4jutils2.shacl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.FOAF;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicWriterSettings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.miranet.rdf4jutils2.model.util.NamespaceCollection;

public class ArtistDataValidationRulesTest {

	private static final Collection<Namespace> NAMESPACES = NamespaceCollection.create()
			.add("", "http://data.example.org/")
			.add("ex", "http://example.org/")
			.add(FOAF.NS)
			.buildSnapshot();

	private ModelBuilder dataBuilder;

	@BeforeEach
	public void setUp() {
		dataBuilder = new ModelBuilder();
		NAMESPACES.forEach(dataBuilder::setNamespace);
	}

	@Test
	public void artist_ok() {
		dataBuilder.subject(":Picasso")
				.add(RDF.TYPE, "ex:Artist")
				.add("foaf:firstName", "Pablo")
				.add("foaf:surname", "Picasso")
				.add("ex:creatorOf", ":guernica");

		dataBuilder.subject(":guernica")
				.add(RDF.TYPE, "ex:Painting");

		final Model data = dataBuilder.build();

		dumpDataOnSysOutAsTrig(NAMESPACES, data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = ArtistDataValidationRules.buildShaclRules();
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules);

		assertFalse(validationReport.isPresent());
	}

	@Test
	public void artist_error_missing_creatorOf() {
		dataBuilder.subject(":Picasso")
				.add(RDF.TYPE, "ex:Artist")
				.add("foaf:firstName", "Pablo")
				.add("foaf:surname", "Picasso");

		final Model data = dataBuilder.build();

		dumpDataOnSysOutAsTrig(NAMESPACES, data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = ArtistDataValidationRules.buildShaclRules();
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules);

		assertTrue(validationReport.isPresent());

		dumpDataOnSysOutAsTrig(NAMESPACES, validationReport.get());
	}

	@Test
	public void artist_error_creatorOf_notAPainting() {
		dataBuilder.subject(":Picasso")
				.add(RDF.TYPE, "ex:Artist")
				.add("foaf:firstName", "Pablo")
				.add("foaf:surname", "Picasso")
				.add("ex:creatorOf", ":guernica");

		dataBuilder.subject(":guernica")
				.add(RDF.TYPE, "ex:Foo");

		final Model data = dataBuilder.build();

		dumpDataOnSysOutAsTrig(NAMESPACES, data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = ArtistDataValidationRules.buildShaclRules();
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules);

		assertTrue(validationReport.isPresent());

		dumpDataOnSysOutAsTrig(NAMESPACES, validationReport.get());
	}

	private static void dumpDataOnSysOutAsTrig(final Collection<Namespace> namespaces, final Iterable<Statement> data) {
		final RDFWriter writer = Rio.createWriter(RDFFormat.TRIG, System.out);
		writer.getWriterConfig().set(BasicWriterSettings.INLINE_BLANK_NODES, Boolean.TRUE);

		writer.startRDF();
		namespaces.forEach(ns -> writer.handleNamespace(ns.getPrefix(), ns.getName()));
		data.forEach(writer::handleStatement);
		writer.endRDF();
	}
}
