package kr.nomade.apivod

import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import ru.gildor.coroutines.retrofit.awaitResponse
import ru.gildor.coroutines.retrofit.awaitResult
import ru.gildor.coroutines.retrofit.getOrThrow
import java.io.FileInputStream
import com.google.gson.GsonBuilder
import retrofit2.converter.gson.GsonConverterFactory
import ru.gildor.coroutines.retrofit.getOrNull


class BlogManager {
    private val service: BlogService

    var userToken: String = ""

    private val authorization: String
        get() = "JWT ${userToken}"        // FIXME: JWT일 경우 "Token" -> "JWT" 변경

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                .create()

        val retrofit = Retrofit.Builder()
                .baseUrl("http://70b8046f.ngrok.io")    // FIXME: 각자의 주소로 변경
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        service = retrofit.create(BlogService::class.java)
    }

    suspend fun getUserToken(username: String, password: String): String {
        val result = service.getUserToken(username, password).awaitResult()
        var userTokenRepsonse = result.getOrNull()
        userToken = userTokenRepsonse?.token ?: ""
        return userToken
    }

    suspend fun getUserTokenWithProvider(provider: String, accessToken: String): String {
        val result = service.getUserTokenWithProvider(provider, accessToken).awaitResult()
        var userTokenRepsonse = result.getOrNull()
        userToken = userTokenRepsonse?.token ?: ""
        return userToken
    }

    suspend fun getPostList(page: Int): PostResponse {
        val result = service.getPostList(authorization, page).awaitResult()
        val postList = result.getOrThrow()
        return PostResponse(page, postList)
    }

    suspend fun newMessagePost(message: String): Post {
        val result = service.newMessagePost(authorization, message).awaitResult()
        val post = result.getOrThrow()
        return post
    }

    suspend fun editMessagePost(postId: Int, message: String): Post {
        val result = service.editMessagePost(authorization, postId, message).awaitResult()
        val post = result.getOrThrow()
        return post
    }

    suspend fun newPhotoPost(photoPath: String): Post {
        val photoName = photoPath.substring(photoPath.lastIndexOf("/") + 1)
        val extension = MimeTypeMap.getFileExtensionFromUrl(photoPath)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)

        val inputStream = FileInputStream(photoPath)
        val contentBytes = inputStream.readBytes()
        inputStream.close()

        val body = RequestBody.create(MediaType.parse(mimeType), contentBytes)
        val photoField = MultipartBody.Part.createFormData("photo", photoName, body)

        val result = service.newPhotoPost(authorization, photoField).awaitResult()
        val post = result.getOrThrow()
        return post
    }

    suspend fun deletePost(postId: Int): Boolean {
        val response = service.deletePost(authorization, postId).awaitResponse()
        return response.isSuccessful
    }

    companion object {
        val TAG = BlogManager::class.java.name
    }
}
