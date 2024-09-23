class Importance {
    static hints = [
      "Use this class to list tasks from most important to least important.",
      "Importance is based on the 'importance' attribute.",
    ];
  
    static getImportantTasks(tasks) {
      return tasks.slice().sort((a, b) => b.importance - a.importance);
    }
  }
  
  export default Importance;
  