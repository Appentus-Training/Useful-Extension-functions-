

/*
  finds and colors all urls contained in a string 
  @param linkColor color for the url default is blue
  @param linkClickAction action to perform when user click that link 
 */
fun String.linkify(linkColor:Int = Color.BLUE,linkClickAction:((String) -> Unit)? = null): SpannableStringBuilder {
    val builder = SpannableStringBuilder(this)
    val matcher = Patterns.WEB_URL.matcher(this)
    while(matcher.find()){
        val start = matcher.start()
        val end = matcher.end()
        builder.setSpan(ForegroundColorSpan(Color.BLUE),start,end,0)
        val onClick = object : ClickableSpan(){
            override fun onClick(p0: View) {
                    linkClickAction?.invoke(matcher.group())
            }
        }
        //builder.setSpan(onClick,start,end,0)
    }
    return builder
}
/*
 Merges multiple audio file, duration of output file is equal to shortest audio file
 @Param outputFilePath path of the output file , need to point to files containing raw pcm data example (.wav)
 @Param audioPaths , a list of audio files that needs to be merged should be mp3 file
 @returns merged file if merging was successful null otherwise
 requires dependency implementation 'com.arthenica:ffmpeg-kit-full:4.5.LTS'
 
 */
@ExperimentalCoroutinesApi
suspend fun mergeAudio(outputFilePath :String,audioPaths:List<String>) =
    suspendCancellableCoroutine<File?> {
        val mergeCommand = buildString {
            audioPaths.forEach { audioPath ->
                append("-i")
                append(" ")
                append(audioPath)
            }
            append(" -filter_complex amix=inputs=2:duration=first:dropout_transition=3 ")
            append(outputFilePath)
        }
        FFmpegKit.executeAsync(mergeCommand) { session ->
            if(session.returnCode.isSuccess){
                it.resume(null){}
            }else {
                it.resume(File(outputFilePath)) {}
            }
        }
    }
    
    
/*
 put audio effect on wav audio file
 @param outputFilePath path of the output file
 @param audioPaths , a list of audio files that needs to be merged should be mp3 file,need to point to files containing raw pcm data example (.wav)
 @param effects , a list of effect that need to be applied for example listOf("afade=t=in:ss=0:d=10","aecho=0.8:0.88:6:0.4") to appy echo and fade in effect to the final audio file
 @returns merged file if merging was successful null otherwise
 requires dependency implementation 'com.arthenica:ffmpeg-kit-full:4.5.LTS'
 */
suspend fun putEffects(inputFilePath:String,outputFilePath:String,effects:List<String>) =
    suspendCancellableCoroutine<File?> {
        val audioAfterEffectCommand = buildString {
            append(" -y -i ")
            append(inputFilePath)
            append(" -af ")
            effects.forEachIndexed { index , effect ->
                append(effect)
                if(index < effects.size - 1) {
                    append(",")
                }
            }
            append(" ")
            append(outputFilePath)
        }
        FFmpegKit.executeAsync(audioAfterEffectCommand) { session ->
            session.print()
            if(session.returnCode.isSuccess){
                it.resume(File(outputFilePath)){}
            }else {
                it.resume(null) {}
            }
        }
    }
    
    
    
    // prints any object in logcat filter using system
    fun Any.print() {
    println(this)
    }
    
