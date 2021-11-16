package ch.miranet.rdf4jutils2.query;

import static ch.miranet.rdf4jutils2.query.QueryUtils.loadQuery;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class QueryUtilsTest {

	@Test
	void testLoadQuery_ok() {
		final Optional<String> query = loadQuery("queries/sample-query-spo.rq");

		assertTrue(query.isPresent());
		assertTrue(query.get().contains("QueryUtilsTest.testLoadQuery_ok()"));
	}

	@Test
	void testLoadQuery_null() {
		final Optional<String> query = loadQuery(null);

		assertNull(query);
	}

	@Test
	void testLoadQuery_fileNotExists() {
		final Optional<String> query = loadQuery("foo/bar/inexistant-query.rq");

		assertFalse(query.isPresent());
	}

}
