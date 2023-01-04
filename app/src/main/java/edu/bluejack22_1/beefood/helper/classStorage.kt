package edu.bluejack22_1.beefood.helper

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream

class classStorage {

    companion object{
        val storageRef = Firebase.storage.reference

        suspend fun uploadPhoto(imageView : ImageView) : Uri{
            imageView.isDrawingCacheEnabled = true
            imageView.buildDrawingCache()
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()


            var imageRef = storageRef.child("image.png")

            var uploadTask = imageRef.putBytes(data)
            uploadTask.addOnFailureListener {
                Log.d("upload file", "failed")
            }.addOnSuccessListener { taskSnapshot ->
                Log.d("upload file",  ClassUser.getCurrentUser()?.id.toString() + "success")
//                taskSnapshot.
            }
            return imageRef.downloadUrl.await()
        }



//        // Create a reference to "mountains.jpg"
//        val mountainsRef = storageRef.child("mountains.jpg")
//
//        // Create a reference to 'images/mountains.jpg'
//        val mountainImagesRef = storageRef.child("images/mountains.jpg")
//
//        // While the file names are the same, the references point to different files
//        mountainsRef.name == mountainImagesRef.name // true
//        mountainsRef.path == mountainImagesRef.path // false
    }

}