package com.example.labexam4.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.labexam4.model.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)  //if the primary key already exist in the table old data will replace the new data
    suspend fun insertTask(task: Task)   //insert tasks in the database

    @Update
    suspend fun updateTask(task: Task)  //update

    @Delete
    suspend fun deleteTask(task: Task)   //delete

    @Query("SELECT * FROM TASKS ORDER BY id DESC")  //display all the tasks
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM TASKS WHERE taskTitle LIKE :query OR taskDate LIKE :query OR taskDesc LIKE :query")  //search a task
    fun searchTask(query: String?): LiveData<List<Task>>


}