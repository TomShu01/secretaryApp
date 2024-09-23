from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from models import Task, UpdateTaskModel, task_helper
from config import task_collection
from bson import ObjectId
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

origins = ["*"]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Create task
@app.post("/api/tasks", response_description="Add new task", response_model=Task)
async def create_task(task: Task):
    task_data = task.dict()
    new_task = await task_collection.insert_one(task_data)
    created_task = await task_collection.find_one({"_id": new_task.inserted_id})
    return task_helper(created_task)

# Get all tasks
@app.get("/api/tasks", response_description="List all tasks")
async def get_tasks():
    tasks = []
    async for task in task_collection.find():
        tasks.append(task_helper(task))
    return tasks

# Get a single task
@app.get("/api/tasks/{id}", response_description="Get a single task", response_model=Task)
async def get_task(id: str):
    task = await task_collection.find_one({"_id": ObjectId(id)})
    if task is None:
        raise HTTPException(status_code=404, detail="Task not found")
    return task_helper(task)

# Update task
@app.put("/api/tasks/{id}", response_description="Update a task", response_model=Task)
async def update_task(id: str, task: UpdateTaskModel):
    task_data = {k: v for k, v in task.dict().items() if v is not None}
    print(task_data)

    if len(task_data) >= 1:
        update_result = await task_collection.update_one({"id": int(id)}, {"$set": task_data})

        if update_result.modified_count == 1:
            updated_task = await task_collection.find_one({"id": int(id) })
            if updated_task:
                return task_helper(updated_task)

    existing_task = await task_collection.find_one({"id": int(id) })
    if existing_task is None:
        raise HTTPException(status_code=404, detail="Task not found")
    return task_helper(existing_task)

# Delete task
@app.delete("/api/tasks/{id}", response_description="Delete a task")
async def delete_task(id: str):
    delete_result = await task_collection.delete_one({"id": int(id)})

    if delete_result.deleted_count == 1:
        return {"message": "Task deleted"}
    else:
        raise HTTPException(status_code=404, detail="Task not found")
