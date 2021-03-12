package ch.miranet.rdf4jutils2.model.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ModelBuilderFacadeTest {

	private ModelBuilder refModelBuilder;
	private ModelBuilderFacade modelBuilderFacade;
	private Namespace nsData;

	@BeforeEach
	public void setUp() {
		nsData = new SimpleNamespace("", "http://data.example.org/");

		refModelBuilder = new ModelBuilder();
		refModelBuilder.setNamespace(nsData);

		modelBuilderFacade = new ModelBuilderFacade(nsData);
	}

	@Test
	void addNullable_null() {
		modelBuilderFacade.subject(":Hawaii")
				.addNullable("rdfs:label", null);

		final Model model = modelBuilderFacade.build();

		assertTrue(model.isEmpty());
	}

	@Test
	void addNullable_notNull() {
		modelBuilderFacade.subject(":Hawaii")
				.addNullable("rdfs:label", "Hawaiʻi");

		final Model model = modelBuilderFacade.build();
		assertEquals(1, model.size());

		final Model refModel = refModelBuilder.subject(":Hawaii")
				.add("rdfs:label", "Hawaiʻi")
				.build();

		assertEquals(refModel, model);
	}

	@Test
	void addEach_map_singleValue() {
		final Map<IRI, Object> propertyValues = new HashMap<>();
		propertyValues.put(RDFS.LABEL, "Hawaiʻi");

		modelBuilderFacade.subject(":Hawaii")
				.addEach(propertyValues);

		final Model model = modelBuilderFacade.build();
		assertEquals(1, model.size());

		final Model refModel = refModelBuilder.subject(":Hawaii")
				.add("rdfs:label", "Hawaiʻi")
				.build();

		assertEquals(refModel, model);
	}

	@Test
	void addEach_map_iterableValue() {
		final Map<IRI, Object> propertyValues = new HashMap<>();
		propertyValues.put(RDFS.LABEL, Arrays.asList("Hawaiʻi", "Big Island"));

		modelBuilderFacade.subject(":Hawaii")
				.addEach(propertyValues);

		final Model model = modelBuilderFacade.build();
		assertEquals(2, model.size());

		final Model refModel = refModelBuilder.subject(":Hawaii")
				.add("rdfs:label", "Hawaiʻi")
				.add("rdfs:label", "Big Island")
				.build();

		assertEquals(refModel, model);
	}

	@Test
	void convertAnyArrayToList_string_array() {
		final List<?> list = ModelBuilderFacade.convertAnyArrayToList(new String[] { "Hawaiʻi", "Big Island" });

		assertEquals(2, list.size());
		assertTrue(list.contains("Hawaiʻi"));
		assertTrue(list.contains("Big Island"));
	}

	@Test
	void convertAnyArrayToList_int_array() {
		final List<?> list = ModelBuilderFacade.convertAnyArrayToList(new int[] { 1, 2, 3 });

		assertEquals(3, list.size());
		assertTrue(list.contains(1));
		assertTrue(list.contains(2));
		assertTrue(list.contains(3));
	}

	@Test
	void addEach_map_arrayValue_string_array() {
		final Map<IRI, Object> propertyValues = new HashMap<>();
		propertyValues.put(RDFS.LABEL, new String[] { "Hawaiʻi", "Big Island" });

		modelBuilderFacade.subject(":Hawaii")
				.addEach(propertyValues);

		final Model model = modelBuilderFacade.build();
		assertEquals(2, model.size());

		final Model refModel = refModelBuilder.subject(":Hawaii")
				.add("rdfs:label", "Hawaiʻi")
				.add("rdfs:label", "Big Island")
				.build();

		assertEquals(refModel, model);
	}

	@Test
	void addEach_map_arrayValue_int_array() {
		final Map<IRI, Object> propertyValues = new HashMap<>();
		propertyValues.put(RDFS.COMMENT, new int[] { 1, 2, 3 });

		modelBuilderFacade.subject(":Hawaii")
				.addEach(propertyValues);

		final Model model = modelBuilderFacade.build();
		assertEquals(3, model.size());

		final Model refModel = refModelBuilder.subject(":Hawaii")
				.add("rdfs:comment", 1)
				.add("rdfs:comment", 2)
				.add("rdfs:comment", 3)
				.build();

		assertEquals(refModel, model);
	}

	@Test
	void addEach_object_collection() {
		final List<String> objectValues = Arrays.asList("Hawaiʻi", "Big Island");

		modelBuilderFacade.subject(":Hawaii")
				.addEach("rdfs:label", objectValues);

		final Model model = modelBuilderFacade.build();
		assertEquals(2, model.size());

		final Model refModel = refModelBuilder.subject(":Hawaii")
				.add("rdfs:label", "Hawaiʻi")
				.add("rdfs:label", "Big Island")
				.build();

		assertEquals(refModel, model);
	}

}
