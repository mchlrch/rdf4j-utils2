package ch.miranet.rdf4jutils2.model.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.rdf4j.model.Namespace;
import org.eclipse.rdf4j.model.impl.SimpleNamespace;

public class NamespaceCollection {

	final Set<Namespace> namespaces = new HashSet<>();

	public static NamespaceCollection create() {
		return new NamespaceCollection();
	}
	
	public static NamespaceCollection create(Collection<Namespace> namespaces) {
		final NamespaceCollection result = new NamespaceCollection();		
		namespaces.forEach(result::add);
		
		return result;
	}

	/**
	 * @param prefix The namespace's prefix.
	 * @param name   The namespace's name.
	 */
	public NamespaceCollection add(String prefix, String name) {
		this.namespaces.add(new SimpleNamespace(prefix, name));
		return this;
	}

	public NamespaceCollection add(Namespace ns) {
		this.namespaces.add(ns);
		return this;
	}

	public Collection<Namespace> buildSnapshot() {
		final List<Namespace> result = new ArrayList<Namespace>(this.namespaces);
		result.sort(Comparator.naturalOrder());
		return result;
	}

}
