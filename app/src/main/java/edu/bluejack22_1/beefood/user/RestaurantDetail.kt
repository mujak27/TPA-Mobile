package edu.bluejack22_1.beefood.user

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.bluejack22_1.beefood.R
import edu.bluejack22_1.beefood.adapter.MenuItemAdapter
import edu.bluejack22_1.beefood.helper.classStorage
import edu.bluejack22_1.beefood.model.ClassCart
import edu.bluejack22_1.beefood.model.ClassMenu
import edu.bluejack22_1.beefood.model.ClassRestaurant
import edu.bluejack22_1.beefood.model.ClassUser
import kotlinx.coroutines.runBlocking

class RestaurantDetail : AppCompatActivity() {

    lateinit var layoutEdit : LinearLayout
    lateinit var editName : EditText
    lateinit var errEdit : TextView
    lateinit var imageView : ImageView
    lateinit var widgetEditButton : Button

    var isImageChanged = false
    lateinit var url : String

    lateinit var restaurantId : String
    private lateinit var menusRecycler : RecyclerView

    lateinit var restaurantTitle: TextView
    lateinit var checkoutButton : FloatingActionButton

    var carts : ArrayList<ClassCart> = ArrayList()

    fun onUpdate(){
        Log.d("update restaurant", "on update")
        restaurantTitle.setText(editName.getText().toString())
        var pictureLink = ClassUser.getCurrentUser()?.pictureLink!!
        if(isImageChanged){
            pictureLink = runBlocking { classStorage.uploadPhoto(imageView).toString() }
        }
        Log.d("update restaurant pic link", pictureLink)
        ClassRestaurant.updateRestaurant(restaurantId, editName.getText().toString(), pictureLink)
    }


    fun onSelectPhoto(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }


    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {

        override fun doInBackground(vararg urls: String): Bitmap? {
            Log.d("upload file download image", "do in bg")
            val imageURL = urls[0]
            var image: Bitmap? = null
            try {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
                Log.d("upload file display image", "success")
            }
            catch (e: Exception) {
                Log.e("upload file Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }


    fun onAddCart(id : String){
        var cartItem = carts.firstOrNull{it.menuId == id}
        if(cartItem == null){
            carts.add(ClassCart(id, 1))
        }else{
            cartItem.quantity++;
        }
        var check = carts.first{it.menuId == id}

    }

    fun onRemoveCart(id : String){
        var cartItem = carts.firstOrNull{it.menuId == id}
        if(cartItem == null) return;
        if(cartItem.quantity == 1){
            carts.remove(cartItem)
        }else{
            cartItem.quantity--;
        }
    }

    fun onCheckout(){
        if(carts.size == 0) return
        val intent = Intent(this, Checkout::class.java)
        intent.putExtra("restaurantId", restaurantId)
        intent.putExtra("carts", carts)
        this.startActivity(intent)
    }



    private var offset : Long = 0
    private var threshold : Long = 5
    private var isLoading = false
    private var isEnd = false
    var linearLayoutManager = LinearLayoutManager(this)
    var menus : ArrayList<ClassMenu> = arrayListOf()
    var currentMenuPosition : Int = 0
    lateinit var currentMenuImageView : ImageView

    var totalPrice = 0


    private fun loadMore() {
        if (!isLoading && !isEnd && linearLayoutManager.findLastCompletelyVisibleItemPosition() == menus.size-1) {
            isLoading = true
            var lastId = ""
            if(menus.size > 0) lastId = menus.get(menus.size-1).id
            var newMenus = runBlocking { ClassMenu.getMenusFromRestaurantIdWithOffset(restaurantId, threshold, offset, lastId) }
            if(newMenus.size > 0){
                if(newMenus.size < threshold){
                    isEnd = true;
                }
                var lastSize = menus.size - 1
                menus.addAll(newMenus)
                menusRecycler.adapter = MenuItemAdapter(restaurantId, menus, ::onAddCart, ::onRemoveCart, ::onSelectMenuPhoto)
                offset += threshold
                linearLayoutManager.scrollToPosition(lastSize-3)
            }
            isLoading = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)
        restaurantTitle = findViewById(R.id.restaurantTitle)

        restaurantId = intent.getStringExtra("restaurantId").toString()
        var restaurant = runBlocking { ClassRestaurant.getRestaurantById(restaurantId)}
        restaurantTitle.setText(restaurant.name)

        widgetEditButton = findViewById(R.id.edit_Restaurant_button)
        imageView = findViewById(R.id.restaurant_photo)
        layoutEdit = findViewById(R.id.edit_restaurant)
        editName = findViewById<EditText>(R.id.edit_restaurant_name)
        errEdit = findViewById<TextView>(R.id.error_edit_restaurant)

        checkoutButton = findViewById(R.id.buttonCheckout)
        checkoutButton.setOnClickListener{onCheckout()}

        menusRecycler = findViewById(R.id.recyclerViewMenus)
        menusRecycler.layoutManager = LinearLayoutManager(this)
        menusRecycler.setHasFixedSize(true)

        loadMore()
        menusRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                loadMore()
            }
        })


        url = restaurant.pictureLink.toString()

        if(url != null && url != "null" && url != ""){
            DownloadImageFromInternet(imageView).execute(url)
        }

        findViewById<Button>(R.id.button_select_photo).setOnClickListener {
            onSelectPhoto()
        }

        editName.setText(restaurantTitle.text)
        val role = ClassUser.getCurrentUser()?.role!!
        if(role=="Customer"){
            layoutEdit.visibility = View.GONE
        }
        widgetEditButton.setOnClickListener {
            if(editName.getText().toString() == ""){
                errEdit.setText("Fill the name to change")
            }else{
                onUpdate()

            }
        }



    }



    // MENU


    fun onSelectMenuPhoto(position: Int, menuId : String, imageView: ImageView){
        currentMenuPosition = position
        currentMenuImageView = imageView
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 102)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101){
            isImageChanged = true
            Log.d("upload file on activ result", "req code 101")
            var pic : Bitmap? = data?.getParcelableExtra<Bitmap>("data")
            Log.d("upload file bitmap", pic.toString())
            imageView.setImageBitmap(pic)
        }else if(requestCode == 102){
            var pic : Bitmap? = data?.getParcelableExtra<Bitmap>("data")
            currentMenuImageView.setImageBitmap((pic))
        }
    }
}
