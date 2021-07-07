
object ApplicationId {
    val id = "com.vivant.hlmt.app.uat"
}

object Modules {
    val app = ":app"

    val navigation = ":navigation"

    val common = ":common"
    val commonTest = ":common_test"

    val local = ":data:local"
    val remote = ":data:remote"
    val model = ":data:model"
    val repository = ":data:repository"

    val featureHome = ":features:home"
    val featureSecurity = ":features:security"
    val featureHra = ":features:hra"
    val featureMedicationTracker = ":features:medication_tracker"
    val featureFitnessTracker = ":features:fitness_tracker"
    val featureRecordsTracker = ":features:records_tracker"
    val featureTrackParameter = ":features:track_parameter"
    val featureBlogs = ":features:blogs"
    val featureToolsCalculators = ":features:tools_calculators"
}

object Releases {
    const val versionCode = 1
    const val versionName = "1.0.3"
}

object Versions {
    val multidex = "1.0.3"
    val kotlin = "1.4.32"
    val gradle = "4.2.1"
    val compileSdk = 29
    val minSdk = 19
    val targetSdk = 29
    val appDesign = "28.0.0"
    val appCompat = "1.2.0"
    val appLegacySupport = "1.0.0"
    val coreKtx = "1.3.2"
    val constraintLayout = "2.0.4"
    val junit = "4.12"
    val androidTestRunner = "1.1.2-alpha02"
    val espressoCore = "3.2.0-alpha02"
    val retrofit = "2.9.0"
    val retrofitCoroutines = "0.9.2"
    val retrofitGson = "2.4.0"
    val gson = "2.8.6"
    val okHttp = "3.12.1"
    val coroutines = "1.1.1"
    val koin = "1.0.2"
    val timber = "4.7.1"
    val lifecycle = "2.1.0-alpha04"
    val nav = "2.3.5"
    val googleMaterialDesign = "1.2.0"
//    val room = "2.1.0-alpha06"
    val room = "2.2.4"
    val recyclerview = "1.0.0"
    val safeArgs = "2.3.5"
    val glide = "4.9.0"
    val mockwebserver = "2.7.5"
    val archCoreTest = "2.0.0"
    val androidJunit = "1.1.0"
    val mockk = "1.9.2"
    val fragmentTest = "1.1.0-alpha06"
    val databinding = "3.3.2"
    val facebooksdk = "5.13.0"
    val firebase = "18.0.1"
    val firebaseMessaging = "21.0.1"
    val firebaseAuth = "20.0.2"
    val firebaseCrashlytics = "2.10.1"
    val firebaseAnalytics = "18.0.1"
    val facebookShimmer = "0.3.0"
    val picasso = "2.71828"
    val dexter = "6.2.2"
    val datePicker = "4.2.3"
    val jSoup = "1.12.1"
    val gmsAuth = "19.0.0"
    val gmsFitness = "20.0.0"
    val fabricCrashlytics = "2.9.5"
    val sdp = "1.0.6"
    val otpView = "1.0.6"
    val mpaChart = "v3.0.3"
    val evernote = "1.2.5"
    val circalProgressView = "v1.3"
    val stepBarView = "1.1.0"
    val colorSeekBar = "1.0.2"
    val materialDesignLib = "1.6"
    val datetimepicker = "3.6.4"
    val pdfViewer = "2.0.3"
    val circleImageview = "2.2.0"
    val ucrop = "2.2.6"
    val indicatorseekbar = "2.1.2"
    val gms = "4.3.8"
}

