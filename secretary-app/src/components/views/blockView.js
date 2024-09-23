import TaskHandle from '../taskHandle'
import React, { useState, useEffect } from 'react';

const BlockView = ({ tasks, setTasks, tasksFlag }) => {
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
        <div style={{display: 'inline-block'}}>
            { tasks.map((task) => <div style={{height: size.height, width: size.width, display: 'inline-block'}}><TaskHandle id={task.id} tasks={tasks} setTasks={setTasks} key={task.id} tasksFlag={tasksFlag} /></div>) }
        </div>
    )
}

export default BlockView