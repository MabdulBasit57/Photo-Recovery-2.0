package com.recover.photo.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.recover.photo.R
import com.recover.photo.ui.adapter.RecoveredAudioAdapter
import com.recover.photo.utils.Utils
import java.io.File

class AudioFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecoveredAudioAdapter
    private lateinit var emptyImg: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_audio, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        emptyImg = view.findViewById(R.id.emptyImage)

        val files = getAllFiles()
        adapter = RecoveredAudioAdapter(requireContext(),files, object : RecoveredAudioAdapter.OnItemClickListener {
            override fun onItemClick(file: File, position: Int) {
                // Handle audio file click
                begin(position)
            }
        })
        recyclerView.adapter = adapter
        return view
    }

    private fun getAllFiles(): List<File> {
        val files = mutableListOf<File>()
        val f = File(Utils.getPathSave(requireContext(), getString(R.string.restore_folder_path_audio)))

        val fileList = f.listFiles()

        if (fileList == null) {
            // Handle empty state
        } else {
            for (file in fileList) {
                if (file.name.endsWith(".mp3") || file.name.endsWith(".opus")) {
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
        val file = getAllFiles()[position]
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "audio/*")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
    }
}