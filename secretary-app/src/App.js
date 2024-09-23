import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'
import Main from './pages/main';

function App() {
  return (
    <div className="App">
      <Router>
        <Routes>
          <Route path="/" element={ <Main /> } exact />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