object Libraries {
    //Multidex
    val multidex = "com.android.support:multidex:${Versions.multidex}"
    // KOIN
    val koin = "org.koin:koin-android:${Versions.koin}"
    val koinViewModel = "org.koin:koin-android-viewmodel:${Versions.koin}"
    // ROOM
    val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    val roomRunTime = "androidx.room:room-runtime:${Versions.room}"
    val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    // RETROFIT
    val retrofitCoroutineAdapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.retrofitCoroutines}"
    val gson = "com.google.code.gson:gson:${Versions.gson}"
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofitGsonConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofitGson}"
    val httpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"
    val scalarsConverter = "com.squareup.retrofit2:converter-scalars:${Versions.retrofit}"
    // Dexter : To check Permissions at Runtime
    val dexter = "com.karumi:dexter:${Versions.dexter}"
    // GLIDE
    val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    val picasso = "com.squareup.picasso:picasso:${Versions.picasso}"
    val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    val facebook = "com.facebook.android:facebook-android-sdk:${Versions.facebooksdk}"
    // FIREBASE
    val firebaseAuth = "com.google.firebase:firebase-auth:${Versions.firebaseAuth}"
    val firebaseCore = "com.google.firebase:firebase-core:${Versions.firebase}"
    val firebaseMessaging = "com.google.firebase:firebase-messaging:${Versions.firebaseMessaging}"
    val fabricsCathartics = "com.crashlytics.sdk.android:crashlytics:${Versions.fabricCrashlytics}"
    val firebaseAnalytics = "com.google.firebase:firebase-analytics:${Versions.firebaseAnalytics}"
    val firebaseCrashlytics = "com.crashlytics.sdk.android:crashlytics:${Versions.firebaseCrashlytics}"

    val gsmAuth = "com.google.android.gms:play-services-auth:${Versions.gmsAuth}"
    val gsmFitness = "com.google.android.gms:play-services-fitness:${Versions.gmsFitness}"

    //CUSTOME VIEW
    val circularProgress = "com.github.jakob-grabner:Circle-Progress-View:${Versions.circalProgressView}"
    val chartLib = "com.github.PhilJay:MPAndroidChart:${Versions.mpaChart}"
    val otpView = "com.github.aabhasr1:OtpView:${Versions.otpView}"
    val colorSeekBar = "com.github.rtugeek:ColorSeekBar:${Versions.colorSeekBar}"
    val materialDesignLib = "com.github.vajro:MaterialDesignLibrary:${Versions.materialDesignLib}"
    //facebook Shimmer
    val shimmer = "com.facebook.shimmer:shimmer:${Versions.facebookShimmer}"
    val stepbar = "com.github.imaNNeoFighT:StepBarView:${Versions.stepBarView}"
    val datePickerDialogue = "com.wdullaer:materialdatetimepicker:${Versions.datePicker}"
    // JSoup to parse HTML
    val jSoup = "org.jsoup:jsoup:${Versions.jSoup}"
    // evernote for Notification
    val evernote = "com.evernote:android-job:${Versions.evernote}"

    val circleImageview = "de.hdodenhof:circleimageview:${Versions.circleImageview}"
    val ucrop = "com.github.yalantis:ucrop:${Versions.ucrop}"
    val datetimepicker = "com.wdullaer:materialdatetimepicker:${Versions.datetimepicker}"
    val pdfViewer = "com.github.barteksc:android-pdf-viewer:${Versions.pdfViewer}"
    val indicatorseekbar = "com.github.warkiz.widget:indicatorseekbar:${Versions.indicatorseekbar}"
    val scalableSdp = "com.intuit.sdp:sdp-android:${Versions.sdp}"

    // gms google service
    val gmsService = "com.google.gms:google-services:{${Versions.gms}}}"

}

object KotlinLibraries {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    val kotlinCoroutineCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
}

object AndroidLibraries {
    // KOTLIN
    val kotlinCoroutineAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    // ANDROID
    val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    val appDesign = "com.android.support:design:${Versions.appDesign}"
    val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
    val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerview}"
    val navigation = "androidx.navigation:navigation-ui-ktx:${Versions.nav}"
    val navigationFrag = "androidx.navigation:navigation-fragment-ktx:${Versions.nav}"
    val androidxLegacySupport = "androidx.legacy:legacy-support-v4:${Versions.appLegacySupport}"
    val googleMaterialDesign = "com.google.android.material:material:${Versions.googleMaterialDesign}"
}

object TestLibraries {
    // ANDROID TEST
    val androidTestRunner = "androidx.test:runner:${Versions.androidTestRunner}"
    val espresso = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
    val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espressoCore}"
    val archCoreTest = "androidx.arch.core:core-testing:${Versions.archCoreTest}"
    val junit = "androidx.test.ext:junit:${Versions.androidJunit}"
    val fragmentNav = "androidx.fragment:fragment-testing:${Versions.fragmentTest}"
    // KOIN
    val koin = "org.koin:koin-test:${Versions.koin}"
    // MOCK WEBSERVER
    val mockWebServer = "com.squareup.okhttp:mockwebserver:${Versions.mockwebserver}"
    // MOCK
    val mockkAndroid = "io.mockk:mockk-android:${Versions.mockk}"
    val mockk = "io.mockk:mockk:${Versions.mockk}"
    // COROUTINE
    val coroutine = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    // DATA BINDING
    val databinding = "androidx.databinding:databinding-compiler:${Versions.databinding}"
}