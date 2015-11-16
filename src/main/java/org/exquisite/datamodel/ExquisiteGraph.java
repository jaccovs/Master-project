/**
 *
 */
package org.exquisite.datamodel;

import java.util.*;

/**
 * @author Arash
 */
public class ExquisiteGraph<Typ> {

    private Map<Typ, Vertex> Vertex;

    /**
     * Constructor makes an empty graph
     */
    public ExquisiteGraph() {
        this.Vertex = new HashMap<Typ, Vertex>();
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        ExquisiteGraph<String> exquisiteGraph = new ExquisiteGraph<String>();

        exquisiteGraph.addVertex("A1");
        exquisiteGraph.addVertex("A2");
        exquisiteGraph.addVertex("B1");
        exquisiteGraph.addVertex("B2");
        exquisiteGraph.addVertex("C1");
        exquisiteGraph.addVertex("C2");
        exquisiteGraph.addVertex("D1");
        exquisiteGraph.addVertex("D2");

        // B1 = A1 + A2
        exquisiteGraph.addEdge("A1", "B1");
        exquisiteGraph.addEdge("A2", "B1");

        // B2 = A2 +3
        exquisiteGraph.addEdge("A2", "B2");

        // C1 = B1 + B2
        exquisiteGraph.addEdge("B1", "C1");
        exquisiteGraph.addEdge("B2", "C1");

        // C2 = B1 * B2
        exquisiteGraph.addEdge("B1", "C2");
        exquisiteGraph.addEdge("B2", "C2");

        //D1 = C1 + C2
        exquisiteGraph.addEdge("C1", "D1");
        exquisiteGraph.addEdge("C2", "D1");

        //D2 = C1 + A1
        exquisiteGraph.addEdge("C1", "D2");
        exquisiteGraph.addEdge("A1", "D2");

        System.out.println("All Precedents of D1: " + exquisiteGraph.getAllParents("D1"));
        System.out.println("All Dependents of A1: " + exquisiteGraph.getAllChilds("A1"));
    }

    /**
     * @param name
     * @return true if the graph contains the vertex
     */
    public boolean contains(Typ name) {
        return Vertex.containsKey(name);
    }

    /**
     * Adds a new vertex with the given name to the graph
     *
     * @param name
     */
    public void addVertex(Typ name) {
        if (!this.contains(name)) {
            this.Vertex.put(name, new Vertex(name));
        }
    }

    /**
     * Removes a vertex with given name, if it has no neighbor
     *
     * @param name Name of Vertex
     * @throws Exception Vertex has Edge and can not be removed
     */
    public void deleteVertex(Typ name) throws Exception {
        if (this.Vertex.get(name).hasNoNeighbour()) {
            this.Vertex.remove(name);
        } else {
            throw new Exception("Vertex can not be removed, it has neighbors");
        }
    }

    /**
     * Adds a new directed edge
     *
     * @param from name of the start vertex
     * @param to   name of the target
     * @throws Exception Start or target Vertex doesn't exist in the graph
     */
    public void addEdge(Typ from, Typ to) throws Exception {
        if (this.contains(from) && this.contains(to)) {
            Vertex Vertexfrom = this.getVertex(from);
            Vertex Vertexto = this.getVertex(to);
            Vertexfrom.addChild(Vertexto);
            Vertexto.addParent(Vertexfrom);
        } else {
            if (!this.contains(from)) {
                throw new Exception("Start vertex doesn't exist in the graph: " + from);
            } else {
                throw new Exception("Target vertex doesn't exist in the graph" + to);
            }
        }
    }

    /**
     * Removes an Edge
     *
     * @param from
     * @param to
     * @throws Exception Start or target Vertex doesn't exist in the graph
     */
    public void deleteEdge(Typ from, Typ to) throws Exception {
        if (this.contains(from) && this.contains(to)) {
            Vertex Vertexfrom = this.getVertex(from);
            Vertex Vertexto = this.getVertex(to);
            if (Vertexfrom.getChilds().contains(Vertexto) && Vertexto.getParents().contains(Vertexfrom)) {
                Vertexfrom.deleteChild(Vertexto);
                Vertexto.deleteParent(Vertexfrom);
            }
        } else {
            throw new Exception("Start or target Vertex doesn't exist in the graph");
        }
    }

    /**
     * Returns the all names
     *
     * @return Set of names of vertices
     */
    public Set<Typ> getVertex() {
        return this.Vertex.keySet();
    }

    /**
     * Returns children (output edges) of the Vertex
     *
     * @param name
     * @return Children of the Vertex
     * @throws Exception Vertex doesn't exist in the graph
     */
    public Set<Typ> getChilds(Typ name) throws Exception {
        if (!this.contains(name)) {
            throw new Exception("Vertex doesn't exist in the graph");
        }
        return VertexToTyp(this.getVertex(name).getChilds());
    }

