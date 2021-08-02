package com.modcom.bensalcie.modblog

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class PostActivity : AppCompatActivity() {
    private lateinit var etTitle :EditText
    private lateinit var etDEscription:EditText
    private lateinit var ivImage:ImageView
    private val GALLERY_REQUEST_CODE = 5647
    private var imageUri :Uri ?=null

    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference:StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        etTitle = findViewById(R.id.etTitle)
        etDEscription = findViewById(R.id.etDescription)
        ivImage = findViewById(R.id.ivIMage)
        databaseReference = FirebaseDatabase.getInstance("https://mod-blog-default-rtdb.firebaseio.com/").reference.child("MODCOMBLOG/BLOGS")
       storageReference = FirebaseStorage.getInstance().reference.child("MODCOM/IMAGES")
           .child("${System.currentTimeMillis()}"+".jpg")
        ivImage.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
            galleryIntent.setType("image/*")
            startActivityForResult(galleryIntent,GALLERY_REQUEST_CODE)
        }


    }

    fun postToFirebase(view: View) {
        val pd = ProgressDialog(this)
        pd.setTitle("Uploading Post")
        pd.setMessage("Please wait as we upload your post")
        pd.show()
        val title = etTitle.text.toString()
        val description = etDEscription.text.toString()
        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)){
            var imageurl = "default"
            if (imageUri!=null){
                //here the person selected an image from the galllery

                    val uploadTask:UploadTask  = storageReference.putFile(imageUri!!)
                uploadTask.continueWithTask{task->
                    if (task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }
                    storageReference.downloadUrl

                }.addOnCompleteListener{task->

                    if (task.isSuccessful){
                        imageurl = task.result.toString()
                        sendDataToFirebase(title,description,imageurl,pd)
                    }else{
                        Toast.makeText(this,
                            "Uploading image was unsuccessful :${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }

            }else{
                //here the image was not selected
                sendDataToFirebase(title,description,imageurl,pd)
            }


        }

    }

    private fun sendDataToFirebase(
        mytitle: String,
        description: String,
        imageurl: String,
        progessdialog: ProgressDialog
    ) {
        val hashmap = HashMap<String,Any>()
        hashmap["title"] = mytitle
        hashmap["description"] = description
        hashmap["image"]= imageurl
        hashmap["postedby"] = "lewis"
        hashmap["timestamp"] = System.currentTimeMillis().toString()
        val postkey = databaseReference.push().key.toString()
        databaseReference.child(postkey).updateChildren(hashmap).addOnCompleteListener { res->
            if (res.isSuccessful){
                progessdialog.dismiss()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
                Toast.makeText(this, "Blog POsted successfully", Toast.LENGTH_SHORT).show()
            }else{
                progessdialog.dismiss()
                Toast.makeText(this, "Error: ${res.exception?.message}", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            imageUri = data?.data
            ivImage.setImageURI(imageUri)
        }


    }
}