package ch.miranet.rdf4jutils2.shacl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.EmptyModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.junit.jupiter.api.Test;

public class ValidationReportPrinterTest {

	@Test
	void printOneLineForEachResult_node_with_one_violation() {
		final Model validationReport = loadReportWithOneViolation();

		final String reportSummary = ValidationReportPrinter.printOneLineForEachResult(validationReport);

		assertEquals("Node http://data.example.org/Baz/attribute/location on path https://schema.miranet.ch/model2viz/uml/name violated MinCountConstraintComponent", reportSummary);
	}

	@Test
	void printOneLineForEachResult_emptyReport() {
		final Model validationReport = new EmptyModel(new LinkedHashModel());

		final String reportSummary = ValidationReportPrinter.printOneLineForEachResult(validationReport);

		assertTrue(reportSummary.isEmpty());
	}

	// TODO: consider re-using functionality in ch.miranet.protograph.util.ModelLoader.getResourceAsModel(String)
	private static Model loadReportWithOneViolation() {
		try (final InputStream inputStream = ValidationReportPrinterTest.class.getClassLoader().getResourceAsStream("shacl-validation-reports/report-with-one-violation.ttl")) {
			if (inputStream == null)
				throw new RuntimeException("Can't find resource");

			return Rio.parse(inputStream, RDFFormat.TURTLE);

		} catch (final IOException | RDFParseException | UnsupportedRDFormatException e) {
			throw new RuntimeException("Can't load resource", e);
		}
	}
}
