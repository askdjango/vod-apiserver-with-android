package kr.nomade.apivod

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import net.alhazmy13.mediapicker.Image.ImagePicker
import android.support.v7.widget.StaggeredGridLayoutManager


class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.name

    var userToken: String = ""

    private val blogManager by androidLazy { BlogManager() }
    private val blogPostAdapter by androidLazy {
        BlogPostAdapter({ position, blogPost ->
            onItemClick(position, blogPost)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { onNewBlogPost() }

        news_list.apply {
            setHasFixedSize(true)

            val layout = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            layoutManager = layout

            clearOnScrollListeners()
        }

        news_list.adapter = blogPostAdapter

        if ( userToken.isEmpty() ) {
            launch(UI) {
                // FIXME: 별도 로그인 창을 통해, 유저로부터 입력을 받아야합니다.
                val username = "askdjango"
                val password = "mypassword"

                userToken = blogManager.getUserToken(username, password)
                toast("userToken : ${userToken}")
                requestNews()
            }
        }
        else {
            requestNews()
        }
    }

    private fun onNewBlogPost() {
        choiceDialog(arrayOf("메세지로 올리기", "사진으로 올리기"), null, { which, choice ->
            if ( which == 0 ) {
                promptDialog("새 메세지", { message ->
                    launch(UI) {
                        val blogPost = blogManager.newMessagePost(message)
                        blogPostAdapter.insertBlogPost(blogPost)
                        Log.d(TAG, "created blogPost : ${blogPost}")
                    }
                })
            }
            else {
                ImagePicker.Builder(this@MainActivity)
                        .mode(ImagePicker.Mode.CAMERA_AND_GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .scale(1200, 1200)
                        .allowMultipleImages(false)
                        .enableDebuggingMode(true)
                        .build()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ( requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK ) {
            val pathList = data!!.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as List<String>
            Log.d(TAG, "pathList : ${pathList}")

            if ( pathList.isNotEmpty() ) {
                val photoPath = pathList.get(0)

                launch(UI) {
                    val blogPost = blogManager.newPhotoPost(photoPath)
                    blogPostAdapter.insertBlogPost(blogPost)
                    Log.d(TAG, "created blogPost with photo : ${blogPost}")
                }
            }
        }
    }

    private fun onItemClick(position: Int, post: Post) {
        choiceDialog(arrayOf("글 수정", "삭제"), "포스팅 #${post.id}", { which, choice ->
            if ( which == 0 ) {
                promptDialog("포스팅 #${post.id} 수정", { message ->
                    launch(UI) {
                        val blogPost = blogManager.editMessagePost(post.id, message)
                        blogPostAdapter.updatePost(position, blogPost)
                    }
                })
            }
            else {
                launch(UI) {
                    blogManager.deletePost(post.id)
                    blogPostAdapter.removeBlogPost(position)
                }
            }
        })
    }

    private fun requestNews() {
        launch(UI) {
            Log.d(TAG, "requestNews executed.")

            val page = 1
            val response = blogManager.getPostList(page)
            blogPostAdapter.addBlogPostList(response.post_list)
        }
    }
}
