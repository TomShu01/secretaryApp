import React, { useEffect, useRef, useState, useContext } from 'react';
import cytoscape from 'cytoscape';
import { TaskContext } from './view';

function getSortedNodePositions(cy) {
  // Get all nodes from the Cytoscape instance
  const nodes = cy.nodes();

  // Map the nodes to an array of objects containing the node and its position in the original array
  const nodesWithPositions = nodes.map((node, index) => ({
    id: parseInt(node.id()),  // Convert id to an integer
    position: node.renderedPosition() // Store the original position
  }));

  // Sort the nodes by their id
  nodesWithPositions.sort((a, b) => a.id - b.id);

  // Extract the original positions in sorted order
  const sortedPositions = nodesWithPositions.map(item => item.position);

  return sortedPositions;
}

const CytoscapeGraph = ({ elements }) => {
  const cyRef = useRef(null);
  const [cyInstance, setCyInstance] = useState(null);
  const [positions, setPositions] = useContext(TaskContext);

  useEffect(() => {
    // Initialize Cytoscape instance
    const cy = cytoscape({
      container: cyRef.current, // container to render in
      elements: elements, // elements from the JSON
      style: [ // stylesheet for the graph
        {
          selector: 'node',
          style: {
            'background-color': '#666',
            'label': 'data(label)',
            'text-valign': 'center',
            'color': 'white',
          }
        },
        {
          selector: 'edge',
          style: {
            'width': 3,
            'line-color': '#ccc',
            'target-arrow-color': '#ccc',
            'target-arrow-shape': 'triangle',
            'curve-style': 'bezier'
          }
        }
      ],
      layout: {
        name: 'grid',
        rows: 1
      }
    });

    setCyInstance(cy)

    setPositions(getSortedNodePositions(cy).map((position) => {
      return { top: position.y, left: position.x }
    }));

    return () => {
      // Clean up Cytoscape instance on component unmount
      cy.destroy();
    };
  }, []); // re-run the effect if elements change

  useEffect(() => {
    if (cyInstance) {
      // Listen for node addition and removal events
      cyInstance.on('render', () => {
        setPositions(getSortedNodePositions(cyInstance).map((position) => {
          return { top: position.y, left: position.x }
        }));
      });
    }
  }, [cyInstance])

  return (
    <div
      ref={cyRef}
      style={{ width: '100%', height: '100%', border: '1px solid black' }}
    />
  );
};

export default CytoscapeGraph;
