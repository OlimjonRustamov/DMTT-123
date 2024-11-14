package uz.olimjon_rustamov.dmtt_123.children_list

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.olimjon_rustamov.dmtt_123.R
import uz.olimjon_rustamov.dmtt_123.databinding.FragmentChildrenListBinding
import uz.olimjon_rustamov.dmtt_123.model.Child


class ChildrenListFragment : Fragment() {

    private var _binding: FragmentChildrenListBinding? = null
    private val binding: FragmentChildrenListBinding get() = _binding!!
    private val adapter by lazy { ChildAdapter(true) }
    private val dbRef by lazy { Firebase.database.getReference("children") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChildrenListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.childrenListRV.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        dbRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val childrenList = mutableListOf<Child>()
                for (child in snapshot.children) {
                    child.getValue(Child::class.java)?.let { it1 -> childrenList.add(it1) }
                }
                adapter.setData(childrenList)
                binding.childrenListRV.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}

        })


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
        adapter.onCopyClickListener = {
            val clipboard = requireContext().getSystemService(ClipboardManager::class.java)
            clipboard.setPrimaryClip(ClipData.newPlainText("text", "${it.fullName} ID: ${it.id}"))
        }
        adapter.onDeleteClickListener = {
            dbRef.child(it.fullName.toString()).removeValue().addOnSuccessListener {
                Toast.makeText(requireContext(), getString(R.string.deleted), Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
            }
        }

    }

}