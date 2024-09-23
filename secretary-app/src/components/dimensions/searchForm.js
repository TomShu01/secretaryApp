import React from 'react';

const SearchForm = ({ onSubmit }) => {
  // Set default value for search string
  const [searchString, setSearchString] = React.useState('Type something to search...');

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit({ searchString });
  };

  return (
    <form onSubmit={handleSubmit}>
      <label>
        Search:
        <input
          type="text"
          value={searchString}
          onChange={(e) => setSearchString(e.target.value)}
        />
      </label>
      <button type="submit">Search</button>
    </form>
  );
};

export default SearchForm;
