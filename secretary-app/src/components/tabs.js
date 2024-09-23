import React, { useState, useEffect, useContext } from 'react'
import { Context } from "../pages/main"
import {
  SearchForm,
  OptimizeForm,
  UrgentForm,
  ImportanceForm,
  EisenhowerForm,
} from './dimensions'

const functions = {
  Search: {
    Form: SearchForm,
    defaultParams: { searchString: '' },
  },
  Optimize: {
    Form: OptimizeForm,
    defaultParams: { startDate: '1900-01-01', endDate: '2099-01-01' },
  },
  Urgent: {
    Form: UrgentForm,
    defaultParams: {},
  },
  Importance: {
    Form: ImportanceForm,
    defaultParams: {},
  },
  Eisenhower: {
    Form: EisenhowerForm,
    defaultParams: { referenceDate: '1900-01-01', importanceThreshold: 5 },
  },
}

const Tabs = () => {
  const dimensions = [
    ['List', 'Block', 'Graph', 'Calendar', 'Custom'],
    ['Search'],
    ['Optimize', 'Urgent', 'Importance', 'Eisenhower'],
  ];
  // tabState now tracks selected tab for each dimension
  const [tabState, setTabState] = useState(dimensions.map(() => 0)) // Array with same length as dimensions, one for each dimension
  const [currentDimension, setCurrentDimension] = useState(0)
  const [selectedFunction, setSelectedFunction] = useState('')
  const [parametersList, setParametersList] = useState([{ functionName: 'Search', params: { searchString: '' } },
                                                        { functionName: 'Optimize', params: { startDate: '1900-01-01', endDate: '2099-01-01' } }])
  const [currentView, setCurrentView] = useContext(Context)

  const handleKeyDown = (event) => {
    const selectedTab = tabState[currentDimension]

    if (event.key === 'ArrowLeft') {
      setTabState((prev) => {
        const newTabs = [...prev]
        newTabs[currentDimension] = selectedTab > 0 ? selectedTab - 1 : selectedTab
        return newTabs
      })
    } else if (event.key === 'ArrowRight') {
      setTabState((prev) => {
        const newTabs = [...prev]
        newTabs[currentDimension] = selectedTab < dimensions[currentDimension].length - 1 ? selectedTab + 1 : selectedTab
        return newTabs
      })
    } else if (event.key === 'ArrowUp') {
      const newDimension = currentDimension > 0 ? currentDimension - 1 : currentDimension
      setCurrentDimension(newDimension);
    } else if (event.key === 'ArrowDown') {
      const newDimension = currentDimension < dimensions.length - 1 ? currentDimension + 1 : currentDimension
      setCurrentDimension(newDimension)
    }
  }

  useEffect(() => {
    window.onkeydown = handleKeyDown;

    setSelectedFunction(currentDimension > 0 ? dimensions[currentDimension][tabState[currentDimension]]: 'Search')
    setCurrentView([dimensions[0][tabState[0]], parametersList]);

    return () => {
      window.onkeydown = null;
    };
  }, [tabState, currentDimension, parametersList]);

  // dimension functions
  const handleFormSubmit = (params) => {
    setParametersList([
      ...parametersList,
      { functionName: selectedFunction, params },
    ]);
    setSelectedFunction('');
  };

  const SelectedForm = selectedFunction ? functions[selectedFunction].Form : null;

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      {SelectedForm && (
          <div style={{ marginBottom: '20px' }}>
          <SelectedForm onSubmit={handleFormSubmit} />
          </div>
      )}
      {/* Up Arrow */}
      <button
        onClick={() => {
          const newDimension = currentDimension > 0 ? currentDimension - 1 : currentDimension;
          setCurrentDimension(newDimension);
        }}
      >
        ↑
      </button>

      <div style={{ display: 'flex', alignItems: 'center' }}>
        {/* Left Arrow */}
        <button
          onClick={() => {
            const selectedTab = tabState[currentDimension];
            setTabState((prev) => {
              const newTabs = [...prev];
              newTabs[currentDimension] = selectedTab > 0 ? selectedTab - 1 : selectedTab;
              return newTabs;
            });
          }}
        >
          ←
        </button>

        {/* Tabs */}
        <div style={{ margin: '0 10px', display: 'flex', overflow: 'hidden', whiteSpace: 'nowrap' }}>
          {dimensions[currentDimension].map((view, index) => (
            <div
              key={index}
              style={{
                padding: '10px',
                cursor: 'pointer',
                backgroundColor: tabState[currentDimension] === index ? 'lightblue' : 'transparent',
                transform: `translateX(${-tabState[currentDimension] * 100}%)`,
                transition: 'transform 0.3s ease',
                width: '5vw',
              }}
            >
              {view}
            </div>
          ))}
        </div>

        {/* Right Arrow */}
        <button
          onClick={() => {
            const selectedTab = tabState[currentDimension];
            setTabState((prev) => {
              const newTabs = [...prev];
              newTabs[currentDimension] =
                selectedTab < dimensions[currentDimension].length - 1 ? selectedTab + 1 : selectedTab;
              return newTabs;
            });
          }}
        >
          →
        </button>
      </div>

      {/* Down Arrow */}
      <button
        onClick={() => {
          const newDimension = currentDimension < dimensions.length - 1 ? currentDimension + 1 : currentDimension;
          setCurrentDimension(newDimension);
        }}
      >
        ↓
      </button>
    </div>
  );
};

export default Tabs;
