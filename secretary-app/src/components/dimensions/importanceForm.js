import React from 'react';

const ImportanceForm = ({ onSubmit }) => {
  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({});
  };

  return (
    <form onSubmit={handleSubmit}>
      <p>No parameters needed.</p>
      <button type="submit">Get Important Tasks</button>
    </form>
  );
};

export default ImportanceForm;
