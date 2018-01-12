package kr.nomade.apivod

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


interface BlogService {
    @POST("/api-token-auth/")       // FIXME: JWT일 경우 "api-token-auth" -> "api-jwt-auth" 변경
    @FormUrlEncoded
    fun getUserToken(
            @Field("username") username: String,
            @Field("password") password: String
    ): Call<UserTokenResponse>

    @GET("/blog/api/post/")
    fun getPostList(
            @Header("Authorization") authorization: String,
            @Query("page") page: Int
    ): Call<List<Post>>

    @POST("/blog/api/post/")
    @FormUrlEncoded
    fun newMessagePost(
            @Header("Authorization") authorization: String,
            @Field("message") message: String?
    ): Call<Post>

    @POST("/blog/api/post/")
    @Multipart
    fun newPhotoPost(
            @Header("Authorization") authorization: String,
            @Part photo: MultipartBody.Part
    ): Call<Post>

    @GET("/blog/api/post/{postId}/")
    fun getPost(
            @Header("Authorization") authorization: String,
            @Path("postId") postId: Int
    ): Call<Post>

    @PUT("/blog/api/post/{postId}/")
    @FormUrlEncoded
    fun editMessagePost(
            @Header("Authorization") authorization: String,
            @Path("postId") postId: Int,
            @Field("message") message: String
    ): Call<Post>

    @DELETE("/blog/api/post/{postId}/")
    fun deletePost(
            @Header("Authorization") authorization: String,
            @Path("postId") postId: Int
    ): Call<Post>                                       /* FIXME: body가 없는 것에 대한 처리 */
}