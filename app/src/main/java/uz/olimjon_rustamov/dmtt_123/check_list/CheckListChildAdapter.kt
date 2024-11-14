package uz.olimjon_rustamov.dmtt_123.check_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.olimjon_rustamov.dmtt_123.databinding.ItemChildCheckListBinding

class CheckListChildAdapter :
    RecyclerView.Adapter<CheckListChildAdapter.Vh>() {

    private var children: ArrayList<Pair<String, Boolean>> = arrayListOf()
    var onCheckboxChangeListener: ((Pair<String, Boolean>, Boolean) -> Unit)? = null

    fun setData(children: List<Pair<String, Boolean>>) {
        this.children = children as ArrayList<Pair<String, Boolean>>
        notifyDataSetChanged()
    }

    inner class Vh(private val binding: ItemChildCheckListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(child: Pair<String, Boolean>, position: Int) {
            binding.childNameTv.text = child.first
            binding.checkbox.isChecked = child.second
            binding.countTv.text = position.plus(1).toString()

            binding.parentLayout.setOnClickListener {
                val isChecked = binding.checkbox.isChecked
                binding.checkbox.isChecked = !isChecked
                children[position] = child.first to !isChecked
                onCheckboxChangeListener?.invoke(child, !isChecked)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(
            ItemChildCheckListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(children[position], position)
    }
}