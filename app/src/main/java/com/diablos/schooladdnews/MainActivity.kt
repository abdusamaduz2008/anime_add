package com.diablos.schooladdnews

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.diablos.schooladdnews.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private lateinit var binding: ActivityMainBinding
private lateinit var firebasefirestore : FirebaseFirestore
private lateinit  var storageRef: StorageReference
private var imageUri: Uri? = null
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.imageView.setImageResource(R.drawable.baseline_add_a_photo_24)
        dataClickEvents()
        initVars()
    }
    private fun dataClickEvents() {
        binding.upload.setOnClickListener {
            UploadImage()
        }

        binding.imageView.setOnClickListener {
            resultLauncher.launch("image/*")
        }
    }
    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){
        imageUri = it
        binding.imageView.setImageURI(it)
    }





    private fun initVars() {
        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        firebasefirestore = FirebaseFirestore.getInstance()
    }

    private fun UploadImage() {
        val textData = binding.editTextText.text.toString()
        val soatData = binding.editTextText2.text.toString()
        binding.progressBar.visibility = View.VISIBLE
        storageRef = storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let { it ->
            storageRef.putFile(it).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->


                        val map = HashMap<String, Any>()
                        map["imagesUri"] = uri.toString()
                        map["textUri"] = textData
                        map["soatUri"] = soatData

                        firebasefirestore.collection("images").add(map)
                            .addOnCompleteListener { firestoreTask ->
                                if (firestoreTask.isSuccessful) {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this,
                                        "Upload image",
                                        Toast.LENGTH_SHORT).show()
                                    binding.editTextText.text.clear()
                                    binding.editTextText2.text.clear()
                                } else {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(this, firestoreTask
                                        .exception?.message, Toast.LENGTH_SHORT).show()
                                }


                            }
                        binding.progressBar.visibility = View.GONE
                        binding.imageView.setImageResource(R.drawable.baseline_add_a_photo_24)


                    }


                } else {

                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.imageView.setImageResource(R.drawable.baseline_add_a_photo_24)
                }
            }


        }


    }


}