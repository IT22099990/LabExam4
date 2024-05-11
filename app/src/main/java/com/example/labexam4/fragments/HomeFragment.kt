package com.example.labexam4.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.labexam4.MainActivity
import com.example.labexam4.R
import com.example.labexam4.adapter.TaskAdapter
import com.example.labexam4.databinding.FragmentHomeBinding
import com.example.labexam4.model.Task
import com.example.labexam4.viewmodel.TaskViewModel

class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener, MenuProvider {

    private var homeBinding: FragmentHomeBinding? = null
    private val binding get() = homeBinding!!

    private lateinit var tasksViewModel: TaskViewModel
    private lateinit var tasksAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)  //initialize homeBinding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        tasksViewModel = (activity as MainActivity).taskViewModel
        setupHomeRecyclerView()

        binding.addTaskFab.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_addTaskFragment)
        }
    }

    private fun updateUI(task: List<Task>?){
        if (task != null){
            if (task.isNotEmpty()){
                binding.emptyTasksImage.visibility = View.GONE
                binding.homeRecyclerView.visibility = View.VISIBLE
            }
            else{
                binding.emptyTasksImage.visibility = View.VISIBLE
                binding.homeRecyclerView.visibility = View.GONE
            }
        }
    }

    private fun setupHomeRecyclerView(){
        tasksAdapter = TaskAdapter()
        binding.homeRecyclerView.apply{
            layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            setHasFixedSize(true)
            adapter = tasksAdapter
        }

        activity?.let {
            tasksViewModel.getAllTasks().observe(viewLifecycleOwner){ task ->
                tasksAdapter.differ.submitList(task)
                updateUI(task)  //update the UI according to task availability
            }
        }

    }

    private fun searchTask(query: String?){
        val searchQuery = "%$query"

        tasksViewModel.searchTask(searchQuery).observe(this){list ->
            tasksAdapter.differ.submitList(list)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null){
            searchTask(newText)
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        homeBinding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.home_menu, menu)

        val menuSearch = menu.findItem(R.id.searchMenu).actionView as SearchView
        menuSearch.isSubmitButtonEnabled = false
        menuSearch.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return false
    }


}