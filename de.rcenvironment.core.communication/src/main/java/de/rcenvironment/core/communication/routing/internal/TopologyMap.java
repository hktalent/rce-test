/*
 * Copyright (C) 2006-2012 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.communication.routing.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.rcenvironment.core.communication.model.NetworkNodeInformation;
import de.rcenvironment.core.communication.model.NodeIdentifier;
import de.rcenvironment.core.communication.model.impl.NetworkNodeInformationImpl;
import de.rcenvironment.core.communication.model.internal.NodeInformationHolder;
import de.rcenvironment.core.communication.model.internal.NodeInformationRegistryImpl;
import de.rcenvironment.rce.communication.PlatformIdentifier;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;

/**
 * Represents a model of the entire network (i.e. topology map or link state database). A node (
 * {@link TopologyNode}) in the graph corresponds to a RCE instance and an edge represents a
 * connection/link ({@link TopologyLink}) between two RCE instances. The graph is a directed sparse
 * multigraph {@link DirectedSparseMultigraph}.
 * 
 * TODO review: reduce synchronization
 * 
 * @see <a href="http://jung.sourceforge.net/doc/api/index.html">JUNG2</a>
 * 
 * @author Phillip Kroll
 * @author Robert Mischke
 */
public final class TopologyMap {

    /**
     * TODO Remove logger.
     */
    private static final Log LOGGER = LogFactory.getLog(TopologyMap.class);

    /**
     * @see <a
     *      href="http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/graph/DirectedSparseMultigraph.html">DirectedSparseMultigraph</a>
     */
    private final DirectedSparseMultigraph<TopologyNode, TopologyLink> networkModel =
        new DirectedSparseMultigraph<TopologyNode, TopologyLink>();

    private final NetworkNodeInformation localNodeInformation;

    private final NodeIdentifier localNodeId;

    /**
     * The constructor.
     * 
     * @param ownNodeInformation The node that uses the {@link TopologyMap} instance to represent
     *        its view on the network.
     */
    public TopologyMap(NetworkNodeInformation ownNodeInformation) {
        this.localNodeInformation = ownNodeInformation;
        this.localNodeId = ownNodeInformation.getWrappedNodeId();
        TopologyNode localNode = addNode(localNodeId);
        // initialize sequence number
        localNode.invalidateSequenceNumber();
    }

    /**
     * Convenience constructor for unit tests.
     */
    protected TopologyMap(NodeIdentifier nodeId) {
        this(new NetworkNodeInformationImpl(nodeId));
    }

    /**
     * Convert a list of network nodes to a list of their platform identifiers.
     * 
     * @param networkNodes List of network nodes.
     * @return The list of platform identifiers.
     */
    public static Collection<NodeIdentifier> toNodeIdentifiers(Collection<TopologyNode> networkNodes) {
        Collection<NodeIdentifier> result = new ArrayList<NodeIdentifier>();
        for (TopologyNode networkNode : networkNodes) {
            result.add(networkNode.getNodeIdentifier());
        }
        return result;
    }

