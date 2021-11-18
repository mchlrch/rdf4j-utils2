package ch.miranet.rdf4jutils2.shacl;

import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.rdf4j.exceptions.ValidationException;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.RDF4J;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.shacl.ShaclSail;

public class InmemoryShaclValidator {

	/**
	 * @return model with the validation report if SHACL validation failed
	 */
	public static Optional<Model> validate(Iterable<Statement> dataGraphs, Iterable<Statement> shaclRules) {
		return validate(dataGraphs, shaclRules, null);
	}
	
	/**
	 * @return model with the validation report if SHACL validation failed
	 */
	public static Optional<Model> validate(Iterable<Statement> dataGraphs, Iterable<Statement> shaclRules, Consumer<ShaclSail> shaclSailConsumer) {
		ShaclSail shaclSail = new ShaclSail(new MemoryStore());
		
		if (shaclSailConsumer != null) {
			shaclSailConsumer.accept(shaclSail);
		}
		
		Repository repo = new SailRepository(shaclSail);
		repo.init();

		try (RepositoryConnection connection = repo.getConnection()) {

			// add shapes
			connection.begin();
			connection.add(shaclRules, RDF4J.SHACL_SHAPE_GRAPH);

			// add data
			connection.add(dataGraphs);

			try {
				connection.commit();
				return Optional.empty();
			} catch (RepositoryException exception) {
				Throwable cause = exception.getCause();
				if (cause instanceof ValidationException) {
					Model validationReportModel = ((ValidationException) cause).validationReportAsModel();
					return Optional.of(validationReportModel);
				} else {
					throw exception;
				}
			}
		}
	}

}
