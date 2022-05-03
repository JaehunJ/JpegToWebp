package com.example.jpegtowebp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.jpegtowebp.databinding.FragmentMainBinding
import com.google.android.material.snackbar.Snackbar

class MainFragment : Fragment() {

    lateinit var binding: FragmentMainBinding
    val viewModel: FragmentMainViewModel by viewModels()

    private lateinit var adapter  :ImageRecyclerViewAdapter

    private val permissions =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

    private val selectedPictureActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        selectedImage(result)
    }

    private val requestPermissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        if(it.all { per-> per.value }){
            showGalley()
        }else{
            Snackbar.make(
                binding.root, "권한이 설정되지 않음.",
                Snackbar.LENGTH_LONG
            )
                .setAction("Action", null).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.fab.setOnClickListener {
            if (checkPermission(requireContext(), *permissions)) {
                showGalley()
            } else {
                requestPermissionResultLauncher.launch(permissions)
            }
        }

        adapter = ImageRecyclerViewAdapter(requireContext()){
            delete(it)
        }

        binding.rvPhoto.adapter = adapter

        binding.slideRatio.addOnChangeListener { slider, value, fromUser ->
            viewModel.setRatio(value)
        }

        initBinding()
    }

    private fun initBinding(){
        viewModel.ratio.observe(viewLifecycleOwner){
            it?.let{
                binding.tvRatio.text = (it*100f).toInt().toString()
            }

        }

        viewModel.uriList.observe(viewLifecycleOwner){
            it?.let{list->
                binding.btnOk.isEnabled = list.isNotEmpty()
            }
        }
    }

    fun delete(pos:Int){
        viewModel.removeItem(pos)
    }

    fun showGalley() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = MediaStore.Images.Media.CONTENT_TYPE
            data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            putExtra(Intent.EXTRA_MIME_TYPES, "image/jpeg")
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        selectedPictureActivityResultLauncher.launch(intent)
    }

    fun selectedImage(res: ActivityResult) {
        if (res.resultCode == Activity.RESULT_OK) {
            val imageData = res.data

            imageData?.let {
                val d = it.data
                val clip = it.clipData

                //single
                if(d != null){
                    adapter.addData(d)
                    viewModel.addList(d)
                }else if(clip != null){ //multiple
                    val list = mutableListOf<Uri>()
                    val cnt = clip.itemCount
                    for(i in 0 until cnt){
                        list.add(clip.getItemAt(i).uri)
                    }
                    adapter.addData(list)
                    viewModel.addList(list)
                }
            }
        }
    }

    fun checkPermission(context: Context, vararg permssions: String) = permssions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}