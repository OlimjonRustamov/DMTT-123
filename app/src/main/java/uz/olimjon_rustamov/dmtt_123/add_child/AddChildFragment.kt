package uz.olimjon_rustamov.dmtt_123.add_child

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import uz.olimjon_rustamov.dmtt_123.R
import uz.olimjon_rustamov.dmtt_123.children_list.ChildrenListFragment
import uz.olimjon_rustamov.dmtt_123.databinding.FragmentAddChildBinding
import uz.olimjon_rustamov.dmtt_123.model.Child


class AddChildFragment : Fragment() {

    private var _binding: FragmentAddChildBinding? = null
    private val binding: FragmentAddChildBinding get() = _binding!!
    private val ref by lazy { Firebase.database.getReference("children") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddChildBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveBtn.setOnClickListener {
            val name = binding.childNameEt.text.toString()
            val id = binding.childIdEt.text.toString()
            if (name.isNotEmpty() && id.isNotEmpty()) {
                ref.child(name).setValue(Child(name, id)).addOnSuccessListener {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ChildrenListFragment()).commit()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.empty_data), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}