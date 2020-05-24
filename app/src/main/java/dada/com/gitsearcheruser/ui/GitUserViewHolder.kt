package dada.com.gitsearcheruser.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dada.com.gitsearcheruser.R
import dada.com.gitsearcheruser.data.GitUser
import de.hdodenhof.circleimageview.CircleImageView
import kotlin.math.roundToInt

class GitUserViewHolder(view: View):RecyclerView.ViewHolder(view) {
    private val avatar  = view.findViewById<CircleImageView>(R.id.ci_avatar)
    private val login = view.findViewById<TextView>(R.id.tv_name)


    fun bind(gitUser: GitUser?){

        val imageSize:Int = avatar.context.resources.getDimension(R.dimen.list_avatar_image_size).roundToInt()
        Picasso.get().load(gitUser?.avatarUrl).placeholder(R.drawable.social).resize(imageSize,imageSize).into(avatar)
        login.text = gitUser?.login
    }

    companion object {
        fun create(parent: ViewGroup): GitUserViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_git_users, parent, false)
            return GitUserViewHolder(view)
        }
    }

}