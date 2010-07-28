package org.coode.owl.mngr.impl;

import org.coode.owl.mngr.HierarchyProvider;
import org.coode.owl.mngr.OWLServer;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 26, 2010<br><br>
 */
public class OntologyHierarchyProvider implements HierarchyProvider<OWLOntology> {

    private OWLServer server;

    public OntologyHierarchyProvider(OWLServer server) {
        this.server = server;
    }

    public Set<OWLOntology> getRoots() {
        Set<OWLOntology> allImported = new HashSet<OWLOntology>();
        for (OWLOntology ontology : server.getOntologies()){
            allImported.addAll(ontology.getImports());
        }
        Set<OWLOntology> roots = server.getOntologies();
        if (allImported.size() != roots.size()){ // sometimes there are cycles - then just show all
            roots.removeAll(allImported);
        }
        return roots;
    }

    public boolean isRoot(OWLOntology node) {
        return getRoots().contains(node);
    }

    public boolean isLeaf(OWLOntology node) {
        return getChildren(node).isEmpty();
    }

    public Set<OWLOntology> getParents(OWLOntology node) {
        Set<OWLOntology> parents = new HashSet<OWLOntology>();
        for (OWLOntology parent : server.getOntologies()){
            for (OWLImportsDeclaration decl : parent.getImportsDeclarations()){
                if (node.equals(server.getOntologyForIRI(decl.getIRI()))){
                    parents.add(parent);
                    break;
                }
            }
        }
        return parents;
    }

    public Set<OWLOntology> getChildren(OWLOntology node) {
        Set<OWLOntology> imports = new HashSet<OWLOntology>();
        for (OWLImportsDeclaration decl : node.getImportsDeclarations()){
            imports.add(server.getOntologyForIRI(decl.getIRI()));
        }
        return imports;
    }

    public Set<OWLOntology> getEquivalents(OWLOntology node) {
        return Collections.emptySet();
    }

    public Set<OWLOntology> getDescendants(OWLOntology node) {
        return node.getImports();
    }

    public Set<OWLOntology> getAncestors(OWLOntology node) {
        Set<OWLOntology> ancestors = new HashSet<OWLOntology>();
        for (OWLOntology ancestor : server.getOntologies()){
            if (ancestor.getImports().contains(node)){
                ancestors.add(ancestor);
            }
        }
        return ancestors;
    }

    public boolean hasAncestor(OWLOntology node, OWLOntology ancestor) {
        return !node.equals(ancestor) && ancestor.getImports().contains(node);
    }

    public void dispose() {
        server = null;
    }
}
