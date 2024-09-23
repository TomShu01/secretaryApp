import React, { useState } from 'react';
import './TaskPopup.css'; // Import CSS for styling and animation

// Popup component
const TaskPopup = ({ task, onClose, onAdd, onDelete, onUpdate }) => {
  const [taskDetails, setTaskDetails] = useState({ ...task });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setTaskDetails({
      ...taskDetails,
      [name]: value,
    });
  };

  return (
    <div className="popup-overlay">
      <div className="popup-content">
        <h2>Edit Task</h2>
        <label>
          Name:
          <input
            type="text"
            name="name"
            value={taskDetails.name}
            onChange={handleChange}
          />
        </label>
        <br/>
        <label>
          Start:
          <input
            type="date"
            name="start"
            value={taskDetails.start}
            onChange={handleChange}
          />
        </label>
        <br/>
        <label>
          To:
          <input
            type="date"
            name="to"
            value={taskDetails.to}
            onChange={handleChange}
          />
        </label>
        <br/>
        <label>
          predecessor:
          <input
            type="text"
            name="predecessor"
            value={taskDetails.predecessor}
            onChange={handleChange}
          />
        </label>
        <br/>
        <label>
          successor:
          <input
            type="text"
            name="successor"
            value={taskDetails.successor}
            onChange={handleChange}
          />
        </label>
        <br/>
        <label>
          duration:
          <input
            type="int"
            name="duration"
            value={taskDetails.duration}
            onChange={handleChange}
          />
        </label>
        <br/>
        <label>
          importance:
          <input
            type="int"
            name="successor"
            value={taskDetails.importance}
            onChange={handleChange}
          />
        </label>
        <br/>
        <label>
          details:
          <input
            type="text"
            name="details"
            value={taskDetails.details}
            onChange={handleChange}
          />
        </label>
        <div className="popup-buttons">
          <button onClick={onClose}>Return</button>
          <button onClick={() => onAdd(taskDetails)}>Add</button>
          <button onClick={() => onDelete(taskDetails.id)}>Delete</button>
          <button onClick={() => onUpdate(taskDetails)}>Update</button>
        </div>
      </div>
    </div>
  );
};

export default TaskPopup