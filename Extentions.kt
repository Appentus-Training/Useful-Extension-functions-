

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

// recieve in
fun String.decode(): String {
    return Base64.decode(this, Base64.DEFAULT).toString(Charsets.UTF_8)
}

//send in --
fun String.encode(): String {
    return Base64.encodeToString(this.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
}

fun Fragment.locationPermissionAllowed() = ActivityCompat.checkSelfPermission(
    requireActivity(),
    Manifest.permission.ACCESS_FINE_LOCATION
) == PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(
    requireActivity(),
    Manifest.permission.ACCESS_COARSE_LOCATION
) == PackageManager.PERMISSION_GRANTED

 fun Fragment.getAddressForLocation(location: Location): String {
    val geoCoder = Geocoder(requireActivity(),Locale.getDefault())
    val address = geoCoder.getFromLocation(location.latitude,location.longitude,1)
    return address[0].getAddressLine(0)
}
 
 fun Fragment.showToast(msg: String = "Something went wrong , try again") {
    Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(@StringRes msg: Int) {
    Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
}

// hide keyboard
fun Fragment.hideSoftKeyboard() {
    val imm =
        requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view?.windowToken, 0)
}


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



var Menu.visibility: Boolean
    get() = false
    set(value) {
        iterator().forEach {
            it.isVisible = value
        }
    }
    
    @ExperimentalCoroutinesApi
fun Context.networkAvailableFlow(): Flow<Boolean> = callbackFlow {
    val callback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                this@callbackFlow.trySend(true).isSuccess
            }

            override fun onLost(network: Network) {
                this@callbackFlow.trySend(false).isSuccess
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
