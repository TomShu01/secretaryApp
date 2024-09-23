class Optimize {
    static hints = [
      "Use this class to find the optimal schedule of tasks.",
      "It considers task durations and predecessor/successor relationships.",
    ];
  
    static optimizeSchedule(tasks, { startDate, endDate }) {
      // Mapping from task id to task
      const taskMap = {};
      tasks.forEach((task) => {
        taskMap[task.id] = task;
      });
  
      // Build graph
      const graph = {};
      tasks.forEach((task) => {
        graph[task.id] = {
          task: task,
          successors: [],
        };
      });
  
      tasks.forEach((task) => {
        if (task.successor && task.successor !== 0) {
          graph[task.id].successors.push(task.successor);
        }
      });
  
      // Topological sort to detect cycles
      const visited = {};
      const stack = [];
      let hasCycle = false;
  
      function visit(nodeId, ancestors) {
        if (visited[nodeId]) return;
        if (ancestors.has(nodeId)) {
          hasCycle = true;
          return;
        }
        ancestors.add(nodeId);
        graph[nodeId].successors.forEach((successorId) => {
          visit(successorId, ancestors);
        });
        ancestors.delete(nodeId);
        visited[nodeId] = true;
        stack.push(nodeId);
      }
  
      Object.keys(graph).forEach((nodeId) => {
        visit(nodeId, new Set());
      });
  
      if (hasCycle) {
        return { error: 'Cycle detected in task dependencies.' };
      }
  
      // Calculate earliest start and finish times
      const startTimes = {};
      const finishTimes = {};
  
      const sDate = new Date(startDate);
      const eDate = new Date(endDate);
  
      while (stack.length) {
        const taskId = stack.pop();
        const task = taskMap[taskId];
  
        let earliestStart = sDate;
        if (task.predecessor && task.predecessor !== 0) {
          const predFinish = finishTimes[task.predecessor];
          if (predFinish > earliestStart) {
            earliestStart = predFinish;
          }
        }
  
        const taskStart = earliestStart;
        const taskFinish = new Date(taskStart.getTime() + task.duration * 60000);
  
        if (taskFinish > eDate) {
          return { error: 'Tasks cannot be scheduled within the given date constraints.' };
        }
  
        startTimes[taskId] = taskStart;
        finishTimes[taskId] = taskFinish;
      }
  
      // Build the schedule
      const schedule = tasks.map((task) => ({
        ...task,
        Start: startTimes[task.id],
        To: finishTimes[task.id],
      }));
  
      return schedule;
    }
  }
  
  export default Optimize;
  