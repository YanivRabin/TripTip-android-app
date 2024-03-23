package com.example.viewandroidapp

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.viewandroidapp.databinding.ActivityPostsBinding
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.bumptech.glide.Glide



class PostsActivity : AppCompatActivity() {

    private lateinit var placesClient: PlacesClient
    private lateinit var binding: ActivityPostsBinding
    private lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve country name passed from HomeActivity and change the text
        val countryName = intent.getStringExtra("countryName")
        binding.countryNameTextView.text = countryName

        // Show the loading indicator before starting the data loading operation
        binding.loadingProgressBar.visibility = View.VISIBLE
        // Hide the CardView while waiting for data
        binding.cardView.visibility = View.GONE

        // Initialize the SDK
        Places.initializeWithNewPlacesApiEnabled(applicationContext, "AIzaSyBIKekkmwD4p8SmXo23bZtiMyn6KGtdu3o")
        // Create a new PlacesClient instance
        placesClient = Places.createClient(this)

        // get the places from a country
        val placesList = mutableListOf<String>()
        val countriesAndPlaces = resources.getStringArray(R.array.places_by_country)
        for (item in countriesAndPlaces) {
            val countryAndPlace = item.split(",")
            if (countryAndPlace.size == 2 && countryAndPlace[0] == countryName) {
                placesList.add(countryAndPlace[1])
            }
        }
        if (placesList.size == 2) {
            // Generate a random index within the range of placesList size
            val randomIndex = (0 until placesList.size).random()
            // Get the random place using the random index
            val randomPlace = placesList[randomIndex]

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
                        }
                    }.addOnFailureListener { exception: Exception? ->
                        binding.loadingProgressBar.visibility = View.GONE
                        Log.d("places", "error")
                        if (exception is ApiException) {
                            // Handle the error.
                            Log.d("places", "error $exception")
                        }
                    }
                }
            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    Log.e("places", "Place not found: ${exception.statusCode}")
                }
                displayNoReview()
            }
        } else {
            Log.d("places", "this country has no reviews")
            displayNoReview()
        }


        // Setup RecyclerView for posts
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = PostAdapter(generatePosts(countryName))

        // Setup navigation buttons
        val homeButton: ImageButton = findViewById(R.id.homeButton)
        val searchButton: ImageButton = findViewById(R.id.searchButton)
        val createPostButton: ImageButton = findViewById(R.id.createPostButton)
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        NavUtil.setupActivityButtons(this, homeButton, searchButton, createPostButton, profileButton)
    }

    // Generate dummy list of posts (replace with dataBase)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun generatePosts(country: String?): ArrayList<Post> {
        val posts = arrayListOf<Post>()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val currentDate = LocalDateTime.now().format(formatter)
        for (i in 1..5) {
            posts.add(Post(
                R.drawable.profile_picture,
                "Yaniv is at $country",
                currentDate,
                "This is my flight to $country",
                R.drawable.background
            ))
        }
        return posts
    }

    @SuppressLint("SetTextI18n")
    private fun displayNoReview() {
        binding.loadingProgressBar.visibility = View.GONE
        binding.cardContainer.visibility = View.GONE
        binding.tipText2.visibility = View.VISIBLE
        binding.tipText.text = "SORRY"
        binding.tipText.setTextColor(ContextCompat.getColor(this, R.color.logout))
    }
}