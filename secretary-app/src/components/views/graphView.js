import React, { useState, useEffect } from 'react';
import CytoscapeGraph from '../graph'

function generateGraphFromTasks(tasks) {
  const nodes = tasks.map((task) => {
    return {data: { id: task.id, label: task.name }}
  })

  const edges = [];

  tasks.forEach(task => {
    // Add an edge from the predecessor to the current task if a predecessor exists
    if (task.predecessor !== null && task.predecessor !== undefined) {
      const edge = { data: { source: task.predecessor, target: task.id } };
      if (!edges.some(e => e.data.source === edge.data.source && e.data.target === edge.data.target)) {
        edges.push(edge);
      }
    }

    // Add an edge from the current task to the successor if a successor exists
    if (task.successor !== null && task.successor !== undefined) {
      const edge = { data: { source: task.id, target: task.successor } };
      if (!edges.some(e => e.data.source === edge.data.source && e.data.target === edge.data.target)) {
        edges.push(edge);
      }
    }
  });
  
  // Return the JSON object with nodes and edges
  return { nodes, edges };
}

const GraphView = ({ tasks, setTasks, tasksFlag }) => {
  const [size, setSize] = useState({ height: '75px', width: '200px' })
  const [visible, setVisible] = useState('visible')

  useEffect(() => {
      const updatedTasks = tasks.map(task => ({
          ...task,
          size: size,
          visible: visible
      }));
      setTasks(updatedTasks)
  }, [size, visible, tasksFlag])

  return (
      <div style={{width: '100%', height: '100%'}}>
          <CytoscapeGraph elements={generateGraphFromTasks(tasks)} />
      </div>
  )
}

export default GraphView