    /**
     * Integrates information from an {@link LinkStateAdvertisement} into the graph.
     * 
     * @param lsa The link state advertisement.
     * @return Whether the link state advertisement was "accepted" and therefore integrated in the
     *         graph or considered as being to old.
     */
    public synchronized boolean update(LinkStateAdvertisement lsa) {
        // TODO optimize algorithm --krol_ph

        NodeIdentifier lsaOwner = lsa.getOwner();

        if (lsaOwner.equals(localNodeId)) {
            TopologyNode ownNode = getNode(localNodeId);
            // TODO eliminate these from being sent; at least as part of the initial batch response
            // -- misc_ro
            LOGGER.debug("Ignored an update LSA for the local node: LSA seqNo=" + lsa.getSequenceNumber() + ", local seqNo="
                + ownNode.getSequenceNumber());
            return false;
        }

        boolean lsaRootPresent = containsNode(lsaOwner);

        TopologyNode lsaRoot = getNode(lsaOwner);

        if ((lsaRootPresent && lsaRoot.getSequenceNumber() >= lsa.getSequenceNumber()) 
            || (!lsaRootPresent && LinkStateAdvertisement.REASON_SHUTDOWN.equals(lsa.getReason()))) {
            // lsa is too old, discard it, do not forward it
           // we know already that the node is not there anymore
            return false;
        }

        // TODO Maybe removing and re-inserting is not the best way to update the graph.
        // remove node and edges
        if (lsaRootPresent) {
            for (TopologyNode successor : networkModel.getSuccessors(lsaRoot)) {
                for (TopologyLink link : networkModel.findEdgeSet(lsaRoot, successor)) {
                    removeLink(link);
                }
            }
        }

        // the LSA tells something about a node that has new or updated information
        if (LinkStateAdvertisement.REASON_STARTUP.equals(lsa.getReason())
            || LinkStateAdvertisement.REASON_UPDATE.equals(lsa.getReason())) {

            // add node and set properties
            TopologyNode node = addNode(lsaOwner);
            node.setRouting(lsa.isRouting());
            node.setLastSequenceNumber(lsa.getSequenceNumber());
            node.setLastGraphHashCode(lsa.getGraphHashCode());
            node.setDisplayName(lsa.getDisplayName());
            node.setIsWorkflowHost(lsa.getIsWorkflowHost());

            // TODO temporary approach
            NodeInformationHolder metaInformationHolder =
                NodeInformationRegistryImpl.getInstance().getWritableNodeInformation(lsaOwner.getNodeId());
            metaInformationHolder.setDisplayName(lsa.getDisplayName());
            metaInformationHolder.setIsWorkflowHost(lsa.getIsWorkflowHost());

            // add links (and remote nodes)
            for (TopologyLink link : lsa.getLinks()) {
                if (!containsNode(link.getDestination())) {
                    addNode(link.getDestination());
                }
                addLink(link);
            }
        }

        // if the purpose of the LSA was to tell that an instance is shutting down
        if (LinkStateAdvertisement.REASON_SHUTDOWN.equals(lsa.getReason())) {
            // LOGGER.debug(String.format("%s has been informed that %s will shut down soon.",
            // owner,
            // lsa.getOwner()));
            // FIXME explicitly close/remove connections
            // TODO check: are both incoming and outgoing links removed from topology map?
            removeNode(lsaRoot);
        }

        return true;
    }

    /**
     * Computes the shortest path from the source node to the destination node. The Dijkstra
     * shortest path algorithm is used to determine the shortest path.
     * 
     * @see <a
     *      href="http://jung.sourceforge.net/doc/api/edu/uci/ics/jung/algorithms/shortestpath/DijkstraShortestPath.html">
     *      DijkstraShortestPath</a>
     * @param source The source platform
     * @param destination The destination platform
     * @return The shortest path between source and destination.
     */
    public synchronized NetworkRoute getShortestPath(NodeIdentifier source, NodeIdentifier destination) {
        // TODO optimization: provide method that only returns the next step (if rest is irrelevant)
        // TODO optimization: add caching!
        long elapsed = 0;

        List<TopologyLink> path = new ArrayList<TopologyLink>();
        List<NodeIdentifier> nodes = new ArrayList<NodeIdentifier>();

        TopologyNode sourceNode = getNode(source);
        TopologyNode destinationNode = getNode(destination);
        // TODO check: behavior if these conditions are NOT met?
        if (sourceNode != null && destinationNode != null) {
            DijkstraShortestPath<TopologyNode, TopologyLink> alg =
                new DijkstraShortestPath<TopologyNode, TopologyLink>(networkModel);

            long start = System.nanoTime();
            path = alg.getPath(sourceNode, destinationNode);
            elapsed = System.nanoTime() - start;

            for (TopologyLink link : path) {
                nodes.add(networkModel.getEndpoints(link).getSecond().getNodeIdentifier());
            }
        }
        // LOGGER.debug(String.format("Route computation: %s ms", elapsed * 10e-9));
        return new NetworkRoute(source, destination, path, nodes, elapsed);
    }

    /**
     * Get the ids of all reachable nodes.
     * 
     * @param restrictToWorkflowHostsAndSelf No description available.
     * @return Set of platform identifiers.
     */
    public synchronized Set<PlatformIdentifier> getIdsOfReachableNodes(boolean restrictToWorkflowHostsAndSelf) {
        Set<PlatformIdentifier> result = new HashSet<PlatformIdentifier>();
        // get the map of reachable nodes by (ab)using the Dijkstra algorithm;
        // TODO is there a more efficient approach available in JUNG?
        DijkstraShortestPath<TopologyNode, TopologyLink> alg =
            new DijkstraShortestPath<TopologyNode, TopologyLink>(networkModel);
        Map<TopologyNode, Number> distanceMap = alg.getDistanceMap(getNode(localNodeId));
        for (TopologyNode node : distanceMap.keySet()) {
            if (restrictToWorkflowHostsAndSelf) {
                boolean isLocalNode = localNodeId.equals(node.getNodeIdentifier());
                if (!(isLocalNode || node.getIsWorkflowHost())) {
                    continue;
                }
            }
            // TODO remove typecast once NetworkIdentifier vs. PlatformIdentifier is done
            result.add((PlatformIdentifier) node.getNodeIdentifier());
        }
        return result;
    }

