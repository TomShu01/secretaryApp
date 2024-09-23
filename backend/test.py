from pymongo import MongoClient

client = MongoClient('mongodb://localhost:27017/')
db = client['tasks_db']
collection = db['tasks_collection']
documents = collection.find()
response = collection.delete_many({})

for document in documents:
    print(document)