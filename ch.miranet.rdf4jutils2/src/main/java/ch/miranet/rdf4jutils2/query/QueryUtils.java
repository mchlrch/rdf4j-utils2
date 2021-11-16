package ch.miranet.rdf4jutils2.query;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public class QueryUtils {

	public static Optional<String> loadQuery(final String queryFilePath) {
		if (queryFilePath == null) {
			return null;
		}

		final InputStream inputStream = QueryUtils.class.getClassLoader().getResourceAsStream(queryFilePath);

		if (inputStream == null) {
			return Optional.empty();
		}

		try {
			try (final InputStreamReader isr = new InputStreamReader(inputStream, UTF_8.toString());
					final BufferedReader reader = new BufferedReader(isr)) {
				return Optional.of(reader.lines().collect(joining(System.lineSeparator())));
			}
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load query " + queryFilePath, e);
		} finally {
			try {
				inputStream.close();
			} catch (final IOException e) {
				throw new RuntimeException("Failed to close input stream.", e);
			}
		}
	}

}