    /**
     * FIXME unspecified semantics; rename or replace for clarity -- misc_ro.
     * @return The link state advertisement.
     */
    public synchronized LinkStateAdvertisement generateLsa() {
        return generateLsa(getLocalNodeId(), true);
    }

    /**
     * 
     * @return The LSA cache.
     */
    public synchronized LinkStateAdvertisementBatch generateLsaBatchOfAllNodes() {
        LinkStateAdvertisementBatch lsaCache = new LinkStateAdvertisementBatch();
        for (TopologyNode node : networkModel.getVertices()) {
            lsaCache.put(node.getNodeIdentifier(), generateLsa(node.getNodeIdentifier(), false));
        }
        return lsaCache;
    }

    private synchronized LinkStateAdvertisement generateLsa(NodeIdentifier root, boolean incSequenceNumber) {
        TopologyNode rootNode = getNode(root);
        // TODO This is not the right place to update the graph hash code
        // TODO review: shouldn't this happen *after* incrementing the sequence number? -- misc_ro
        rootNode.setLastGraphHashCode(hashCode());

        long sequenceNumber;
        if (incSequenceNumber) {
            sequenceNumber = rootNode.invalidateSequenceNumber();
        } else {
            sequenceNumber = rootNode.getSequenceNumber();
        }
        return LinkStateAdvertisement.createUpdateLsa(
            root, rootNode.getDisplayName(),
            rootNode.getIsWorkflowHost(),
            sequenceNumber,
            hashCode(),
            rootNode.isRouting(),
            networkModel.getOutEdges(rootNode)
        );
    }

    /**
     * @return The link state advertisement.
     */
    public synchronized LinkStateAdvertisement generateShutdownLSA() {
        TopologyNode myself = getNode(getLocalNodeId());
        myself.setLastGraphHashCode(hashCode());
        return LinkStateAdvertisement.createShutDownLsa(
            getLocalNodeId(), localNodeInformation.getDisplayName(),
            localNodeInformation.getIsWorkflowHost(),
            myself.invalidateSequenceNumber()
        );
    }

    /**
     * Produces a startup LSA.
     * 
     * @return The startup LSAs.
     */
    public synchronized LinkStateAdvertisement generateStartupLSA() {
        TopologyNode myself = getNode(getLocalNodeId());
        myself.setLastGraphHashCode(hashCode());
        return LinkStateAdvertisement.createStartUpLsa(
            getLocalNodeId(), localNodeInformation.getDisplayName(),
            localNodeInformation.getIsWorkflowHost(),
            myself.invalidateSequenceNumber(),
            myself.isRouting(),
            networkModel.getOutEdges(myself)
        );
    }

