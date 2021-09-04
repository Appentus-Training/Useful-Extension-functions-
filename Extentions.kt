

/**
 method of return the last location in sychronous fasion so that you don't need to use any callback in your code 
 @return the last location of the device **/
@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.awaitLastLocation() =
    suspendCoroutine<Location> { continuation ->
        lastLocation.addOnSuccessListener { location ->
            continuation.resume(location)
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }

/**
 method returns the drawble associated with the provided id
 this method just makes your code smaller , now in your activity you don't have to say
 ContextCompat.getDrawble(this,R.id.your_drawble) just say getAsDrawble(R.id.your_drawble)
**/
fun Context.getAsDrawable(id:Int) = ContextCompat.getDrawable(this,id)

/**
similar to above to use in frament without using requireActivity().getAsDrawble(Int) 
**/

fun Fragment.getAsDrawable(id:Int) = ContextCompat.getDrawable(this.requireActivity(),id)

fun Fragment.getAsColor(id:Int) = ContextCompat.getColor(this.requireActivity(),id)


/** funtions that returns a callback flow of network availablity and automatically removes the callback when the couroutine is canceled **/
@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Context.networkAvailableFlow(): Flow<Boolean> = callbackFlow {
    val callback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                offer(true)
            }

            override fun onLost(network: Network) {
                offer(false)
            }
        }
    val manager = getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
    manager.registerNetworkCallback(NetworkRequest.Builder().run {
        addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        build()
    }, callback)

    awaitClose { 
        manager.unregisterNetworkCallback(callback)
    }
}

/** extension funtions returning a flow of characters in an edittext also removes the callback when the courotine is canceled , 
call debounce(millis) to recieve callback after the millis in case you're hitting an api for each character **/

fun EditText.afterTextChangedFlow(): Flow<Editable?> 
    = callbackFlow {
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                offer(s)
            }
            override fun beforeTextChanged(s: CharSequence?, 
                               start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, 
                               start: Int, before: Int, count: Int) {}
        }
        addTextChangedListener(watcher)
        awaitClose { removeTextChangedListener(watcher) }
    }
    
    suspend fun Fragment.showGpsDialog(): Boolean {
    val locationSettingsRequest = LocationSettingsRequest
        .Builder()
        .setAlwaysShow(true)
        .addLocationRequest(
            LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        )
        .build()
    val settingsClient = LocationServices.getSettingsClient(requireActivity())
    var settingsResult: LocationSettingsResponse? = null
    val deferred = lifecycleScope.async {
        try {
            settingsResult =
                settingsClient.checkLocationSettings(locationSettingsRequest).awaitGpsState()
        } catch (e: ApiException) {
            val resolvableException = e as ResolvableApiException
            startIntentSenderForResult(
                resolvableException.resolution.intentSender,
                123,
                null,
                0,
                0,
                0,
                null
            )
        } catch (e: Exception) {
        }
    }
    deferred.await()
    if (settingsResult == null) {
        return false
    }
    return settingsResult!!.locationSettingsStates.isGpsUsable
}

suspend fun Task<LocationSettingsResponse>.awaitGpsState() =
    suspendCancellableCoroutine<LocationSettingsResponse> { continuation ->
        addOnSuccessListener {
            val result: LocationSettingsResponse
            try {
                result = getResult(ApiException::class.java)
                continuation.resume(result, null)
            } catch (e: ApiException) {
                continuation.resumeWithException(e)
            }
        }
        addOnFailureListener {
            continuation.resumeWithException(it)
        }
    }

// decode an string that is  in base 64
fun String.decode(): String {
    return Base64.decode(this, Base64.DEFAULT).toString(Charsets.UTF_8)
}

//encode an string  in base 64
fun String.encode(): String {
    return Base64.encodeToString(this.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
}

// easily check wheather or not location permisson allowed
fun Fragment.locationPermissionAllowed() = ActivityCompat.checkSelfPermission(
    requireActivity(),
    Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(
    requireActivity(),
    Manifest.permission.ACCESS_COARSE_LOCATION
) == PackageManager.PERMISSION_GRANTED

// get the address of a location
 fun Fragment.getAddressForLocation(location: Location): String {
    val geoCoder = Geocoder(requireActivity(),Locale.getDefault())
    val address = geoCoder.getFromLocation(location.latitude,location.longitude,1)
    return address[0].getAddressLine(0)
}
 
 //easily show a toast , never forget to call show()
 fun Fragment.showToast(msg: String = "Something went wrong , try again") {
    Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
}

 // easily show a toast
fun Fragment.showToast(@StringRes msg: Int) {
    Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
}

// hide keyboard
fun Fragment.hideSoftKeyboard() {
    val imm =
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}


// sigin in firebase using coroutine , avoid callback
suspend fun sigInWithPhoneAuthCredential(credential: PhoneAuthCredential) =
 suspendCancellableCoroutine<FirebaseUser?> { continuation ->
     FirebaseAuth.getInstance()
         .signInWithCredential(credential)
         .addOnCompleteListener {
             if(it.isSuccessful){
                 continuation.resume(it.result.user)
             }else{
                 continuation.resume(null)
             }
         }
 }



 // create user in firebase using co-routine , avoid callback
suspend fun Fragment.createUserWithPhone(phoneNumber:String):Boolean{

   return suspendCancellableCoroutine { continuation ->
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    continuation.resume(true)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    exception.printStackTrace()
                    continuation.resume(false)
                }



            }).build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}

