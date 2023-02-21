package com.ibcemobile.smoxstyler.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.adapter.UpNextOptionAdapter
import com.ibcemobile.smoxstyler.data.UpNextOptionRepository
import com.ibcemobile.smoxstyler.databinding.ActivityUpNextOptionsBinding
import com.ibcemobile.smoxstyler.utils.AddUpNextOptionDialog
import com.ibcemobile.smoxstyler.viewmodels.UpNextOptionListViewModel
import com.ibcemobile.smoxstyler.viewmodels.UpNextOptionListViewModelFactory

class UpNextOptionsActivity : BaseActivity() {
    private lateinit var viewModel: UpNextOptionListViewModel
    private var binding: ActivityUpNextOptionsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpNextOptionsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        val factory = UpNextOptionListViewModelFactory(UpNextOptionRepository.getInstance())
        viewModel = ViewModelProvider(this, factory).get(UpNextOptionListViewModel::class.java)
        val adapter = UpNextOptionAdapter()
        adapter.setItemClickListener(object : UpNextOptionAdapter.ItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (view.id == R.id.btnDelete) {
                    showConfirmDeleteService(position)
                } else {
                    val option = viewModel.getOption(position)
                    option?.apply {
                        val returnIntent = Intent()
                        returnIntent.putExtra("status", option.title)
                        setResult(Activity.RESULT_OK, returnIntent)
                        finish()
                    }
                }
            }
        })
        binding!!.recyclerView.adapter = adapter
        viewModel.options.observe(this, Observer { options ->
            if (options != null) {
                adapter.submitList(options)
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.fetchList(this@UpNextOptionsActivity)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_product, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                openRoomDialog()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openRoomDialog() {
        val dialog = AddUpNextOptionDialog(this)

        dialog.show()
        dialog.confirmButton.setOnClickListener {
            val title = dialog.valueEditText.text.toString()
            dialog.dismiss()
            viewModel.addOption(this@UpNextOptionsActivity, title)
        }
    }

    private fun showConfirmDeleteService(position: Int) {
        val builder = AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
        builder.setMessage("Are you sure you want to delete the selected service?")
        builder.setPositiveButton("DELETE") { _, _ ->
            viewModel.deleteOption(this@UpNextOptionsActivity, position)
        }
        builder.setNegativeButton("CANCEL", null)
        builder.show()
    }
}