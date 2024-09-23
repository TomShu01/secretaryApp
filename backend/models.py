from pydantic import BaseModel, Field
from typing import Optional

# Pydantic model for validation
class Task(BaseModel):
    name: str
    id: int
    predecessor: int
    successor: int
    start: str
    to: str
    duration: int
    importance: int
    details: Optional[str] = None

class UpdateTaskModel(BaseModel):
    name: Optional[str]
    id: int
    predecessor: int
    successor: int
    start: str
    to: str
    duration: int
    importance: int
    details: Optional[str] = None

# Function to help format MongoDB output
def task_helper(task) -> dict:
    return {
        "_id": str(task["_id"]),
        "name": task["name"],
        "id": task["id"],
        "predecessor": task["predecessor"],
        "successor": task["successor"],
        "start": task["start"], 
        "to": task["to"], 
        "duration": task["duration"],
        "importance": task["importance"],
        "details": task.get("details", "")
    }
