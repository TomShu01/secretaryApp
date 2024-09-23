import React from 'react';

const CustomButton = ({ name, icon, shape }) => {
  // Define styles based on the shape prop
  const getButtonStyle = () => {
    switch (shape) {
      case 'circle':
        return {
          borderRadius: '50%',
          padding: '10px 20px',
          display: 'inline-block',
          alignItems: 'center',
          justifyContent: 'center',
          width: '50px',
          height: '50px',
        };
      case 'rectangle':
        return {
          borderRadius: '5px',
          padding: '10px 20px',
          display: 'inline-block',
          alignItems: 'center'
        };
      case 'rounded-rectangle':
        return {
          borderRadius: '20px',
          padding: '10px 20px',
          display: 'inline-block',
          alignItems: 'center',
        };
      // Add more shapes as needed
      default:
        return {
          borderRadius: '5px',
          padding: '10px 20px',
          display: 'inline-block',
          alignItems: 'center',
        };
    }
  };

  return (
    <button style={getButtonStyle()}>
      {icon}
      {name && <span>{name}</span>}
    </button>
  );
};

// Example usage with a font-awesome icon
// You need to import the icon library in your project
/*
import { FaHome } from 'react-icons/fa';

<CustomButton 
  name="Home" 
  icon={<FaHome />} 
  shape="circle" 
/>
*/

export default CustomButton;
