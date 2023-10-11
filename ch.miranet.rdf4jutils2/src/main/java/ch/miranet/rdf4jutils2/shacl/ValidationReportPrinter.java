package ch.miranet.rdf4jutils2.shacl;

import static ch.miranet.rdf4jutils2.query.QueryUtils.loadQuery;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.eclipse.rdf4j.query.QueryLanguage.SPARQL;

import java.util.Iterator;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

public class ValidationReportPrinter {

	private static final String QUERY = loadQuery("queries/extract-results-from-shacl-validation-report.rq").get();

	public static String printOneLineForEachResult(final Iterable<Statement> shaclValidationReport) {
		final StringBuilder sb = new StringBuilder();

		final Repository memoryRepo = new SailRepository(new MemoryStore());
		try (final RepositoryConnection connection = memoryRepo.getConnection()) {

			connection.add(shaclValidationReport);

			final TupleQueryResult result = connection.prepareTupleQuery(SPARQL, QUERY).evaluate();
			for (final Iterator<BindingSet> it = result.iterator(); it.hasNext();) {
				sb.append(new ValidationResultStruct(it.next()));
				if (it.hasNext()) {
					sb.append(System.lineSeparator());
				}
			}
		}
		memoryRepo.shutDown();

		return sb.toString();
	}

	private static class ValidationResultStruct {
		private final String focusNode;
		private final String resultPath;
		private final String sourceConstraintComponentShortName;

		ValidationResultStruct(final BindingSet bindingSet) {
			this.focusNode = bindingSet.getValue("focusNode").toString();
			this.resultPath = bindingSet.getValue("resultPath").toString();
			this.sourceConstraintComponentShortName = substringAfterLast(
					bindingSet.getValue("sourceConstraintComponent").toString(), "#");
		}

		@Override
		public String toString() {
			return String.format("Node %s on path %s violated %s", this.focusNode, this.resultPath,
					this.sourceConstraintComponentShortName);
		}
	}
}
