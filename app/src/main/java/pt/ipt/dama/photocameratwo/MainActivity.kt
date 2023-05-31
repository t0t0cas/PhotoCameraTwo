package pt.ipt.dama.photocameratwo

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import java.io.FileDescriptor
import java.io.IOException

class MainActivity : AppCompatActivity() {
    // auxiliary vars
    private lateinit var frame:ImageView
    private var imageUri: Uri?=null
    private val RESULT_LOAD_IMAGE=123
    private val IMAGE_CAPTURE_CODE=654

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        frame=findViewById(R.id.imageView)

        // check if you can use the camera and write the image to storage
        if(checkSelfPermission(Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED
        ){
            //we have a problem. One or both permission were not given
            val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            requestPermissions(permission, 112 )

        }else{
            //we are going to do something usefull
            frame?.setOnLongClickListener {
                // the user press for long time the imageView
                openCamera()
                true
            }
            frame?.setOnClickListener{
                //the user only touch the imageView
                openGallery()
            }
        }

    }

    /**
     * opens the galery to show photos
     */
    private fun openGallery() {
        var galleryIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE)
    }

    /**
     * Starts the camera to take the photo
     */
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "new picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image from the camera")
        imageUri=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        startActivityForResult(cameraIntent,IMAGE_CAPTURE_CODE)
    }
    //********************
    //auxiliary functions
    //********************

    /**
     * Reads a bitmap file
     */
    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try{
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri,"r")
            val fileDescriptor:FileDescriptor=parcelFileDescriptor!!.fileDescriptor
            val image=BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        }catch(e:IOException){
            e.printStackTrace()
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_CAPTURE_CODE && resultCode == Activity.RESULT_OK){
            val bitmap=uriToBitmap(imageUri!!)
            frame?.setImageBitmap(bitmap)
        }
        if(requestCode==RESULT_LOAD_IMAGE && resultCode== Activity.RESULT_OK && data!= null){
            imageUri=data.data
            val bitmap=uriToBitmap(imageUri!!)
            frame?.setImageBitmap(bitmap)
        }
    }



}