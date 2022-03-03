/**
 * fades out a view making in completely invisible by default
 * @param fadeDuration the duration of fade effect in milliseconds default value is 300ms
 * @param endAlpha the alpha value that view will have after the animation completes default value is 0
 */
fun View.fadeOut(fadeDuration:Long = 300,endAlpha:Float = 0f){
    ValueAnimator.ofFloat(1f,endAlpha).apply {
        duration = fadeDuration
        addUpdateListener {
            val animatedValue = it.animatedValue as Float
            alpha = animatedValue
        }
    }.start()
}

/**
 * fades in a view 
 * @param fadeDuration the duration of fade effect in milliseconds default value is 300ms
 */
fun View.fadeIn(fadeDuration:Long = 300){
    ValueAnimator.ofFloat(0f,1f).apply {
        duration = fadeDuration
        addUpdateListener {
            val animatedValue = it.animatedValue as Float
            alpha = animatedValue
        }
    }.start()
}

/**
 * sign in user with google using firebase with given google signin account and give back result without callback
 * @param account GoogleSignInAccount that you want to sign into
 * @return true if signin was successful false otherwise
 */
suspend fun signInWithGoogleFirebase(account: GoogleSignInAccount) = suspendCancellableCoroutine<Boolean> {  continuation ->
    //showLoader(binding.rootView)
    val mAuth = FirebaseAuth.getInstance()
    val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
    mAuth.signInWithCredential(authCredential).addOnCompleteListener {  signInTask ->
        if(signInTask.isSuccessful){
            continuation.resume(true,null)
        }else{
            continuation.resume(false,null)
        }
    }
}
/**
 * @author Abhinav Chouhan
 * @since 7-dec-2021
 * checks whether or not the given CharSequence contains at least given number of given
 * @param number at least these many digits should be present 
 * @return true if at least given number of digits are present false otherwise 
 */
fun CharSequence.hasAtLeastDigits(number:Int) = contains("[0-9]{${number},}".toRegex())

/**
 * * @author Abhinav Chouhan
 * @since 7-dec-2021
 * checks whether or not the given CharSequence contains at least given number of special characters 
 * @param number at least these many special character should be present 
 *  @return true if at least given number of special characters are present false otherwise
 */
fun CharSequence.hasAtLeastSpecialCharacters(number:Int) = contains("[\\W]{${number},}".toRegex())


/**
 * @author Abhinav Chouhan 
 * @since 7-dec-2021
 * checks whether or not the given CharSequence contains at least given number of length long
 * @param number at least these characters should be present
 *  @return true if at least given number of characters are present false otherwise
 */
fun CharSequence.hasAtLeastLength(number:Int) = length >= number
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
    
 /**
 * @return a list of all countries with their name, dialing code ,and code
 * country class should be defined like data class Country(val name:String,@SerializedName("dial_code")val dialCode:String,val code:String)
 */
