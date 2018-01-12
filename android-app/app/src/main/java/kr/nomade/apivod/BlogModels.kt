package kr.nomade.apivod

import java.util.*


data class UserTokenResponse(
        val token: String)


data class PostResponse(
        val page: Int,
        val post_list: List<Post>)


data class Post(
        val id: Int,
        val message: String?,
        val photo: String?,
        val updated_at: Date)
