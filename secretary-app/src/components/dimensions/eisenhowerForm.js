import React from 'react';

const EisenhowerForm = ({ onSubmit }) => {
  // Set default values for reference date and importance threshold
  const [referenceDate, setReferenceDate] = React.useState('2024-07-06');
  const [importanceThreshold, setImportanceThreshold] = React.useState(5);

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({ referenceDate, importanceThreshold });
  };

  return (
    <form onSubmit={handleSubmit}>
      <label>
        Reference Date:
        <input
          type="date"
          value={referenceDate}
          onChange={(e) => setReferenceDate(e.target.value)}
        />
      </label>
      <label>
        Importance Threshold:
        <input
          type="number"
          value={importanceThreshold}
          onChange={(e) => setImportanceThreshold(Number(e.target.value))}
          min="1"
          max="10"
        />
      </label>
      <button type="submit">Get Eisenhower Matrix</button>
    </form>
  );
};

export default EisenhowerForm;