//logout use in firebase using coroutine , avoid callback

suspend fun logoutUser():Boolean{
   return suspendCancellableCoroutine<Boolean> { continuation ->
        var resumed = false
        with(FirebaseAuth.getInstance()) {
            addAuthStateListener { firebaseAuth ->
                if(!resumed) {
                    if (firebaseAuth.currentUser == null) {
                        continuation.resume(true)
                        resumed = true
                    } else {
                        continuation.resume(false)
                    }
                }
            }
            signOut()
        }
    }

}


// extension property to make menu invisible
var Menu.visibility: Boolean
    get() = false
    set(value) {
        iterator().forEach {
            it.isVisible = value
        }
    }
    
 
 // display notification in kotlin way
 
 inline fun Context.notification(channelId: String, func: NotificationCompat.Builder.() -> Unit): Notification {
    val builder = NotificationCompat.Builder(this, channelId)
    builder.func()
    return builder.build()
}
 
 //esily check whether or not a service is running , isServiceRunning<MySerice>()
 fun <reified T> Context.isServiceRunning(): Boolean {
    val manager = activityManager
    return manager.getRunningServices(Integer.MAX_VALUE)
            .any { T::class.java.name == it.service.className }
}
 
 /** Set the View visibility to VISIBLE and eventually animate the View alpha till 100% */
fun View.visible(animate: Boolean = true) {
    if (animate) {
        animate().alpha(1f).setDuration(300).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationStart(animation)
                visibility = View.VISIBLE
            }
        })
    } else {
        visibility = View.VISIBLE
    }
}

//get the occurances of a substirng in a string
fun String.occurrencesOf(sub: String): Int {
    var count = 0
    var last = 0
    while (last != -1) {
        last = this.indexOf(sub, last)
        if (last != -1) {
            count++
            last += sub.length
        }
    }
    return count
}


//covert string to data using desired format
fun String.toDate(format: String): Date? {
  val dateFormatter = SimpleDateFormat(format, Locale.US)
  return try {
    dateFormatter.parse(this)
  } catch (e: ParseException) {
    null
  }
}

//extension property to get the screen size in pixels
@Suppress("DEPRECATION")
val Context.screenSize: Point
get() {
  val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
  val display = wm.defaultDisplay
  val size = Point()
  display.getSize(size)
  return size
}

//extension propery to get device model number
val Any.deviceName: String
get() {
  val manufacturer = Build.MANUFACTURER
  val model = Build.MODEL
  return if (model.startsWith(manufacturer))
    model.capitalize(Locale.getDefault())
  else
    manufacturer.capitalize(Locale.getDefault()) + " " + model
}

//esily get direction to a location
fun Context.directionsTo(location: Location) {
  val lat = location.latitude
  val lng = location.longitude
  val uri = String.format(Locale.US, "http://maps.google.com/maps?daddr=%f,%f", lat, lng)
  try {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
    startActivity(intent)
  }
  catch (e: ActivityNotFoundException) {
    e.printStackTrace()

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    startActivity(intent)
  }
}

//convert string to uri
val String.asUri: Uri?
get() = try {
  if (URLUtil.isValidUrl(this))
    Uri.parse(this)
  else
    null
} catch (e: Exception) {
  null
}

// Send location updates to the consumer
@ExperimentalCoroutinesApi
@SuppressLint("MissingPermission")
fun FusedLocationProviderClient.locationFlow() = callbackFlow<Location> {
    // A new Flow is created. This code executes in a coroutine!

    // 1. Create callback and add elements into the flow
    val callback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            result ?: return // Ignore null responses
            for (location in result.locations) {
                try {
                    trySend(location).isSuccess // Send location to the flow
                } catch (t: Throwable) {
                    // Location couldn't be sent to the flow
                }
            }
        }
    }

    // 2. Register the callback to get location updates by calling requestLocationUpdates
    requestLocationUpdates(
        LocationRequest.create(),
        callback,
        Looper.getMainLooper()
    ).addOnFailureListener { e ->
        close(e) // in case of error, close the Flow
    }

    // 3. Wait for the consumer to cancel the coroutine and unregister
    // the callback. This suspends the coroutine until the Flow is closed.
    awaitClose {
        // Clean up code goes here
        removeLocationUpdates(callback)
    }
}

fun List<LatLng>.toStaticMapApiFormat() =
    buildString {
        if (size >= 2) {
            for (i in 0..size - 2) {
                val latLng = this@toStaticMapApiFormat[i]
                append("${latLng.latitude},${latLng.longitude}|")
            }
            val latLng = this@toStaticMapApiFormat[size - 1]
            append("${latLng.latitude},${latLng.longitude}")
        }
    }