private fun getCurrenciesList(): ArrayList<Currency> {
        val gson = Gson()
        val currenciesJson = """[
            {"cc":"AED","symbol":"\u062f.\u0625;","name":"UAE dirham"},
            {"cc":"AFN","symbol":"Afs","name":"Afghan afghani"},
            {"cc":"ALL","symbol":"L","name":"Albanian lek"},
            {"cc":"AMD","symbol":"AMD","name":"Armenian dram"},
            {"cc":"ANG","symbol":"NA\u0192","name":"Netherlands Antillean gulden"},
            {"cc":"AOA","symbol":"Kz","name":"Angolan kwanza"},
            {"cc":"ARS","symbol":"$","name":"Argentine peso"},
            {"cc":"AUD","symbol":"$","name":"Australian dollar"},
            {"cc":"AWG","symbol":"\u0192","name":"Aruban florin"},
            {"cc":"AZN","symbol":"AZN","name":"Azerbaijani manat"},
            {"cc":"BAM","symbol":"KM","name":"Bosnia and Herzegovina konvertibilna marka"},
            {"cc":"BBD","symbol":"Bds$","name":"Barbadian dollar"},
            {"cc":"BDT","symbol":"\u09f3","name":"Bangladeshi taka"},
            {"cc":"BGN","symbol":"BGN","name":"Bulgarian lev"},
            {"cc":"BHD","symbol":".\u062f.\u0628","name":"Bahraini dinar"},
            {"cc":"BIF","symbol":"FBu","name":"Burundi franc"},
            {"cc":"BMD","symbol":"BD$","name":"Bermudian dollar"},
            {"cc":"BND","symbol":"B$","name":"Brunei dollar"},
            {"cc":"BOB","symbol":"Bs.","name":"Bolivian boliviano"},
            {"cc":"BRL","symbol":"R$","name":"Brazilian real"},
            {"cc":"BSD","symbol":"B$","name":"Bahamian dollar"},
            {"cc":"BTN","symbol":"Nu.","name":"Bhutanese ngultrum"},
            {"cc":"BWP","symbol":"P","name":"Botswana pula"},
            {"cc":"BYR","symbol":"Br","name":"Belarusian ruble"},
            {"cc":"BZD","symbol":"BZ$","name":"Belize dollar"},
            {"cc":"CAD","symbol":"$","name":"Canadian dollar"},
            {"cc":"CDF","symbol":"F","name":"Congolese franc"},
            {"cc":"CHF","symbol":"Fr.","name":"Swiss franc"},
            {"cc":"CLP","symbol":"$","name":"Chilean peso"},
            {"cc":"CNY","symbol":"\u00a5","name":"Chinese/Yuan renminbi"},
            {"cc":"COP","symbol":"Col$","name":"Colombian peso"},
            {"cc":"CRC","symbol":"\u20a1","name":"Costa Rican colon"},
            {"cc":"CUC","symbol":"$","name":"Cuban peso"},
            {"cc":"CVE","symbol":"Esc","name":"Cape Verdean escudo"},
            {"cc":"CZK","symbol":"K\u010d","name":"Czech koruna"},
            {"cc":"DJF","symbol":"Fdj","name":"Djiboutian franc"},
            {"cc":"DKK","symbol":"Kr","name":"Danish krone"},
            {"cc":"DOP","symbol":"RD$","name":"Dominican peso"},
            {"cc":"DZD","symbol":"\u062f.\u062c","name":"Algerian dinar"},
            {"cc":"EEK","symbol":"KR","name":"Estonian kroon"},
            {"cc":"EGP","symbol":"\u00a3","name":"Egyptian pound"},
            {"cc":"ERN","symbol":"Nfa","name":"Eritrean nakfa"},
            {"cc":"ETB","symbol":"Br","name":"Ethiopian birr"},
            {"cc":"EUR","symbol":"\u20ac","name":"European Euro"},
            {"cc":"FJD","symbol":"FJ$","name":"Fijian dollar"},
            {"cc":"FKP","symbol":"\u00a3","name":"Falkland Islands pound"},
            {"cc":"GBP","symbol":"\u00a3","name":"British pound"},
            {"cc":"GEL","symbol":"GEL","name":"Georgian lari"},
            {"cc":"GHS","symbol":"GH\u20b5","name":"Ghanaian cedi"},
            {"cc":"GIP","symbol":"\u00a3","name":"Gibraltar pound"},
            {"cc":"GMD","symbol":"D","name":"Gambian dalasi"},
            {"cc":"GNF","symbol":"FG","name":"Guinean franc"},
            {"cc":"GQE","symbol":"CFA","name":"Central African CFA franc"},
            {"cc":"GTQ","symbol":"Q","name":"Guatemalan quetzal"},
            {"cc":"GYD","symbol":"GY$","name":"Guyanese dollar"},
            {"cc":"HKD","symbol":"HK$","name":"Hong Kong dollar"},
            {"cc":"HNL","symbol":"L","name":"Honduran lempira"},
            {"cc":"HRK","symbol":"kn","name":"Croatian kuna"},
            {"cc":"HTG","symbol":"G","name":"Haitian gourde"},
            {"cc":"HUF","symbol":"Ft","name":"Hungarian forint"},
            {"cc":"IDR","symbol":"Rp","name":"Indonesian rupiah"},
            {"cc":"ILS","symbol":"\u20aa","name":"Israeli new sheqel"},
            {"cc":"INR","symbol":"\u20B9","name":"Indian rupee"},
            {"cc":"IQD","symbol":"\u062f.\u0639","name":"Iraqi dinar"},
            {"cc":"IRR","symbol":"IRR","name":"Iranian rial"},
            {"cc":"ISK","symbol":"kr","name":"Icelandic kr\u00f3na"},
            {"cc":"JMD","symbol":"J$","name":"Jamaican dollar"},
            {"cc":"JOD","symbol":"JOD","name":"Jordanian dinar"},
            {"cc":"JPY","symbol":"\u00a5","name":"Japanese yen"},
            {"cc":"KES","symbol":"KSh","name":"Kenyan shilling"},
            {"cc":"KGS","symbol":"\u0441\u043e\u043c","name":"Kyrgyzstani som"},
            {"cc":"KHR","symbol":"\u17db","name":"Cambodian riel"},
            {"cc":"KMF","symbol":"KMF","name":"Comorian franc"},
            {"cc":"KPW","symbol":"W","name":"North Korean won"},
            {"cc":"KRW","symbol":"W","name":"South Korean won"},
            {"cc":"KWD","symbol":"KWD","name":"Kuwaiti dinar"},
            {"cc":"KYD","symbol":"KY$","name":"Cayman Islands dollar"},
            {"cc":"KZT","symbol":"T","name":"Kazakhstani tenge"},
            {"cc":"LAK","symbol":"KN","name":"Lao kip"},
            {"cc":"LBP","symbol":"\u00a3","name":"Lebanese lira"},
            {"cc":"LKR","symbol":"Rs","name":"Sri Lankan rupee"},
            {"cc":"LRD","symbol":"L$","name":"Liberian dollar"},
            {"cc":"LSL","symbol":"M","name":"Lesotho loti"},
            {"cc":"LTL","symbol":"Lt","name":"Lithuanian litas"},
            {"cc":"LVL","symbol":"Ls","name":"Latvian lats"},
            {"cc":"LYD","symbol":"LD","name":"Libyan dinar"},
            {"cc":"MAD","symbol":"MAD","name":"Moroccan dirham"},
            {"cc":"MDL","symbol":"MDL","name":"Moldovan leu"},
            {"cc":"MGA","symbol":"FMG","name":"Malagasy ariary"},
            {"cc":"MKD","symbol":"MKD","name":"Macedonian denar"},
            {"cc":"MMK","symbol":"K","name":"Myanma kyat"},
            {"cc":"MNT","symbol":"\u20ae","name":"Mongolian tugrik"},
            {"cc":"MOP","symbol":"P","name":"Macanese pataca"},
            {"cc":"MRO","symbol":"UM","name":"Mauritanian ouguiya"},
            {"cc":"MUR","symbol":"Rs","name":"Mauritian rupee"},
            {"cc":"MVR","symbol":"Rf","name":"Maldivian rufiyaa"},
            {"cc":"MWK","symbol":"MK","name":"Malawian kwacha"},
            {"cc":"MXN","symbol":"$","name":"Mexican peso"},
            {"cc":"MYR","symbol":"RM","name":"Malaysian ringgit"},
            {"cc":"MZM","symbol":"MTn","name":"Mozambican metical"},
            {"cc":"NAD","symbol":"N$","name":"Namibian dollar"},
            {"cc":"NGN","symbol":"\u20a6","name":"Nigerian naira"},
            {"cc":"NIO","symbol":"C$","name":"Nicaraguan c\u00f3rdoba"},
            {"cc":"NOK","symbol":"kr","name":"Norwegian krone"},
            {"cc":"NPR","symbol":"NRs","name":"Nepalese rupee"},
            {"cc":"NZD","symbol":"NZ$","name":"New Zealand dollar"},
            {"cc":"OMR","symbol":"OMR","name":"Omani rial"},
            {"cc":"PAB","symbol":"B./","name":"Panamanian balboa"},
            {"cc":"PEN","symbol":"S/.","name":"Peruvian nuevo sol"},
            {"cc":"PGK","symbol":"K","name":"Papua New Guinean kina"},
            {"cc":"PHP","symbol":"\u20b1","name":"Philippine peso"},
            {"cc":"PKR","symbol":"Rs.","name":"Pakistani rupee"},
            {"cc":"PLN","symbol":"z\u0142","name":"Polish zloty"},
            {"cc":"PYG","symbol":"\u20b2","name":"Paraguayan guarani"},
            {"cc":"QAR","symbol":"QR","name":"Qatari riyal"},
            {"cc":"RON","symbol":"L","name":"Romanian leu"},
            {"cc":"RSD","symbol":"din.","name":"Serbian dinar"},
            {"cc":"RUB","symbol":"R","name":"Russian ruble"},
            {"cc":"SAR","symbol":"SR","name":"Saudi riyal"},
            {"cc":"SBD","symbol":"SI$","name":"Solomon Islands dollar"},
            {"cc":"SCR","symbol":"SR","name":"Seychellois rupee"},
            {"cc":"SDG","symbol":"SDG","name":"Sudanese pound"},
            {"cc":"SEK","symbol":"kr","name":"Swedish krona"},
            {"cc":"SGD","symbol":"S$","name":"Singapore dollar"},
            {"cc":"SHP","symbol":"\u00a3","name":"Saint Helena pound"},
            {"cc":"SLL","symbol":"Le","name":"Sierra Leonean leone"},
            {"cc":"SOS","symbol":"Sh.","name":"Somali shilling"},
            {"cc":"SRD","symbol":"$","name":"Surinamese dollar"},
            {"cc":"SYP","symbol":"LS","name":"Syrian pound"},
            {"cc":"SZL","symbol":"E","name":"Swazi lilangeni"},
            {"cc":"THB","symbol":"\u0e3f","name":"Thai baht"},
            {"cc":"TJS","symbol":"TJS","name":"Tajikistani somoni"},
            {"cc":"TMT","symbol":"m","name":"Turkmen manat"},
            {"cc":"TND","symbol":"DT","name":"Tunisian dinar"},
            {"cc":"TRY","symbol":"TRY","name":"Turkish new lira"},
            {"cc":"TTD","symbol":"TT$","name":"Trinidad and Tobago dollar"},
            {"cc":"TWD","symbol":"NT$","name":"New Taiwan dollar"},
            {"cc":"TZS","symbol":"TZS","name":"Tanzanian shilling"},
            {"cc":"UAH","symbol":"UAH","name":"Ukrainian hryvnia"},
            {"cc":"UGX","symbol":"USh","name":"Ugandan shilling"},
            {"cc":"USD","symbol":"US$","name":"United States dollar"},
            {"cc":"UYU","symbol":"${"$"}U","name":"Uruguayan peso"},
            {"cc":"UZS","symbol":"UZS","name":"Uzbekistani som"},
            {"cc":"VEB","symbol":"Bs","name":"Venezuelan bolivar"},
            {"cc":"VND","symbol":"\u20ab","name":"Vietnamese dong"},
            {"cc":"VUV","symbol":"VT","name":"Vanuatu vatu"},
            {"cc":"WST","symbol":"WS$","name":"Samoan tala"},
            {"cc":"XAF","symbol":"CFA","name":"Central African CFA franc"},
            {"cc":"XCD","symbol":"EC$","name":"East Caribbean dollar"},
            {"cc":"XDR","symbol":"SDR","name":"Special Drawing Rights"},
            {"cc":"XOF","symbol":"CFA","name":"West African CFA franc"},
            {"cc":"XPF","symbol":"F","name":"CFP franc"},
            {"cc":"YER","symbol":"YER","name":"Yemeni rial"},
            {"cc":"ZAR","symbol":"R","name":"South African rand"},
            {"cc":"ZMK","symbol":"ZK","name":"Zambian kwacha"},
            {"cc":"ZWR","symbol":"Z$","name":"Zimbabwean dollar"}
        ]"""
        val jsonArray = JSONArray(currenciesJson)
        val currenciesList = ArrayList<Currency>(jsonArray.length())
        for(i in 0 until jsonArray.length()){
            val currency = Gson().fromJson(jsonArray.getJSONObject(i).toString(), Currency::class.java)
            currenciesList.add(currency) 
        }
        return currenciesList
    }

/** show desired loader in any fragment
@param rootView  , loader will go in the midpoint of root view
**/
@SuppressLint("ResourceType")
fun Fragment.showLoader(rootView : ViewGroup) {
    val loaderAnimation = LottieAnimationView(requireActivity()).apply {
        id = 12345
        setAnimation(R.raw.loader)
        repeatMode = LottieDrawable.INFINITE
        loop(true)
        layoutParams = ViewGroup.LayoutParams(50.toPx(), 50.toPx())
        playAnimation()
    }
    loaderAnimation.doOnLayout {
        it.x = rootView.width/2f - it.width/2
        it.y = rootView.height/2f - it.height/2
    }
    rootView.addView(loaderAnimation)
}

// pass same viewgroup that was paased in showLoader(ViewGroup)
@SuppressLint("ResourceType")
fun removeLoader(rootView: ViewGroup){
    val animationView = rootView.findViewById<LottieAnimationView>(12345)
    rootView.removeView(animationView)
}

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
