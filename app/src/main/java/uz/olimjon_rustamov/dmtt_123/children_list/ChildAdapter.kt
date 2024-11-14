package uz.olimjon_rustamov.dmtt_123.children_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import uz.olimjon_rustamov.dmtt_123.R
import uz.olimjon_rustamov.dmtt_123.databinding.ItemChildBinding
import uz.olimjon_rustamov.dmtt_123.model.Child

class ChildAdapter(private val showDelete: Boolean = false) : RecyclerView.Adapter<ChildAdapter.Vh>() {

    private var children: List<Child> = listOf()
    var onChildClickListener: ((Child) -> Unit)? = null
    var onCopyClickListener: ((Child) -> Unit)? = null
    var onDeleteClickListener: ((Child) -> Unit)? = null

    fun setData(children: List<Child>) {
        this.children = children
        notifyDataSetChanged()
    }

    inner class Vh(private val binding: ItemChildBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(child: Child, position: Int) {
            binding.childNameTv.text = child.fullName
            binding.childIdTv.text = binding.root.context.getString(R.string.id_x, child.id)
            binding.countTv.text = position.plus(1).toString()

            itemView.setOnClickListener {
                onChildClickListener?.invoke(child)
            }
            binding.copyIv.setOnClickListener {
                onCopyClickListener?.invoke(child)
            }
            binding.deleteIv.isVisible = showDelete
            binding.deleteIv.setOnClickListener {
                onDeleteClickListener?.invoke(child)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemChildBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(children[position], position)
    }
}