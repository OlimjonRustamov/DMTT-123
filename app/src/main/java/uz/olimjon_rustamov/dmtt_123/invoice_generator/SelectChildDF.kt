package uz.olimjon_rustamov.dmtt_123.invoice_generator

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.olimjon_rustamov.dmtt_123.R
import uz.olimjon_rustamov.dmtt_123.children_list.ChildAdapter
import uz.olimjon_rustamov.dmtt_123.databinding.FragmentChildrenListBinding
import uz.olimjon_rustamov.dmtt_123.model.Child

class SelectChildDF : DialogFragment() {

    private var _binding: FragmentChildrenListBinding? = null
    private val binding get() = _binding!!
    private var onChildClickListener: ((Child) -> Unit)? = null
    private val adapter by lazy { ChildAdapter() }
    private val dbRef by lazy { Firebase.database.getReference("children") }

    fun setOnChildClickListener(listener: (Child) -> Unit) {
        onChildClickListener = listener
    }

    override fun getTheme(): Int = R.style.FullScreenDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChildrenListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.childrenListRV.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
        dbRef.get().addOnSuccessListener {
            val childrenList = mutableListOf<Child>()
            for (child in it.children) {
                child.getValue(Child::class.java)?.let { it1 -> childrenList.add(it1) }
            }
            adapter.setData(childrenList)

            binding.childrenListRV.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }.addOnFailureListener {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
        }

        binding.childrenListRV.adapter = adapter
        adapter.onChildClickListener = {
            onChildClickListener?.invoke(it)
            dialog?.dismiss()
        }
        adapter.onCopyClickListener = {
            val clipboard = requireContext().getSystemService(ClipboardManager::class.java)
            clipboard.setPrimaryClip(ClipData.newPlainText("text", "${it.fullName} ID: ${it.id}"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun Fragment.showSelectChildDF(onChildClickListener: (Child) -> Unit) {
            val df = SelectChildDF()
            df.setOnChildClickListener(onChildClickListener)
            df.show(parentFragmentManager, "SelectChildDF")
        }

    }

}