import React from 'react';

const OptimizeForm = ({ onSubmit }) => {
  // Set default values for start and end dates
  const [startDate, setStartDate] = React.useState('2024-07-05');
  const [endDate, setEndDate] = React.useState('2024-07-10');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({ startDate, endDate });
  };

  return (
    <form onSubmit={handleSubmit}>
      <label>
        Start Date:
        <input
          type="date"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
        />
      </label>
      <label>
        End Date:
        <input
          type="date"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
        />
      </label>
      <button type="submit">Optimize Schedule</button>
    </form>
  );
};

export default OptimizeForm;
