package com.example.mvvmlab

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
class TaskViewModelTest {

    // A fake repository gives us control over the starting data
    private class FakeRepository : TaskRepository() {
        override fun loadInitialTasks(): List<Task> = listOf(
            Task(1, "First"),
            Task(2, "Second", done = true)
        )
    }

    @Test
    fun toggleDone_flipsTheTaskState() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.toggleDone(1)
        assertTrue(viewModel.tasks.value.first { it.id == 1 }.done)
    }

    @Test
    fun toggleDone_twice_returnsToOriginalState() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.toggleDone(2)
        viewModel.toggleDone(2)
        assertTrue(viewModel.tasks.value.first { it.id == 2 }.done)
    }

    @Test
    fun addTask_appendsWithNextId() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.addTask("Third")
        assertEquals(3, viewModel.tasks.value.size)
        assertEquals(3, viewModel.tasks.value.last().id)
        assertEquals("Third", viewModel.tasks.value.last().title)
    }

    @Test
    fun addTask_ignoresBlankTitle() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.addTask(" ")
        assertEquals(2, viewModel.tasks.value.size)
    }

    // A. Delete
    @Test
    fun deleteTask() { // ubah tanpa parameter
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.deleteTask(1)
        assertEquals(1, viewModel.tasks.value.size)
        assertTrue(viewModel.tasks.value.none { it.id == 1 })
    }

    //Jika ada ID anomali
    @Test
    fun deleteTask_unknownId_changesNothing() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.deleteTask(99)
        assertEquals(2, viewModel.tasks.value.size)
    }

    // B. Filter
    @Test
    fun filterOff_showsAllTasks() {
        val viewModel = TaskViewModel(FakeRepository())
        assertEquals(2, viewModel.visibleTasks.size)
    }

    @Test
    fun filterOn_showsOnlyUnfinishedTasks() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.setShowOnlyOpen(true)
        assertEquals(listOf(1), viewModel.visibleTasks.map { it.id })
    }

    @Test
    fun filterOn_toggleDone_updatesVisibleTasks() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.setShowOnlyOpen(true)
        viewModel.toggleDone(1)
        assertTrue(viewModel.visibleTasks.isEmpty())
    }

    @Test
    fun filterOn_addTask_newTaskIsVisible() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.setShowOnlyOpen(true)
        viewModel.addTask("Third")
        assertEquals(listOf(1, 3), viewModel.visibleTasks.map { it.id })
    }

    @Test
    fun filterOn_doesNotRemoveDataFromTasks() {
        val viewModel = TaskViewModel(FakeRepository())
        viewModel.setShowOnlyOpen(true)
        assertEquals(2, viewModel.tasks.value.size)
    }
}