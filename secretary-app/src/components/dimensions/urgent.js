class Urgent {
    static hints = [
      "Use this class to list tasks from earliest to latest start time.",
    ];
  
    static getUrgentTasks(tasks) {
      return tasks.slice().sort((a, b) => new Date(a.start) - new Date(b.start));
    }
  }
  
  export default Urgent;
  