    /**
     * Returns all children (dependents) of the Vertex
     *
     * @param name
     * @return All children (dependents) of the Vertex
     * @throws Exception Vertex doesn't exist in the graph
     */
    public Set<Typ> getAllChilds(Typ name) throws Exception {
        if (!this.contains(name)) {
            throw new Exception("Vertex doesn't exist in the graph");
        }
        return VertexToTyp(this.getVertex(name).getAllChilds());
    }

    /**
     * Returns parents of the Vertex
     *
     * @param name
     * @return Parents of Vertex
     * @throws Exception Vertex doesn't exist in the graph
     */
    public Set<Typ> getParents(Typ name) throws Exception {
        if (!this.contains(name)) {
            throw new Exception("Vertex doesn't exist in the graph");
        }
        return VertexToTyp(this.getVertex(name).getParents());
    }

    /**
     * Returns all parents (Precedents) of the Vertex
     *
     * @param name
     * @return All parents (Precedents) of the Vertex
     * @throws Exception Vertex doesn't exist in the graph
     */
    public Set<Typ> getAllParents(Typ name) throws Exception {
        if (!this.contains(name)) {
            throw new Exception("Vertex " + name + " doesn't exist in the graph");
        }
        return VertexToTyp(this.getVertex(name).getAllParents());
    }

    private Set<Typ> VertexToTyp(Collection<Vertex> Vertex) {
        Set<Typ> result = new HashSet<Typ>();
        for (Vertex child : Vertex) {
            result.add(child.getName());
        }
        return result;
    }

    private Vertex getVertex(Typ name) {
        return Vertex.get(name);
    }

    /**
     * Vertex with unique name
     * wit a set of direct precedents and a set of direct dependents
     *
     * @author Arash
     */
    private class Vertex {
        private Typ name;
        private Set<Vertex> parents;
        private Set<Vertex> children;

        /**
         * Constructor makes a new Vertex with a unique name name
         *
         * @param name Name of Vertex
         */
        public Vertex(Typ name) {
            this.name = name;
            this.parents = new HashSet<Vertex>();
            this.children = new HashSet<Vertex>();
        }

        /**
         * @return Parents of the Vertex
         */
        public Collection<Vertex> getParents() {
            return parents;
        }

        /**
         * @return Childs of the Vertex
         */
        public Collection<Vertex> getChilds() {
            return children;
        }

        /**
         * Adds a new Vertex to direct children
         *
         * @param parent Vertex
         */
        public void addChild(Vertex child) {
            this.children.add(child);
        }

        /**
         * Adds a new Vertex to direct parents
         *
         * @param parent Vertex
         */
        public void addParent(Vertex parent) {
            this.parents.add(parent);
        }

        /**
         * Removes a Vertex from direct children
         *
         * @param Vertex to remove
         */
        public void deleteChild(Vertex Vertex) {
            children.remove(Vertex);
        }

        /**
         * Removes a Vertex from direct Parents
         *
         * @param Vertex to remove
         */
        public void deleteParent(Vertex Vertex) {
            parents.remove(Vertex);
        }

        /**
         * @return All Childs
         */
        public HashSet<Vertex> getAllChilds() {
            HashSet<Vertex> allChilds = new HashSet<Vertex>();
            HashSet<Vertex> temp = new HashSet<Vertex>();
            int size = 0;
            allChilds.addAll(this.getChilds());
            while (size < allChilds.size()) {
                size = allChilds.size();
                temp.clear();
                for (Vertex Vertex : allChilds) {
                    temp.addAll(Vertex.getChilds());
                }
                allChilds.addAll(temp);
            }
            return allChilds;
        }

        /**
         * @return All Parents
         */
        public HashSet<Vertex> getAllParents() {
            HashSet<Vertex> allParents = new HashSet<Vertex>();
            HashSet<Vertex> temp;
            int size = 0;
            allParents.addAll(this.getParents());
            while (size < allParents.size()) {
                size = allParents.size();
                temp = new HashSet<Vertex>();
                for (Vertex Vertex : allParents) {
                    temp.addAll(Vertex.getParents());
                }
                allParents.addAll(temp);
            }
            return allParents;
        }

        /**
         * @return Name of Vertex
         */
        public Typ getName() {
            return this.name;
        }

        /**
         * @return true, if the vertex has no neighbor
         */
        public boolean hasNoNeighbour() {
            return (this.getChilds().isEmpty() && this.getParents().isEmpty());
        }
    }
}
