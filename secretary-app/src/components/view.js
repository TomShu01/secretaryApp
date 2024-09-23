import React, { useContext, useState, useRef, useEffect, useMemo } from 'react'
import { ListView, BlockView, GraphView, CalendarView } from './views'
import Task from './task'
import { Context } from '../pages/main'
import {
    Search,
    Optimize,
    Urgent,
    Importance,
    Eisenhower,
    SearchForm,
    OptimizeForm,
    UrgentForm,
    ImportanceForm,
    EisenhowerForm,
  } from './dimensions'
import axios from 'axios'

// Specifies different View Components to be added in the UI
const viewTypes = {
    default: ListView,
    List: ListView,
    Block: BlockView,
    Graph: GraphView,
    Calendar: CalendarView
};

// Stores info about each dimension
const functions = {
    Search: {
      execute: Search.searchTasks,
      Form: SearchForm,
      hints: Search.hints,
      defaultParams: { searchString: '' },
    },
    Optimize: {
      execute: Optimize.optimizeSchedule,
      Form: OptimizeForm,
      hints: Optimize.hints,
      defaultParams: { startDate: '1900-01-01', endDate: '2099-01-01' },
    },
    Urgent: {
      execute: Urgent.getUrgentTasks,
      Form: UrgentForm,
      hints: Urgent.hints,
      defaultParams: {},
    },
    Importance: {
      execute: Importance.getImportantTasks,
      Form: ImportanceForm,
      hints: Importance.hints,
      defaultParams: {},
    },
    Eisenhower: {
      execute: Eisenhower.getEisenhowerMatrix,
      Form: EisenhowerForm,
      hints: Eisenhower.hints,
      defaultParams: { referenceDate: '1900-01-01', importanceThreshold: 5 },
    },
  };

export const TaskContext = React.createContext()

const View = () => {
    const [currentView] = useContext(Context);
    const [tasks, setTasks] = useState([]);
    const [positions, setPositions] = useState([]);
    const [tasksFlag, setTasksFlag] = useState(false);

    const ViewType = currentView[0] in viewTypes ? viewTypes[currentView[0]] : viewTypes["default"]

    useEffect(() => {
        fetchTasks(currentView) 
    }, [currentView])

    useEffect(() => {
        function handleResize() {
            setTasksFlag((flag) => !flag)
        }
    
        window.addEventListener('resize', handleResize)
    }, [setTasksFlag])

    // handleApplyFunctions: Applies all functions to tasks, each function
    //   acting independent of each other
    const handleApplyFunctions = (data, paramList) => {
        console.log(paramList)
        for (const item of paramList) {
            const funcObj = functions[item.functionName]
            let funcResult

            try {
                funcResult = funcObj.execute(data, item.params)
            } catch (error) {
                alert(`Error in ${item.functionName}: ${error.message}`)
            return
            }

            if (funcResult.error) {
                alert(`${item.functionName} Error: ${funcResult.error}`)
            return
            }

            data = funcResult
        }
        return(data)
    };

    // syncDictionaries: Remove keys from dict1 that are not in dict2. Add 
    //   keys from dict2 that are not in dict1
    function syncDictionaries(dict1, dict2) {
        for (let key in dict1) {
            if (!(key in dict2)) {
                delete dict1[key];
            }
        }

        for (let key in dict2) {
            if (!(key in dict1)) {
                dict1[key] = dict2[key];
            }
        }
    
        return dict1;
    }

    // fetchTasks: fetches tasks from the database, then applies functions to the tasks
    const fetchTasks = async (currentView) => {
        console.log(currentView)
        console.log(currentView[1])
        try {
            const response = await axios.get('http://127.0.0.1:8000/api/tasks');
            const initial_tasks = response.data.map(task => ({
                ...task,
                size: { height: '25px', width: '40px'},
                visible: 'visible'
            }))

            const transformedTasks = handleApplyFunctions(initial_tasks, currentView[1])
            const newPositions = Object.assign({}, ...transformedTasks.map((task) => ({[task.id]: { id: task.id, top: 0, left: 0 }})))
            setPositions(syncDictionaries(positions, newPositions))
            setTasks(transformedTasks)
            setTasksFlag((flag) => !flag)
        } catch (error) {
            console.error('Error fetching tasks:', error);
        }
    };

    return (
        <div style={{ width: "100%", height: "60vh" }}>
            <TaskContext.Provider value={[positions, setPositions]}>
                <ViewType tasks={tasks} setTasks={setTasks} tasksFlag={tasksFlag} />
                {tasks.map((task, index) => (
                    <div key={index} >
                        <Task pos={positions[task.id]} taskData={task} tasks={tasks} setTasks={setTasks} />
                    </div>
                ))}
            </TaskContext.Provider>
        </div>
    );
};
export default View;