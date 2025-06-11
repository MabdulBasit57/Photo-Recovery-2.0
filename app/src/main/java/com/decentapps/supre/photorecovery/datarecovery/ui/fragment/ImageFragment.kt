package com.decentapps.supre.photorecovery.datarecovery.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.decentapps.supre.photorecovery.datarecovery.R
import com.decentapps.supre.photorecovery.datarecovery.ui.activity.ViewRecoveredImages
import com.decentapps.supre.photorecovery.datarecovery.ui.adapter.RecoveredFileAdapter
import com.decentapps.supre.photorecovery.datarecovery.utils.Utils
import java.io.File

class ImageFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecoveredFileAdapter
    private lateinit var emptyImg: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyImg = view.findViewById(R.id.emptyImage)
        val files = getAllFiles()
        adapter = RecoveredFileAdapter(requireContext(),files, object : RecoveredFileAdapter.OnItemClickListener {
            override fun onItemClick(file: File, position: Int) {
                // Handle image file click
                begin(position)
            }
        })
        recyclerView.adapter = adapter
        return view
    }

    private fun getAllFiles(): List<File> {
        val files = mutableListOf<File>()
        val f = File(Utils.getPathSave(requireContext(), "Recovered AB Photos"))

        val fileList = f.listFiles()

        if (fileList == null) {
            // Handle empty state
        } else {
            for (file in fileList) {
                if (file.name.endsWith(".png") || file.name.endsWith(".jpg") || file.name.endsWith(".jpeg") || file.name.endsWith(".gif")) {
                    files.add(file)
                }
            }
        }
        if(files.isNullOrEmpty()){
            emptyImg.visibility=View.VISIBLE
        }
        else{
            emptyImg.visibility=View.GONE
        }
        return files
    }

    private fun begin(position: Int) {
        val files = getAllFiles()
        val intent = Intent(requireContext(), ViewRecoveredImages::class.java).apply {
            putExtra("position", position)
            putExtra("files", files as ArrayList)
        }
        startActivity(intent)
    }
}