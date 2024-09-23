class Search {
    static hints = [
      "Use this class to search tasks based on a keyword.",
      "It searches all attributes of each task.",
    ];
  
    static searchTasks(tasks, params) {
        const lowerSearchString = params.searchString.toLowerCase();
      return tasks.filter((task) =>
        Object.values(task).some((value) =>
          String(value).toLowerCase().includes(lowerSearchString)
        )
      );
    }
  }
  
  export default Search;
  