    /**
     * Tests whether every node in the network has exactly the same model of the network.
     * 
     * @return Whether the network models are fully converged.
     */
    public synchronized boolean hasSameTopologyHashesForAllNodes() {
        for (TopologyNode node : getNodes()) {
            if (node.getLastGraphHashCode() != hashCode()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns all {@link TopologyLink}s between two nodes that are present in the network graph.
     * 
     * @param source The source node.
     * @param destination The destination node.
     * @return The list of all edges/links in the network/graph.
     */
    public synchronized Collection<TopologyLink> getAllLinksBetween(TopologyNode source, TopologyNode destination) {
        if (source != null && destination != null) {
            return networkModel.findEdgeSet(source, destination);
        }
        return new ArrayList<TopologyLink>();
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    // TODO synchronized equals() might be risky
    public synchronized boolean equals(Object object) {
        if (object instanceof TopologyMap) {
            return hashCode() == object.hashCode();
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public synchronized int hashCode() {
        // TODO Review hash code generation. Maybe this can be done simpler.

        String graphSerialization = "NetworkGraph:";

        List<TopologyNode> networkNodes = new ArrayList<TopologyNode>(networkModel.getVertices());
        Collections.sort(networkNodes);

        for (TopologyNode networkNode : networkNodes) {
            graphSerialization += networkNode.hashCode();
        }

        List<TopologyLink> networkLinks = new ArrayList<TopologyLink>(networkModel.getEdges());
        Collections.sort(networkLinks);

        for (TopologyLink networkLink : networkLinks) {
            graphSerialization += networkLink.hashCode();
        }

        return graphSerialization.hashCode();
    }

    /**
     * @param nodeIdentifier The node identifier.
     * @return The network node.
     */
    public synchronized TopologyNode getNode(NodeIdentifier nodeIdentifier) {
        // TODO use map instead of linear search here! -- misc_ro
        for (TopologyNode networkNode : networkModel.getVertices()) {
            if (networkNode.getNodeIdentifier().equals(nodeIdentifier)) {
                return networkNode;
            }
        }
        return null;
    }

    /**
     * Convenience method to get the sequence number for a node by its id. If no matching node
     * exists, a {@link IllegalArgumentException} is thrown.
     * 
     * @param id the id of the node
     * @return the current sequence number of that node
     */
    public long getSequenceNumberOfNode(NodeIdentifier id) {
        TopologyNode node = getNode(id);
        if (node == null) {
            throw new IllegalArgumentException("No such node: " + id.getNodeId());
        }
        return node.getSequenceNumber();
    }

    /**
     * @param nodeIdentifier The node identifier.
     * @return Whether graph contains the node.
     */
    public synchronized boolean containsNode(NodeIdentifier nodeIdentifier) {
        return containsNode(new TopologyNode(nodeIdentifier));
    }

    /**
     * @param networkNode The network node.
     * @return Whether graph contains the node.
     */
    public synchronized boolean containsNode(TopologyNode networkNode) {
        return networkModel.containsVertex(networkNode);
    }

    /**
     * @param source The source platform.
     * @param destination The destination platform.
     * @return Whether the graph contains <code>source</code> and <code>destination</code> platform
     *         AND at least one edge between both platforms.
     */
    public synchronized boolean containsLinkBetween(NodeIdentifier source, NodeIdentifier destination) {
        TopologyNode sourceNode = getNode(source);
        TopologyNode destinationNode = getNode(destination);
        if (sourceNode != null && destinationNode != null) {
            return networkModel.getSuccessors(sourceNode).contains(destinationNode);
        }
        return false;
    }

    /**
     * Tests whether a link is present in the network.
     * 
     * @param source The source of the link.
     * @param destination The destination of the link.
     * @param connectionId The id of the connection.
     * @return Whether the graph contains the link.
     */
    public synchronized boolean containsLink(NodeIdentifier source, NodeIdentifier destination, String connectionId) {
        return networkModel.containsEdge(new TopologyLink(source, destination, connectionId));
    }

    /**
     * Tests whether a link is present in the network.
     * 
     * @see {@link TopologyLink#equals(Object)}
     * @param networkLink The network link.
     * @return Whether the graph contains the link.
     */
    public synchronized boolean containsLink(TopologyLink networkLink) {
        return networkModel.containsEdge(networkLink);
    }

    /**
     * @param source The source node.
     * @param destination The destination node
     * @param connectionId The id of the connection
     * @param string
     * @return Whether adding the link was successful.
     */
    public synchronized TopologyLink addLink(NodeIdentifier source, NodeIdentifier destination, String connectionId) {
        TopologyLink newLink = new TopologyLink(source, destination, connectionId);
        if (!addLink(newLink)) {
            throw new IllegalStateException("Failed to add new topology link");
        }
        return newLink;
    }

    /**
     * Add a link into the network topology.
     * TODO clean up.
     * 
     * @param networkLink The network link.
     * @return true 
     */
    public synchronized boolean addLink(TopologyLink networkLink) {
        if (networkLink.getConnectionId() == null) {
            throw new NullPointerException("Connection id must not be null");
        }
        TopologyNode sourceNode = getNode(networkLink.getSource());
        TopologyNode destinationNode = getNode(networkLink.getDestination());
        // new:
        if (sourceNode == null) {
            // add node implicitly
            sourceNode = addNode(networkLink.getSource());
        }
        if (destinationNode == null) {
            // add node implicitly
            destinationNode = addNode(networkLink.getDestination());
        }
        if (!networkModel.addEdge(networkLink, sourceNode, destinationNode)) {
            LOGGER.warn(String.format("Link edge %s was not added to graph -- duplicate?", networkLink));
            return false;
        }
        return true;
        // old:
        // if (sourceNode != null && destinationNode != null && !sourceNode.equals(destinationNode))
        // {
        // if (containsLink(networkLink)) {
        // removeLink(networkLink);
        // }
        // networkModel.addEdge(networkLink, sourceNode, destinationNode);
        // return true;
        // }
        // return false;
    }

    /**
     * @param source The source node.
     * @param destination The destination node
     * @param connectionId The connection id
     * @return Whether adding the link was successful.
     */
    public synchronized boolean removeLink(NodeIdentifier source, NodeIdentifier destination, String connectionId) {
        return removeLink(new TopologyLink(source, destination, connectionId));
    }

    /**
     * @param link The network link.
     * @return Whether adding the link was successful.
     */
    public synchronized boolean removeLink(TopologyLink link) {
        if (containsLink(link)) {
            return networkModel.removeEdge(link);
        } else {
            LOGGER.warn("Edge removal requested for non-existant link: " + link);
            return false;
        }
    }

    /**
     * Remove a node.
     * 
     * @param node The node
     */
    public synchronized void removeNode(TopologyNode node) {
        networkModel.removeVertex(node);
    }

    /**
     * 
     * Remove a node.
     * 
     * @param node The node
     */
    public synchronized void removeNode(NodeIdentifier node) {
        removeNode(new TopologyNode(node));
    }

    /**
     * @param nodeIdentifier The platform identifier.
     * @return Whether the platform existed in the graph before.
     */
    public synchronized TopologyNode addNode(NodeIdentifier nodeIdentifier) {
        TopologyNode existingNetworkNode = getNode(nodeIdentifier);
        if (existingNetworkNode == null) {
            TopologyNode node = new TopologyNode(nodeIdentifier);
            networkModel.addVertex(node);
            return node;
        }
        return existingNetworkNode;
    }

    /**
     * @return The number of platforms in the graph.
     */
    public synchronized int getNodeCount() {
        return networkModel.getVertexCount();
    }

    /**
     * @return The number of links in the graph.
     */
    public synchronized int getLinkCount() {
        return networkModel.getEdgeCount();
    }

    /**
     * @return A list of all links.
     */
    public synchronized Collection<TopologyLink> getAllLinks() {
        return networkModel.getEdges();
    }

    /**
     * Searches for a network link that is outgoing from the {@link TopologyMap#localNodeId} and
     * contains the network contact.
     * 
     * @param connectionId The network connection id
     * @return The found network link or null.
     */
    public synchronized TopologyLink getLinkForConnection(String connectionId) {
        // TODO review: replace by map? -- misc_ro
        for (TopologyLink link : networkModel.getOutEdges(getNode(getLocalNodeId()))) {
            if (link.getConnectionId().equals(connectionId)) {
                return link;
            }
        }
        return null;
    }

    /**
     * TODO Enter comment!
     *  
     * @param connectionId The connection id.
     * @return Is there a link for the connection.
     */
    public synchronized boolean hasLinkForConnection(String connectionId) {
        return getLinkForConnection(connectionId) != null;
    }

    /**
     * @return A list of all nodes.
     */
    public synchronized Collection<TopologyNode> getNodes() {
        return networkModel.getVertices();
    }

    /**
     * @param networkNode The network node.
     * @return All successor nodes (platforms) in the current graph.
     */
    public synchronized Collection<TopologyNode> getSuccessors(TopologyNode networkNode) {
        return networkModel.getSuccessors(networkNode);
    }

    /**
     * @return All successor nodes of the graph owner.
     */
    public synchronized Collection<TopologyNode> getSuccessors() {
        return getSuccessors(getLocalNodeId());
    }

    /**
     * @param nodeIdentifier The platform identifier.
     * @return All successor nodes (platforms) in the current graph.
     */
    public synchronized Collection<TopologyNode> getSuccessors(NodeIdentifier nodeIdentifier) {
        return getSuccessors(getNode(nodeIdentifier));
    }

    /**
     * @param networkNode The network node.
     * @return All predecessor nodes (platforms) in the current graph.
     */
    public synchronized Collection<TopologyNode> getPredecessors(TopologyNode networkNode) {
        return networkModel.getPredecessors(networkNode);
    }

    /**
     * @return Returns the owner.
     */
    public synchronized NodeIdentifier getLocalNodeId() {
        return localNodeId;
    }

}
