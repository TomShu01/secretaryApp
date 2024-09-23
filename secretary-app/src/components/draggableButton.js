import React, { useContext, useState, forwardRef, useRef, useEffect } from 'react';
import { TaskContext } from './view';

const DraggableButton = ({ index }) => {
  const [positions, setPositions] = useContext(TaskContext);
  const [position, setPosition] = useState({ x: 400, y: 400 });
  const [dragging, setDragging] = useState(false);
  const [offset, setOffset] = useState({ x: 0, y: 0 });
  const componentRef = useRef(null);

  const onMouseDown = (e) => {
    setDragging(true);
    setOffset({
      x: e.clientX - position.x,
      y: e.clientY - position.y,
    });
  };

  const onMouseMove = (e) => {
    if (dragging) {
      setPosition({
        x: e.clientX - offset.x,
        y: e.clientY - offset.y,
      });
    }
  };

  const onMouseUp = () => {
    setDragging(false);
  };

  useEffect(() => {
    const updateLocation = () => {
    if (componentRef.current) {
    const rect = componentRef.current.getBoundingClientRect();
    setPositions(oldArray => {
      const newArray = [...oldArray];
      newArray[index] = { top: rect.top, left: rect.left };
      return newArray;
    });
    }
    };
   
    updateLocation();

    const resizeObserver = new MutationObserver(() => {
      updateLocation();
    });

    if (componentRef.current) {
      resizeObserver.observe(componentRef.current, { attributes: true });
    }

    return () => {
      resizeObserver.disconnect()
    }
   
    // window.addEventListener('resize', updateLocation);
    // return () => {
    // window.removeEventListener('resize', updateLocation);
    // };
  }, [setPositions]);

  return (
    <div
      ref={componentRef}
      style={{
        position: 'absolute',
        left: `${position.x}px`,
        top: `${position.y}px`,
        cursor: dragging ? 'grabbing' : 'grab',
        padding: '10px',
      }}
      onMouseDown={onMouseDown}
      onMouseMove={onMouseMove}
      onMouseUp={onMouseUp}
      onMouseLeave={onMouseUp} // Stop dragging if the mouse leaves the element
    >
      <button style={{ padding: '10px 20px', cursor: 'inherit' }}>Drag Me</button>
    </div>
  );
};

export default DraggableButton;