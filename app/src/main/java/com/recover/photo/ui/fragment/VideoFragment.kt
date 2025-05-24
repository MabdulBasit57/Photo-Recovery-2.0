package com.recover.photo.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.recover.photo.R
import com.recover.photo.ui.adapter.RecoveredFileAdapter
import com.recover.photo.utils.Utils
import java.io.File

class VideoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyImg: ImageView
    private lateinit var adapter: RecoveredFileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyImg = view.findViewById(R.id.emptyImage)

        val files = getAllFiles()
        adapter = RecoveredFileAdapter(requireContext(),files, object : RecoveredFileAdapter.OnItemClickListener {
            override fun onItemClick(file: File, position: Int) {
                // Handle video file click
                begin(file)
            }
        })
        recyclerView.adapter = adapter
        return view
    }

    private fun getAllFiles(): List<File> {
        val files = mutableListOf<File>()
        val f = File(Utils.getPathSave(requireContext(), getString(R.string.restore_folder_path_video)))

        val fileList = f.listFiles()

        if (fileList == null) {
            // Handle empty state
        } else {
            for (file in fileList) {
                if (file.name.endsWith(".mp4") || file.name.endsWith(".mkv")) {
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

    private fun begin(file:File) {
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "video/*")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
    }
}