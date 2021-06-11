package com.caressa.remote.di

import com.caressa.common.constants.Constants
import com.caressa.remote.*
import com.caressa.remote.interceptor.DecryptInterceptor
import com.caressa.remote.interceptor.EncryptInterceptor
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

private const val DEFAULT = "DEFAULT"
private const val ENCRYPTED = "ENCRYPTED"
private const val BLOGS = "BLOGS"

fun createRemoteModule(baseUrl: String) = module {

    factory<Interceptor> {

        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
            Timber.i("HttpLogging==>" + message)
        }).setLevel(HttpLoggingInterceptor.Level.BODY)

        /* HttpLoggingInterceptor()
             .setLevel(HttpLoggingInterceptor.Level.BODY)*/
    }

    factory {
        OkHttpClient.Builder()
            .protocols(Arrays.asList(Protocol.HTTP_1_1))
            .connectTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .addInterceptor(get())
            .addInterceptor(DecryptionInterceptor())
            .build()
    }

    single(name = ENCRYPTED) {
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .protocols(Arrays.asList(Protocol.HTTP_1_1))
                    .connectTimeout(3, TimeUnit.MINUTES)
                    .writeTimeout(3, TimeUnit.MINUTES)
                    .readTimeout(3, TimeUnit.MINUTES)
                    .addNetworkInterceptor(EncryptInterceptor())
                    .addInterceptor(get())
                    .addInterceptor(DecryptInterceptor())
                    .build()
            )
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    single(name = DEFAULT) {
        Retrofit.Builder()
            .client(get())
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    single(name = BLOGS) {
        Retrofit.Builder()
            .client(get())
            .baseUrl(Constants.strBlogsBaseURL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    single(name = DEFAULT) { get<Retrofit>(DEFAULT).create(ApiService::class.java) }

    single(name = ENCRYPTED) { get<Retrofit>(ENCRYPTED).create(ApiService::class.java) }

    single(name = BLOGS) { get<Retrofit>(BLOGS).create(ApiService::class.java) }


    factory { SecurityDatasource(get(DEFAULT), get(ENCRYPTED)) }

    factory { MedicationDatasource(get(DEFAULT), get(ENCRYPTED)) }

    factory { FitnessDatasource(get(DEFAULT), get(ENCRYPTED)) }

    factory { ParameterDatasource(get(DEFAULT), get(ENCRYPTED)) }

    factory { ToolsDatasource(get(DEFAULT), get(ENCRYPTED)) }

    factory { HraDatasource(get(DEFAULT), get(ENCRYPTED)) }

    factory { ShrDatasource(get(DEFAULT), get(ENCRYPTED)) }

    factory { ToolsCalculatorsDatasource(get(DEFAULT), get(ENCRYPTED)) }

    factory { BlogsDatasource(get(BLOGS)) }

    factory { HomeDatasource(get(DEFAULT), get(ENCRYPTED)) }


}

