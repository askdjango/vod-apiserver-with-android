package kr.nomade.apivod

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_blog_post.view.*


class BlogPostAdapter(
            val onItemClick: (Int, Post) -> Unit):
        RecyclerView.Adapter<BlogPostAdapter.ViewHolder>() {

    val TAG = BlogPostAdapter::class.java.name

    private val items = ArrayList<Post>()

    fun insertBlogPost(post: Post) {
        items.add(0, post)
        notifyItemInserted(0)
    }

    fun addBlogPostList(postList: List<Post>) {
        val origIndex = items.size
        items.addAll(postList)
        notifyItemRangeInserted(origIndex, postList.size)
        Log.d(TAG, "items size : ${items.size}")
    }

    fun updatePost(position: Int, post: Post) {
        items.set(position, post)
        notifyItemChanged(position)
    }

    fun removeBlogPost(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size-position)
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.view_blog_post))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = items[position]

        holder.itemView.photo_thumb.visible(!post.photo.isNullOrEmpty())
        holder.itemView.photo_thumb.loadImg(post.photo)

        holder.itemView.message.text = "#${post.id}, ${post.message}"

        holder.itemView.setOnClickListener {
            onItemClick(position, post)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}
