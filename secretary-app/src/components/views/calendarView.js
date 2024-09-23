import React, { useState, useEffect } from 'react';
import { Calendar, momentLocalizer } from 'react-big-calendar'
import moment from 'moment'
import 'react-big-calendar/lib/css/react-big-calendar.css';
import TaskHandle from '../taskHandle'

const localizer = momentLocalizer(moment)

const CalendarView = ({ tasks, setTasks, tasksFlag }) => {
  const [size, setSize] = useState({ height: '50px', width: '100px' })
  const [visible, setVisible] = useState('hidden')

  const EventComponent = (eventWrapper) => { return ( <span> <TaskHandle id={eventWrapper.event.id} tasks={tasks} setTasks={setTasks} tasksFlag={tasksFlag}/> </span> ) }

  function constructEventsList(tasks) {
    const EventsList = [];

    tasks.forEach(task => {
      if (task.start) {
        console.log(task.start)
        EventsList.push({
          id: task.id,
          title: task.name, 
          start: task.start,
          end: task.to || task.start // If 'to' is not defined, set 'end' to 'from'
        });
      }
    });

    return EventsList;
  }

  useEffect(() => {
      const updatedTasks = tasks.map(task => ({
          ...task,
          size: size,
          visible: visible
      }));
      setTasks(updatedTasks)
  }, [size, visible, tasksFlag])

  const EventsList = constructEventsList(tasks)
  
  return (
      <div>
        <Calendar
          localizer={localizer}
          events={EventsList}
          startAccessor="start"
          endAccessor="end"
          style={{ height: 500 }}
          components={{
              event: EventComponent
            }}
        />
      </div>
  )
}

export default CalendarView