package com.example.tm

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var spacesService: SpacesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api2.tastemade.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        spacesService = retrofit.create(SpacesService::class.java)

        spacesService.entries()
                .enqueue(object : Callback<Entries> {
                    override fun onResponse(call: Call<Entries>, response: Response<Entries>) {
                        val entries = response.body()

                        if (response.isSuccessful && entries != null) {
                            hideProgressBar()
                            showDetailViews()

                            setTitle(entries)
                            setRecipeImage(entries)
                            setRecipe(entries)
                        }
                    }

                    override fun onFailure(call: Call<Entries>, t: Throwable) {
                        throw t
                    }
                })

        ingredients.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        ingredients.setHasFixedSize(true)
    }

    private fun setTitle(entries: Entries) {
        recipeTitle.text = entries.items[0].fields.title
    }

    private fun setRecipeImage(entries: Entries) {
        val landscapeVideoAsset = entries getAssetFor entries.items[0].fields.landscapeAssetLink
        if (landscapeVideoAsset != null) {
            Picasso.get()
                    .load(landscapeVideoAsset.fields["stillImage"] as String)
                    .fit()
                    .centerCrop()
                    .into(videoThumbnail)
        }

        val portraitVideoAsset = entries getAssetFor entries.items[0].fields.portraitAssetLink
        if (portraitVideoAsset != null) {
            videoThumbnail.setOnClickListener { _ ->
                startActivity(VideoActivity
                        .create(this@MainActivity, Uri.parse(portraitVideoAsset.fields["wifiPlaylist"] as String)))
            }
        }
    }

    private fun setRecipe(entries: Entries) {
        val ingredientsText = entries.items[0].fields.recipeJson[0].ingredients
        ingredients.adapter = object : RecyclerView.Adapter<IngredientViewHolder>() {
            override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
                holder.bind(ingredientsText[position])
            }

            override fun getItemCount(): Int {
                return ingredientsText.size
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
                val view = TextView(this@MainActivity)
                val padding = resources.getDimensionPixelSize(R.dimen.padding_medium)
                view.setPadding(padding, padding, padding, padding)
                return IngredientViewHolder(view)
            }

        }
    }

    private fun showDetailViews() {
        videoThumbnail.visibility = View.VISIBLE
        playVideo.visibility = View.VISIBLE
        ingredients.visibility = View.VISIBLE
        ingredientsTitle.visibility = View.VISIBLE
        recipeTitle.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
        progressText.visibility = View.GONE
    }

    class IngredientViewHolder(private val view: TextView) : RecyclerView.ViewHolder(view) {
        fun bind(text: String) {
            view.text = text
        }
    }
}