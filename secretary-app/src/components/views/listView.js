import React, { useState, useEffect } from 'react'
import TaskHandle from '../taskHandle'

const ListView = ({ tasks, setTasks, tasksFlag }) => {
    const [size, setSize] = useState({ height: '75px', width: '500px' })
    const [visible, setVisible] = useState('visible')

    useEffect(() => {
        const updatedTasks = tasks.map(task => ({
            ...task,
            size: size,
            visible: visible
        }));
        console.log(updatedTasks)
        setTasks(updatedTasks)
    }, [size, visible, tasksFlag])

    return (
        <div>
            { tasks.map((task) => 
            <div style={{display: 'block',  marginLeft: 'auto', marginRight: 'auto', height: size.height, width: size.width}} key={task.id}>
                <TaskHandle id={task.id} tasks={tasks} setTasks={setTasks} tasksFlag={tasksFlag} />
            </div>) }
        </div>
    )
}

export default ListView