import React, { useContext, useRef, useEffect } from 'react';
import { TaskContext } from './view';

const TaskHandle = ({ id, tasks, setTasks, tasksFlag }) => {
    const [positions, setPositions] = useContext(TaskContext);
    const componentRef = useRef(null);

    useEffect(() => {
      // updateLocation: updates the location of each task currently displayed
      const updateLocation = () => {
        if (componentRef.current) {
          const rect = componentRef.current.getBoundingClientRect();
          setPositions(positions => {
            positions[id] = { top: rect.top, left: rect.left };
            return positions
          })
        }
      }
      
      updateLocation()
      setTasks(oldArray => {
        const newArray = [...oldArray]
        newArray[id] = {
          ...tasks[id],
          visible: 'visible'
        }
        return newArray
      })

      const resizeObserver = new MutationObserver(() => {
        updateLocation();
      });
  
      if (componentRef.current) {
        resizeObserver.observe(componentRef.current, { attributes: true });
      }
  
      return () => {
        setTasks(oldArray => {
          const newArray = [...oldArray];
          newArray[id] = {
            ...tasks[id],
            visible: 'hidden'
          }
          return newArray;})
        resizeObserver.disconnect()
      }
    }, [setPositions, tasksFlag]);
    
    return (
        <button ref={componentRef} style={{opacity: 0}}>task handle</button>
    )
}

export default TaskHandle