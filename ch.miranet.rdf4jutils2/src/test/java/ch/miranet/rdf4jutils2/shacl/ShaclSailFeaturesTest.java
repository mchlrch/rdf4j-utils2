package ch.miranet.rdf4jutils2.shacl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ShaclSailFeaturesTest {
	private ModelBuilder dataBuilder;

	static Iterable<Statement> loadShaclRules(String filePath) {
		try (final InputStream inputStream = ShaclSailFeaturesTest.class.getResourceAsStream(filePath)) {
			Objects.requireNonNull(inputStream, "File not found: " + filePath);
			final Model rules = Rio.parse(inputStream, "", RDFFormat.TURTLE, RDF4J.SHACL_SHAPE_GRAPH);
			return rules;
		} catch (final IOException | RDFParseException | UnsupportedRDFormatException e) {
			throw new RuntimeException("Failed to load shacl rules.", e);
		}
	}

	static void dumpTurtle(final Optional<Model> model) {
		model.ifPresent(ShaclSailFeaturesTest::dumpTurtle);
	}

	static void dumpTurtle(final Model model) {
		System.setProperty("org.eclipse.rdf4j.rio.inline_blank_nodes", Boolean.TRUE.toString());
		Rio.write(model, System.out, RDFFormat.TURTLE);
	}

	@BeforeEach
	public void setUp() {
		dataBuilder = new ModelBuilder();
	}

	@Test
	public void myshape1_targetSubjectsOf_ok() {
		dataBuilder.subject("https://data.example.org/thing/abc")
				.add(RDFS.LABEL, "ABC")
				.add(RDFS.COMMENT, "Comment about ABC.");

		final Model data = dataBuilder.build();
		dumpTurtle(data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = loadShaclRules("/shacl-rules/myshape1_targetSubjectsOf.ttl");
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules);

		dumpTurtle(validationReport);

		assertFalse(validationReport.isPresent());
	}

	@Test
	public void myshape1_targetSubjectsOf_fail_missing_comment() {
		dataBuilder.subject("https://data.example.org/thing/abc")
				.add(RDFS.LABEL, "ABC");

		final Model data = dataBuilder.build();
		dumpTurtle(data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = loadShaclRules("/shacl-rules/myshape1_targetSubjectsOf.ttl");
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules);

		dumpTurtle(validationReport);

		assertTrue(validationReport.isPresent());
	}

	@Test
	public void myshape2_rsx_targetShape_hasValue_ok() {
		dataBuilder.subject("https://data.example.org/thing/abc")
				.add(RDFS.LABEL, "ABC")
				.add(RDFS.COMMENT, "Comment about ABC.");

		final Model data = dataBuilder.build();
		dumpTurtle(data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = loadShaclRules("/shacl-rules/myshape2_rsx_targetShape_hasValue.ttl");
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules,
				shaclSail -> {
					shaclSail.setEclipseRdf4jShaclExtensions(true);
				});

		dumpTurtle(validationReport);

		assertFalse(validationReport.isPresent());
	}

	@Test
	public void myshape2_rsx_targetShape_hasValue_fail_missing_comment() {
		dataBuilder.subject("https://data.example.org/thing/abc")
				.add(RDFS.LABEL, "ABC");

		final Model data = dataBuilder.build();
		dumpTurtle(data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = loadShaclRules("/shacl-rules/myshape2_rsx_targetShape_hasValue.ttl");
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules,
				shaclSail -> {
					shaclSail.setEclipseRdf4jShaclExtensions(true);
				});

		dumpTurtle(validationReport);

		assertTrue(validationReport.isPresent());
	}

	// fails with rdf4j version 3.6.1 (GraphDB 9.7.0)
	// fails with rdf4j version 3.7.3 (GraphDB 9.10.0)
	// throws java.lang.UnsupportedOperationException: PatternConstraintComponent
	// at
	// org.eclipse.rdf4j.sail.shacl.ast.constraintcomponents.AbstractConstraintComponent.buildSparqlValidNodes_rsx_targetShape(AbstractConstraintComponent.java:90)
	@Test
	@Disabled
	public void myshape3_rsx_targetShape_namespace_pattern_ok() {
		dataBuilder.subject("https://data.example.org/thing/abc")
				.add(RDFS.LABEL, "ABC")
				.add(RDFS.COMMENT, "Comment about ABC.");

		final Model data = dataBuilder.build();
		dumpTurtle(data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = loadShaclRules(
				"/shacl-rules/myshape3_rsx_targetShape_namespace_pattern.ttl");
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules,
				shaclSail -> {
					shaclSail.setEclipseRdf4jShaclExtensions(true);
				});

		dumpTurtle(validationReport);

		assertFalse(validationReport.isPresent());
	}

// TODO: public void myshape3_rsx_targetShape_namespace_pattern_comment_fail_____()	

	@Test
	public void myshape4_dash_all_subjects_target_ok() {
		dataBuilder.subject("https://data.example.org/thing/abc")
				.add(RDFS.LABEL, "ABC")
				.add(RDFS.COMMENT, "Comment about ABC.");

		final Model data = dataBuilder.build();
		dumpTurtle(data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = loadShaclRules("/shacl-rules/myshape4_dash_all_subjects_target.ttl");
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules,
				shaclSail -> {
					shaclSail.setDashDataShapes(true);
				});

		dumpTurtle(validationReport);

		assertFalse(validationReport.isPresent());
	}

	// fails with rdf4j version 3.6.1 (GraphDB 9.7.0)
	// fails with rdf4j version 3.7.3 (GraphDB 9.10.0)
	@Test
	@Disabled
	public void myshape4_dash_all_subjects_target_fail_missing_comment() {
		dataBuilder.subject("https://data.example.org/thing/abc")
				.add(RDFS.LABEL, "ABC");

		final Model data = dataBuilder.build();
		dumpTurtle(data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = loadShaclRules("/shacl-rules/myshape4_dash_all_subjects_target.ttl");
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules,
				shaclSail -> {
					shaclSail.setDashDataShapes(true);
				});

		dumpTurtle(validationReport);

		assertTrue(validationReport.isPresent());
	}

	@Test
	public void myshape5_UndefinedTargetValidatesAllSubjects_ok() {
		dataBuilder.subject("https://data.example.org/thing/abc")
				.add(RDFS.LABEL, "ABC")
				.add(RDFS.COMMENT, "Comment about ABC.");

		final Model data = dataBuilder.build();
		dumpTurtle(data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = loadShaclRules(
				"/shacl-rules/myshape5_UndefinedTargetValidatesAllSubjects.ttl");
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules,
				shaclSail -> {
					shaclSail.setUndefinedTargetValidatesAllSubjects(true);
				});

		dumpTurtle(validationReport);

		assertFalse(validationReport.isPresent());
	}

	// fails with rdf4j version 3.6.1 (GraphDB 9.7.0)
	// fails with rdf4j version 3.7.3 (GraphDB 9.10.0)
	@Test
	@Disabled
	public void myshape5_UndefinedTargetValidatesAllSubjects_fail_missing_comment() {
		dataBuilder.subject("https://data.example.org/thing/abc")
				.add(RDFS.LABEL, "ABC");

		final Model data = dataBuilder.build();
		dumpTurtle(data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = loadShaclRules(
				"/shacl-rules/myshape5_UndefinedTargetValidatesAllSubjects.ttl");
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules,
				shaclSail -> {
					shaclSail.setUndefinedTargetValidatesAllSubjects(true);
				});

		dumpTurtle(validationReport);

		assertTrue(validationReport.isPresent());
	}

	// fails with rdf4j version 3.6.1 (GraphDB 9.7.0)
	// fails with rdf4j version 3.7.3 (GraphDB 9.10.0)
	// throws org.eclipse.rdf4j.sail.shacl.ast.ShaclUnsupportedException
	@Test
	@Disabled
	public void myshape6_rsx_targetShape_path_hasValue_ok() {
		dataBuilder.subject("ex:Thing")
				.add("ex:foo", "ex:bar");

		dataBuilder.subject("https://data.example.org/thing/abc")
				.add(RDF.TYPE, "ex:Thing")
				.add(RDFS.LABEL, "ABC")
				.add(RDFS.COMMENT, "Comment about ABC.");

		final Model data = dataBuilder.build();
		dumpTurtle(data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = loadShaclRules(
				"/shacl-rules/myshape6_rsx_targetShape_path_hasValue.ttl");
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules,
				shaclSail -> {
					shaclSail.setEclipseRdf4jShaclExtensions(true);
				});

		dumpTurtle(validationReport);

		assertFalse(validationReport.isPresent());
	}

	// fails with rdf4j version 3.6.1 (GraphDB 9.7.0)
	// fails with rdf4j version 3.7.3 (GraphDB 9.10.0)
	// throws org.eclipse.rdf4j.sail.shacl.ast.ShaclUnsupportedException
	@Test
	@Disabled
	public void myshape6_rsx_targetShape_path_hasValue_fail_missing_comment() {
		dataBuilder.subject("ex:Thing")
				.add("ex:foo", "ex:bar");

		dataBuilder.subject("https://data.example.org/thing/abc")
				.add(RDF.TYPE, "ex:Thing")
				.add(RDFS.LABEL, "ABC");

		final Model data = dataBuilder.build();
		dumpTurtle(data);

		assertNotNull(data);

		final Iterable<Statement> shaclRules = loadShaclRules(
				"/shacl-rules/myshape6_rsx_targetShape_path_hasValue.ttl");
		final Optional<Model> validationReport = InmemoryShaclValidator.validate(data, shaclRules,
				shaclSail -> {
					shaclSail.setEclipseRdf4jShaclExtensions(true);
				});

		dumpTurtle(validationReport);

		assertTrue(validationReport.isPresent());
	}
}
