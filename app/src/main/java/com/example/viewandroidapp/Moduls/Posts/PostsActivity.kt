package com.example.viewandroidapp.Moduls.Posts

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.viewandroidapp.Model.Model
import com.example.viewandroidapp.NavUtil
import com.example.viewandroidapp.R
import com.example.viewandroidapp.databinding.ActivityPostsBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient


class PostsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: PostViewModel
    private var model = Model.instance
    private lateinit var placesClient: PlacesClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PostViewModel::class.java]

        // Retrieve country name passed from HomeActivity and change the text
        val location = intent.getStringExtra("countryName")

        // Show the loading indicator before starting the data loading operation
        binding.loadingProgressBar.visibility = View.VISIBLE
        // Hide the CardView while waiting for data
        binding.cardView.visibility = View.GONE


        binding.countryNameTextView.text = location
        viewModel.posts = model.getAllPostsByLocation(location!!)
        Log.d("posts", "${viewModel.posts}")

        // Setup RecyclerView for posts
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        viewModel.posts?.observe(this) {
            recyclerView.adapter = PostAdapter(it)
        }

        // Initialize the SDK
        Places.initializeWithNewPlacesApiEnabled(applicationContext, com.example.viewandroidapp.BuildConfig.GOOGLE_MAPS_KEY)
        // Create a new PlacesClient instance
        placesClient = Places.createClient(this)

        // get the places from a country
        val placesList = mutableListOf<String>()
        val countriesAndPlaces = resources.getStringArray(R.array.places_by_country)
        for (item in countriesAndPlaces) {
            val countryAndPlace = item.split(",")
            if (countryAndPlace.size == 2 && countryAndPlace[0] == location) {
                placesList.add(countryAndPlace[1])
            }
        }
        if (placesList.isNotEmpty()) {
            placesApi(placesList)
        }
        else {
            displayNoReview()
        }

        // Setup navigation buttons
        val homeButton: ImageButton = findViewById(R.id.homeButton)
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        val createPostButton: ImageButton = findViewById(R.id.createPostButton)
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        NavUtil.setupActivityButtons(
            this,
            homeButton,
            searchButton,
            createPostButton,
            profileButton
        )
    }

    @SuppressLint("SetTextI18n")
    private fun displayNoReview() {
        binding.loadingProgressBar.visibility = View.GONE
        binding.cardContainer.visibility = View.GONE
        binding.tipText2.visibility = View.VISIBLE
        binding.tipText.text = "SORRY"
        binding.tipText.setTextColor(ContextCompat.getColor(this, R.color.logout))
    }

    private fun placesApi(placesList: MutableList<String>) {
        // Get the random place from the list
        val randomPlace = placesList.removeAt(0)

        // Start the autocomplete query.
        val autocompleteRequest = FindAutocompletePredictionsRequest.builder().setQuery(randomPlace).build()
        // get the place ID
        placesClient.findAutocompletePredictions(autocompleteRequest).addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
            val placeId = response.autocompletePredictions.firstOrNull()?.placeId
            Log.d("places", placeId + " : " + response.autocompletePredictions[0].getPrimaryText(null).toString())

            // Specify the fields to return.
            val placeFields = listOf(Place.Field.REVIEWS)
            // Construct a request object, passing the place ID and fields array.
            val request = placeId?.let { FetchPlaceRequest.newInstance(it, placeFields) }
            if (request != null) {
                placesClient.fetchPlace(request).addOnSuccessListener { response2: FetchPlaceResponse ->
                    val reviews = response2.place.reviews
                    if (reviews != null) {
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.cardView.visibility = View.VISIBLE
                        val placeReview = reviews[0]

                        // Get any attribution and author attribution.
                        val name = placeReview.authorAttribution.name + " at " + randomPlace
                        val photo = placeReview.authorAttribution.photoUri
                        val review = placeReview.originalText
                        val stars = placeReview.rating.toFloat()
                        val date = placeReview.publishTime?.subSequence(0,10)
                        Log.d("places", "photo: $photo, name: $name, review: $review, stars: $stars, date: $date")

                        if (photo != null) {
                            val photoUri = Uri.parse(photo)
                            Glide.with(this@PostsActivity) // Use this@PostsActivity as the context
                                .load(photoUri)
                                .placeholder(R.drawable.account_icon) // Placeholder image resource
                                .error(R.drawable.account_icon) // Error image resource if loading fails
                                .into(binding.profilePicture)
                        }
                        binding.name.text = name
                        binding.ratingBar.rating = stars
                        binding.timestamp.text = date
                        binding.textReview.text = review
                    }
                    else {
                        Log.d("places", "no reviews for that place")
                        if (placesList.isNotEmpty()) {
                            placesApi(placesList)
                        }
                        else {
                            displayNoReview()
                        }
                    }
                }.addOnFailureListener { exception: Exception? ->
                    Log.d("places", "error $exception")
                    if (placesList.isNotEmpty()) {
                        placesApi(placesList)
                    }
                    else {
                        displayNoReview()
                    }
                }
            }
        }.addOnFailureListener { exception: Exception? ->
            Log.e("places", "Place not found: ${exception?.message}")
            if (placesList.isNotEmpty()) {
                placesApi(placesList)
            }
            else {
                displayNoReview()
            }
        }
    }
}