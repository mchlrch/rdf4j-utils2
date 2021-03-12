package ch.miranet.rdf4jutils2.model.util;

import java.util.Arrays;

import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

public class ModelBuilderFacadeUsageSample {

	public static void main(String[] args) {
		Namespace nsData = new SimpleNamespace("", "http://data.example.org/");

		ModelBuilderFacade mbf = new ModelBuilderFacade(nsData);

		// shaka is likely, but not guaranteed
		String shaka = Math.random() >= 0.1 ? "hang loose" : null;

		mbf.subject(":Hawaii")
				.addEach("rdfs:label", Arrays.asList("Hawaiʻi", "Big Island"))
				.addNullable("rdfs:comment", shaka);

		Rio.write(mbf.getModelBuilder().build(), System.out, RDFFormat.TURTLE);
	}

}
