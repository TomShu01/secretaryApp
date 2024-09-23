import React, { useContext, useEffect, useState } from 'react';
import './styles.css';
import TaskPopup from './taskPopup'
import { TaskContext } from './view';
import axios from 'axios';
import Typography from '@mui/material/Typography';
import styles from './task.css'

const Task = ({ pos, taskData, tasks, setTasks }) => {
  const [position, setPosition] = useState({ top: 0, left: 0 });
  const [size, setSize] = useState(taskData.size)
  const [visible, setVisible] = useState(taskData.visible)
  const [expand, setExpand] = useState(false)
  const [positions, setPositions] = useContext(TaskContext);

  const handleClick = () => {
    setExpand(true)
  }
  
  const onClose = () => {
    setExpand(false)
  }
  
  // onAdd: adds task in databse when user adds task in form
  const onAdd = async (taskDetails) => {
    const maxId = tasks.reduce((max, task) => Math.max(max, task.id), 0);
    const newId = maxId + 1;
    taskDetails.id = newId;

    try {
      await axios.post('http://127.0.0.1:8000/api/tasks', taskDetails); // Adjust API endpoint accordingly
      } catch (error) {
      console.error('Error adding a task:', error);
    }

    setTasks(oldArray => {
      const newArray = [...oldArray, {...taskDetails}];
      return newArray;
    })

    setPositions(positions => {
      positions[newId] = {top: 0, left: 0};
      return positions
    })
  }
  
  // onDelete: deletes task in databse when user deletes task in form
  const onDelete = async (id) => {
    try {
      await axios.delete(`http://127.0.0.1:8000/api/tasks/${id}`);
      } catch (error) {
      console.error('Error deleting a task:', error);
    }

    setTasks(oldArray => {
      const newArray = oldArray.filter((task) => task.id != id)
      return newArray;
    })
  }
  
  // onUpdate: updates database when user updates task in form
  const onUpdate = async (taskDetails) => {
    try {
      await axios.put(`http://127.0.0.1:8000/api/tasks/${taskData.id}`, taskDetails);
      } catch (error) {
      console.error('Error deleting a task:', error);
    }

    setTasks(oldArray => {
      const newArray = [...oldArray];
      newArray[taskData.id] = taskDetails;
      return newArray;
    })
  }

  useEffect(() => {
    setPosition({ top: pos.top, left: pos.left });
    setSize(taskData.size)
    setVisible(taskData.visible)
  }, [pos, taskData]);

  if (expand == false) {
    return (<div className="smooth-transition-component"
              class='task-block'
              style={{
                borderStyle: 'solid',
                borderRadius: '5px',
                position: 'absolute',
                top: 0,
                left: 0,
                width: size.width,
                height: size.height,
                visibility: visible,
                display: 'inline-block',
                transform: `translate(${position.left}px, ${position.top}px)`,
                overflow: 'hidden'
              }}
              onClick={handleClick}>
                <Typography variant="h6" style={{ color: '#000000' }}>
                  {taskData.name}
                </Typography>
                <Typography variant="body2" style={{ color: '#000000' }}>
                  {taskData.details}
                </Typography>
            </div>)
  } else {
    return(<TaskPopup task={taskData} onClose={onClose} onAdd={onAdd} onDelete={onDelete} onUpdate={onUpdate} />)
  }
};

export default Task;