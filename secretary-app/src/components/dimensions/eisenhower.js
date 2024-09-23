class Eisenhower {
    static hints = [
      "Use this class to categorize tasks based on urgency and importance.",
      "Provide a date to compare urgency and a threshold for importance.",
    ];
  
    static getEisenhowerMatrix(tasks, { referenceDate, importanceThreshold }) {
      const refDate = new Date(referenceDate);
  
      /* const matrix = {
        urgentImportant: [],
        notUrgentImportant: [],
        urgentNotImportant: [],
        notUrgentNotImportant: [],
      }; */

      const orderedTasks = []
  
      tasks.map((task) => {
        const taskStart = new Date(task.start);
        const isUrgent = taskStart <= refDate;
        const isImportant = task.importance >= importanceThreshold;
  
        if (isUrgent && isImportant) {
          orderedTasks.push(task);
        } else if (!isUrgent && isImportant) {
          orderedTasks.push(task);
        } else if (isUrgent && !isImportant) {
          orderedTasks.push(task);
        } else {
          orderedTasks.push(task);
        }
      });
  
      return orderedTasks;
    }
  }
  
  export default Eisenhower;
  