fun getCountriesList(): List<Country> {

    val countriesJson = """[
{
"name": "Afghanistan",
"dial_code": "+93",
"code": "AF"
},
{
"name": "Aland Islands",
"dial_code": "+358",
"code": "AX"
},
{
"name": "Albania",
"dial_code": "+355",
"code": "AL"
},
{
"name": "Algeria",
"dial_code": "+213",
"code": "DZ"
},
{
"name": "AmericanSamoa",
"dial_code": "+1684",
"code": "AS"
},
{
"name": "Andorra",
"dial_code": "+376",
"code": "AD"
},
{
"name": "Angola",
"dial_code": "+244",
"code": "AO"
},
{
"name": "Anguilla",
"dial_code": "+1264",
"code": "AI"
},
{
"name": "Antarctica",
"dial_code": "+672",
"code": "AQ"
},
{
"name": "Antigua and Barbuda",
"dial_code": "+1268",
"code": "AG"
},
{
"name": "Argentina",
"dial_code": "+54",
"code": "AR"
},
{
"name": "Armenia",
"dial_code": "+374",
"code": "AM"
},
{
"name": "Aruba",
"dial_code": "+297",
"code": "AW"
},
{
"name": "Australia",
"dial_code": "+61",
"code": "AU"
},
{
"name": "Austria",
"dial_code": "+43",
"code": "AT"
},
{
"name": "Azerbaijan",
"dial_code": "+994",
"code": "AZ"
},
{
"name": "Bahamas",
"dial_code": "+1242",
"code": "BS"
},
{
"name": "Bahrain",
"dial_code": "+973",
"code": "BH"
},
{
"name": "Bangladesh",
"dial_code": "+880",
"code": "BD"
},
{
"name": "Barbados",
"dial_code": "+1246",
"code": "BB"
},
{
"name": "Belarus",
"dial_code": "+375",
"code": "BY"
},
{
"name": "Belgium",
"dial_code": "+32",
"code": "BE"
},
{
"name": "Belize",
"dial_code": "+501",
"code": "BZ"
},
{
"name": "Benin",
"dial_code": "+229",
"code": "BJ"
},
{
"name": "Bermuda",
"dial_code": "+1441",
"code": "BM"
},
{
"name": "Bhutan",
"dial_code": "+975",
"code": "BT"
},
{
"name": "Bolivia, Plurinational State of",
"dial_code": "+591",
"code": "BO"
},
{
"name": "Bosnia and Herzegovina",
"dial_code": "+387",
"code": "BA"
},
{
"name": "Botswana",
"dial_code": "+267",
"code": "BW"
},
{
"name": "Brazil",
"dial_code": "+55",
"code": "BR"
},
{
"name": "British Indian Ocean Territory",
"dial_code": "+246",
"code": "IO"
},
{
"name": "Brunei Darussalam",
"dial_code": "+673",
"code": "BN"
},
{
"name": "Bulgaria",
"dial_code": "+359",
"code": "BG"
},
{
"name": "Burkina Faso",
"dial_code": "+226",
"code": "BF"
},
{
"name": "Burundi",
"dial_code": "+257",
"code": "BI"
},
{
"name": "Cambodia",
"dial_code": "+855",
"code": "KH"
},
{
"name": "Cameroon",
"dial_code": "+237",
"code": "CM"
},
{
"name": "Canada",
"dial_code": "+1",
"code": "CA"
},
{
"name": "Cape Verde",
"dial_code": "+238",
"code": "CV"
},
{
"name": "Cayman Islands",
"dial_code": "+ 345",
"code": "KY"
},
{
"name": "Central African Republic",
"dial_code": "+236",
"code": "CF"
},
{
"name": "Chad",
"dial_code": "+235",
"code": "TD"
},
{
"name": "Chile",
"dial_code": "+56",
"code": "CL"
},
{
"name": "China",
"dial_code": "+86",
"code": "CN"
},
{
"name": "Christmas Island",
"dial_code": "+61",
"code": "CX"
},
{
"name": "Cocos (Keeling) Islands",
"dial_code": "+61",
"code": "CC"
},
{
"name": "Colombia",
"dial_code": "+57",
"code": "CO"
},
{
"name": "Comoros",
"dial_code": "+269",
"code": "KM"
},
{
"name": "Congo",
"dial_code": "+242",
"code": "CG"
},
{
"name": "Congo, The Democratic Republic of the Congo",
"dial_code": "+243",
"code": "CD"
},
{
"name": "Cook Islands",
"dial_code": "+682",
"code": "CK"
},
{
"name": "Costa Rica",
"dial_code": "+506",
"code": "CR"
},
{
"name": "Cote d'Ivoire",
"dial_code": "+225",
"code": "CI"
},
{
"name": "Croatia",
"dial_code": "+385",
"code": "HR"
},
{
"name": "Cuba",
"dial_code": "+53",
"code": "CU"
},
{
"name": "Cyprus",
"dial_code": "+357",
"code": "CY"
},
{
"name": "Czech Republic",
"dial_code": "+420",
"code": "CZ"
},
{
"name": "Denmark",
"dial_code": "+45",
"code": "DK"
},
{
"name": "Djibouti",
"dial_code": "+253",
"code": "DJ"
},
{
"name": "Dominica",
"dial_code": "+1767",
"code": "DM"
},
{
"name": "Dominican Republic",
"dial_code": "+1849",
"code": "DO"
},
{
"name": "Ecuador",
"dial_code": "+593",
"code": "EC"
},
{
"name": "Egypt",
"dial_code": "+20",
"code": "EG"
},
{
"name": "El Salvador",
"dial_code": "+503",
"code": "SV"
},
{
"name": "Equatorial Guinea",
"dial_code": "+240",
"code": "GQ"
},
{
"name": "Eritrea",
"dial_code": "+291",
"code": "ER"
},
{
"name": "Estonia",
"dial_code": "+372",
"code": "EE"
},
{
"name": "Ethiopia",
"dial_code": "+251",
"code": "ET"
},
{
"name": "Falkland Islands (Malvinas)",
"dial_code": "+500",
"code": "FK"
},
{
"name": "Faroe Islands",
"dial_code": "+298",
"code": "FO"
},
{
"name": "Fiji",
"dial_code": "+679",
"code": "FJ"
},
{
"name": "Finland",
"dial_code": "+358",
"code": "FI"
},
{
"name": "France",
"dial_code": "+33",
"code": "FR"
},
{
"name": "French Guiana",
"dial_code": "+594",
"code": "GF"
},
{
"name": "French Polynesia",
"dial_code": "+689",
"code": "PF"
},
{
"name": "Gabon",
"dial_code": "+241",
"code": "GA"
},
{
"name": "Gambia",
"dial_code": "+220",
"code": "GM"
},
{
"name": "Georgia",
"dial_code": "+995",
"code": "GE"
},
{
"name": "Germany",
"dial_code": "+49",
"code": "DE"
},
{
"name": "Ghana",
"dial_code": "+233",
"code": "GH"
},
{
"name": "Gibraltar",
"dial_code": "+350",
"code": "GI"
},
{
"name": "Greece",
"dial_code": "+30",
"code": "GR"
},
{
"name": "Greenland",
"dial_code": "+299",
"code": "GL"
},
{
"name": "Grenada",
"dial_code": "+1473",
"code": "GD"
},
{
"name": "Guadeloupe",
"dial_code": "+590",
"code": "GP"
},
{
"name": "Guam",
"dial_code": "+1671",
"code": "GU"
},
{
"name": "Guatemala",
"dial_code": "+502",
"code": "GT"
},
{
"name": "Guernsey",
"dial_code": "+44",
"code": "GG"
},
{
"name": "Guinea",
"dial_code": "+224",
"code": "GN"
},
{
"name": "Guinea-Bissau",
"dial_code": "+245",
"code": "GW"
},
{
"name": "Guyana",
"dial_code": "+595",
"code": "GY"
},
{
"name": "Haiti",
"dial_code": "+509",
"code": "HT"
},
{
"name": "Holy See (Vatican City State)",
"dial_code": "+379",
"code": "VA"
},
{
"name": "Honduras",
"dial_code": "+504",
"code": "HN"
},
{
"name": "Hong Kong",
"dial_code": "+852",
"code": "HK"
},
{
"name": "Hungary",
"dial_code": "+36",
"code": "HU"
},
{
"name": "Iceland",
"dial_code": "+354",
"code": "IS"
},
{
"name": "India",
"dial_code": "+91",
"code": "IN"
},
{
"name": "Indonesia",
"dial_code": "+62",
"code": "ID"
},
{
"name": "Iran, Islamic Republic of Persian Gulf",
"dial_code": "+98",
"code": "IR"
},
{
"name": "Iraq",
"dial_code": "+964",
"code": "IQ"
},
{
"name": "Ireland",
"dial_code": "+353",
"code": "IE"
},
{
"name": "Isle of Man",
"dial_code": "+44",
"code": "IM"
},
{
"name": "Israel",
"dial_code": "+972",
"code": "IL"
},
{
"name": "Italy",
"dial_code": "+39",
"code": "IT"
},
{
"name": "Jamaica",
"dial_code": "+1876",
"code": "JM"
},
{
"name": "Japan",
"dial_code": "+81",
"code": "JP"
},
{
"name": "Jersey",
"dial_code": "+44",
"code": "JE"
},
{
"name": "Jordan",
"dial_code": "+962",
"code": "JO"
},
{
"name": "Kazakhstan",
"dial_code": "+77",
"code": "KZ"
},
{
"name": "Kenya",
"dial_code": "+254",
"code": "KE"
},
{
"name": "Kiribati",
"dial_code": "+686",
"code": "KI"
},
{
"name": "Korea, Democratic People's Republic of Korea",
"dial_code": "+850",
"code": "KP"
},
{
"name": "Korea, Republic of South Korea",
"dial_code": "+82",
"code": "KR"
},
{
"name": "Kuwait",
"dial_code": "+965",
"code": "KW"
},
{
"name": "Kyrgyzstan",
"dial_code": "+996",
"code": "KG"
},
{
"name": "Laos",
"dial_code": "+856",
"code": "LA"
},
{
"name": "Latvia",
"dial_code": "+371",
"code": "LV"
},
{
"name": "Lebanon",
"dial_code": "+961",
"code": "LB"
},
{
"name": "Lesotho",
"dial_code": "+266",
"code": "LS"
},
{
"name": "Liberia",
"dial_code": "+231",
"code": "LR"
},
{
"name": "Libyan Arab Jamahiriya",
"dial_code": "+218",
"code": "LY"
},
{
"name": "Liechtenstein",
"dial_code": "+423",
"code": "LI"
},
{
"name": "Lithuania",
"dial_code": "+370",
"code": "LT"
},
{
"name": "Luxembourg",
"dial_code": "+352",
"code": "LU"
},
{
"name": "Macao",
"dial_code": "+853",
"code": "MO"
},
{
"name": "Macedonia",
"dial_code": "+389",
"code": "MK"
},
{
"name": "Madagascar",
"dial_code": "+261",
"code": "MG"
},
{
"name": "Malawi",
"dial_code": "+265",
"code": "MW"
},
{
"name": "Malaysia",
"dial_code": "+60",
"code": "MY"
},
{
"name": "Maldives",
"dial_code": "+960",
"code": "MV"
},
{
"name": "Mali",
"dial_code": "+223",
"code": "ML"
},
{
"name": "Malta",
"dial_code": "+356",
"code": "MT"
},
{
"name": "Marshall Islands",
"dial_code": "+692",
"code": "MH"
},
{
"name": "Martinique",
"dial_code": "+596",
"code": "MQ"
},
{
"name": "Mauritania",
"dial_code": "+222",
"code": "MR"
},
{
"name": "Mauritius",
"dial_code": "+230",
"code": "MU"
},
{
"name": "Mayotte",
"dial_code": "+262",
"code": "YT"
},
{
"name": "Mexico",
"dial_code": "+52",
"code": "MX"
},
{
"name": "Micronesia, Federated States of Micronesia",
"dial_code": "+691",
"code": "FM"
},
{
"name": "Moldova",
"dial_code": "+373",
"code": "MD"
},
{
"name": "Monaco",
"dial_code": "+377",
"code": "MC"
},
{
"name": "Mongolia",
"dial_code": "+976",
"code": "MN"
},
{
"name": "Montenegro",
"dial_code": "+382",
"code": "ME"
},
{
"name": "Montserrat",
"dial_code": "+1664",
"code": "MS"
},
{
"name": "Morocco",
"dial_code": "+212",
"code": "MA"
},
{
"name": "Mozambique",
"dial_code": "+258",
"code": "MZ"
},
{
"name": "Myanmar",
"dial_code": "+95",
"code": "MM"
},
{
"name": "Namibia",
"dial_code": "+264",
"code": "NA"
},
{
"name": "Nauru",
"dial_code": "+674",
"code": "NR"
},
{
"name": "Nepal",
"dial_code": "+977",
"code": "NP"
},
{
"name": "Netherlands",
"dial_code": "+31",
"code": "NL"
},
{
"name": "Netherlands Antilles",
"dial_code": "+599",
"code": "AN"
},
{
"name": "New Caledonia",
"dial_code": "+687",
"code": "NC"
},
{
"name": "New Zealand",
"dial_code": "+64",
"code": "NZ"
},
{
"name": "Nicaragua",
"dial_code": "+505",
"code": "NI"
},
{
"name": "Niger",
"dial_code": "+227",
"code": "NE"
},
{
"name": "Nigeria",
"dial_code": "+234",
"code": "NG"
},
{
"name": "Niue",
"dial_code": "+683",
"code": "NU"
},
{
"name": "Norfolk Island",
"dial_code": "+672",
"code": "NF"
},
{
"name": "Northern Mariana Islands",
"dial_code": "+1670",
"code": "MP"
},
{
"name": "Norway",
"dial_code": "+47",
"code": "NO"
},
{
"name": "Oman",
"dial_code": "+968",
"code": "OM"
},
{
"name": "Pakistan",
"dial_code": "+92",
"code": "PK"
},
{
"name": "Palau",
"dial_code": "+680",
"code": "PW"
},
{
"name": "Palestinian Territory, Occupied",
"dial_code": "+970",
"code": "PS"
},
{
"name": "Panama",
"dial_code": "+507",
"code": "PA"
},
{
"name": "Papua New Guinea",
"dial_code": "+675",
"code": "PG"
},
{
"name": "Paraguay",
"dial_code": "+595",
"code": "PY"
},
{
"name": "Peru",
"dial_code": "+51",
"code": "PE"
},
{
"name": "Philippines",
"dial_code": "+63",
"code": "PH"
},
{
"name": "Pitcairn",
"dial_code": "+872",
"code": "PN"
},
{
"name": "Poland",
"dial_code": "+48",
"code": "PL"
},
{
"name": "Portugal",
"dial_code": "+351",
"code": "PT"
},
{
"name": "Puerto Rico",
"dial_code": "+1939",
"code": "PR"
},
{
"name": "Qatar",
"dial_code": "+974",
"code": "QA"
},
{
"name": "Romania",
"dial_code": "+40",
"code": "RO"
},
{
"name": "Russia",
"dial_code": "+7",
"code": "RU"
},
{
"name": "Rwanda",
"dial_code": "+250",
"code": "RW"
},
{
"name": "Reunion",
"dial_code": "+262",
"code": "RE"
},
{
"name": "Saint Barthelemy",
"dial_code": "+590",
"code": "BL"
},
{
"name": "Saint Helena, Ascension and Tristan Da Cunha",
"dial_code": "+290",
"code": "SH"
},
{
"name": "Saint Kitts and Nevis",
"dial_code": "+1869",
"code": "KN"
},
{
"name": "Saint Lucia",
"dial_code": "+1758",
"code": "LC"
},
{
"name": "Saint Martin",
"dial_code": "+590",
"code": "MF"
},
{
"name": "Saint Pierre and Miquelon",
"dial_code": "+508",
"code": "PM"
},
{
"name": "Saint Vincent and the Grenadines",
"dial_code": "+1784",
"code": "VC"
},
{
"name": "Samoa",
"dial_code": "+685",
"code": "WS"
},
{
"name": "San Marino",
"dial_code": "+378",
"code": "SM"
},
{
"name": "Sao Tome and Principe",
"dial_code": "+239",
"code": "ST"
},
{
"name": "Saudi Arabia",
"dial_code": "+966",
"code": "SA"
},
{
"name": "Senegal",
"dial_code": "+221",
"code": "SN"
},
{
"name": "Serbia",
"dial_code": "+381",
"code": "RS"
},
{
"name": "Seychelles",
"dial_code": "+248",
"code": "SC"
},
{
"name": "Sierra Leone",
"dial_code": "+232",
"code": "SL"
},
{
"name": "Singapore",
"dial_code": "+65",
"code": "SG"
},
{
"name": "Slovakia",
"dial_code": "+421",
"code": "SK"
},
{
"name": "Slovenia",
"dial_code": "+386",
"code": "SI"
},
{
"name": "Solomon Islands",
"dial_code": "+677",
"code": "SB"
},
{
"name": "Somalia",
"dial_code": "+252",
"code": "SO"
},
{
"name": "South Africa",
"dial_code": "+27",
"code": "ZA"
},
{
"name": "South Sudan",
"dial_code": "+211",
"code": "SS"
},
{
"name": "South Georgia and the South Sandwich Islands",
"dial_code": "+500",
"code": "GS"
},
{
"name": "Spain",
"dial_code": "+34",
"code": "ES"
},
{
"name": "Sri Lanka",
"dial_code": "+94",
"code": "LK"
},
{
"name": "Sudan",
"dial_code": "+249",
"code": "SD"
},
{
"name": "Suriname",
"dial_code": "+597",
"code": "SR"
},
{
"name": "Svalbard and Jan Mayen",
"dial_code": "+47",
"code": "SJ"
},
{
"name": "Swaziland",
"dial_code": "+268",
"code": "SZ"
},
{
"name": "Sweden",
"dial_code": "+46",
"code": "SE"
},
{
"name": "Switzerland",
"dial_code": "+41",
"code": "CH"
},
{
"name": "Syrian Arab Republic",
"dial_code": "+963",
"code": "SY"
},
{
"name": "Taiwan",
"dial_code": "+886",
"code": "TW"
},
{
"name": "Tajikistan",
"dial_code": "+992",
"code": "TJ"
},
{
"name": "Tanzania, United Republic of Tanzania",
"dial_code": "+255",
"code": "TZ"
},
{
"name": "Thailand",
"dial_code": "+66",
"code": "TH"
},
{
"name": "Timor-Leste",
"dial_code": "+670",
"code": "TL"
},
{
"name": "Togo",
"dial_code": "+228",
"code": "TG"
},
{
"name": "Tokelau",
"dial_code": "+690",
"code": "TK"
},
{
"name": "Tonga",
"dial_code": "+676",
"code": "TO"
},
{
"name": "Trinidad and Tobago",
"dial_code": "+1868",
"code": "TT"
},
{
"name": "Tunisia",
"dial_code": "+216",
"code": "TN"
},
{
"name": "Turkey",
"dial_code": "+90",
"code": "TR"
},
{
"name": "Turkmenistan",
"dial_code": "+993",
"code": "TM"
},
{
"name": "Turks and Caicos Islands",
"dial_code": "+1649",
"code": "TC"
},
{
"name": "Tuvalu",
"dial_code": "+688",
"code": "TV"
},
{
"name": "Uganda",
"dial_code": "+256",
"code": "UG"
},
{
"name": "Ukraine",
"dial_code": "+380",
"code": "UA"
},
{
"name": "United Arab Emirates",
"dial_code": "+971",
"code": "AE"
},
{
"name": "United Kingdom",
"dial_code": "+44",
"code": "GB"
},
{
"name": "United States",
"dial_code": "+1",
"code": "US"
},
{
"name": "Uruguay",
"dial_code": "+598",
"code": "UY"
},
{
"name": "Uzbekistan",
"dial_code": "+998",
"code": "UZ"
},
{
"name": "Vanuatu",
"dial_code": "+678",
"code": "VU"
},
{
"name": "Venezuela, Bolivarian Republic of Venezuela",
"dial_code": "+58",
"code": "VE"
},
{
"name": "Vietnam",
"dial_code": "+84",
"code": "VN"
},
{
"name": "Virgin Islands, British",
"dial_code": "+1284",
"code": "VG"
},
{
"name": "Virgin Islands, U.S.",
"dial_code": "+1340",
"code": "VI"
},
{
"name": "Wallis and Futuna",
"dial_code": "+681",
"code": "WF"
},
{
"name": "Yemen",
"dial_code": "+967",
"code": "YE"
},
{
"name": "Zambia",
"dial_code": "+260",
"code": "ZM"
},
{
"name": "Zimbabwe",
"dial_code": "+263",
"code": "ZW"
}
]"""
    val jsonArray = JSONArray(countriesJson)
    val countriesList = ArrayList<Country>(jsonArray.length())
    for (i in 0 until jsonArray.length()) {
        val country = Gson().fromJson(jsonArray.getJSONObject(i).toString(), Country::class.java)
        countriesList.add(country)
    }
    return countriesList
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
    
    
    /**
 * get the user country dialing code
 * @return the dialing code of user , null if dialog code could not be extracted
 * countries array
 *   <string-array name="CountryCodes">
<item>93,AF</item>
<item>355,AL</item>
<item>213,DZ</item>
<item>376,AD</item>
<item>244,AO</item>
<item>672,AQ</item>
<item>54,AR</item>
<item>374,AM</item>
<item>297,AW</item>
<item>61,AU</item>
<item>43,AT</item>
<item>994,AZ</item>
<item>973,BH</item>
<item>880,BD</item>
<item>375,BY</item>
<item>32,BE</item>
<item>501,BZ</item>
<item>229,BJ</item>
<item>975,BT</item>
<item>591,BO</item>
<item>387,BA</item>
<item>267,BW</item>
<item>55,BR</item>
<item>673,BN</item>
<item>359,BG</item>
<item>226,BF</item>
<item>95,MM</item>
<item>257,BI</item>
<item>855,KH</item>
<item>237,CM</item>
<item>1,CA</item>
<item>238,CV</item>
<item>236,CF</item>
<item>235,TD</item>
<item>56,CL</item>
<item>86,CN</item>
<item>61,CX</item>
<item>61,CC</item>
<item>57,CO</item>
<item>269,KM</item>
<item>242,CG</item>
<item>243,CD</item>
<item>682,CK</item>
<item>506,CR</item>
<item>385,HR</item>
<item>53,CU</item>
<item>357,CY</item>
<item>420,CZ</item>
<item>45,DK</item>
<item>253,DJ</item>
<item>670,TL</item>
<item>593,EC</item>
<item>20,EG</item>
<item>503,SV</item>
<item>240,GQ</item>
<item>291,ER</item>
<item>372,EE</item>
<item>251,ET</item>
<item>500,FK</item>
<item>298,FO</item>
<item>679,FJ</item>
<item>358,FI</item>
<item>33,FR</item>
<item>689,PF</item>
<item>241,GA</item>
<item>220,GM</item>
<item>995,GE</item>
<item>49,DE</item>
<item>233,GH</item>
<item>350,GI</item>
<item>30,GR</item>
<item>299,GL</item>
<item>502,GT</item>
<item>224,GN</item>
<item>245,GW</item>
<item>592,GY</item>
<item>509,HT</item>
<item>504,HN</item>
<item>852,HK</item>
<item>36,HU</item>
<item>91,IN</item>
<item>62,ID</item>
<item>98,IR</item>
<item>964,IQ</item>
<item>353,IE</item>
<item>44,IM</item>
<item>972,IL</item>
<item>39,IT</item>
<item>225,CI</item>
<item>81,JP</item>
<item>962,JO</item>
<item>7,KZ</item>
<item>254,KE</item>
<item>686,KI</item>
<item>965,KW</item>
<item>996,KG</item>
<item>856,LA</item>
<item>371,LV</item>
<item>961,LB</item>
<item>266,LS</item>
<item>231,LR</item>
<item>218,LY</item>
<item>423,LI</item>
<item>370,LT</item>
<item>352,LU</item>
<item>853,MO</item>
<item>389,MK</item>
<item>261,MG</item>
<item>265,MW</item>
<item>60,MY</item>
<item>960,MV</item>
<item>223,ML</item>
<item>356,MT</item>
<item>692,MH</item>
<item>222,MR</item>
<item>230,MU</item>
<item>262,YT</item>
<item>52,MX</item>
<item>691,FM</item>
<item>373,MD</item>
<item>377,MC</item>
<item>976,MN</item>
<item>382,ME</item>
<item>212,MA</item>
<item>258,MZ</item>
<item>264,NA</item>
<item>674,NR</item>
<item>977,NP</item>
<item>31,NL</item>
<item>599,AN</item>
<item>687,NC</item>
<item>64,NZ</item>
<item>505,NI</item>
<item>227,NE</item>
<item>234,NG</item>
<item>683,NU</item>
<item>850,KP</item>
<item>47,NO</item>
<item>968,OM</item>
<item>92,PK</item>
<item>680,PW</item>
<item>507,PA</item>
<item>675,PG</item>
<item>595,PY</item>
<item>51,PE</item>
<item>63,PH</item>
<item>870,PN</item>
<item>48,PL</item>
<item>351,PT</item>
<item>1,PR</item>
<item>974,QA</item>
<item>40,RO</item>
<item>7,RU</item>
<item>250,RW</item>
<item>590,BL</item>
<item>685,WS</item>
<item>378,SM</item>
<item>239,ST</item>
<item>966,SA</item>
<item>221,SN</item>
<item>381,RS</item>
<item>248,SC</item>
<item>232,SL</item>
<item>65,SG</item>
<item>421,SK</item>
<item>386,SI</item>
<item>677,SB</item>
<item>252,SO</item>
<item>27,ZA</item>
<item>82,KR</item>
<item>34,ES</item>
<item>94,LK</item>
<item>290,SH</item>
<item>508,PM</item>
<item>249,SD</item>
<item>597,SR</item>
<item>268,SZ</item>
<item>46,SE</item>
<item>41,CH</item>
<item>963,SY</item>
<item>886,TW</item>
<item>992,TJ</item>
<item>255,TZ</item>
<item>66,TH</item>
<item>228,TG</item>
<item>690,TK</item>
<item>676,TO</item>
<item>216,TN</item>
<item>90,TR</item>
<item>993,TM</item>
<item>688,TV</item>
<item>971,AE</item>
<item>256,UG</item>
<item>44,GB</item>
<item>380,UA</item>
<item>598,UY</item>
<item>1,US</item>
<item>998,UZ</item>
<item>678,VU</item>
<item>39,VA</item>
<item>58,VE</item>
<item>84,VN</item>
<item>681,WF</item>
<item>967,YE</item>
<item>260,ZM</item>
<item>263,ZW</item>
</string-array>
 */
fun Fragment.getDialingCode(): String? {
    val telephonyManager =
        requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    val code = telephonyManager.networkCountryIso.toUpperCase()
    val countryNames = resources.getStringArray(R.array.CountryCodes)
    var dialingCode: String? = null
    countryNames.iterator().forEach {
        if (it.substringAfter(",") == code) {
            dialingCode = "+" + it.substringBefore(",")
            return@forEach
        }
    }
    return dialingCode
}
