package uz.olimjon_rustamov.dmtt_123.check_list

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.olimjon_rustamov.dmtt_123.databinding.FragmentCheckListBinding
import uz.olimjon_rustamov.dmtt_123.model.Child
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

class CheckListFragment : Fragment() {

    private var _binding: FragmentCheckListBinding? = null
    private val binding: FragmentCheckListBinding get() = _binding!!
    private val adapter by lazy { CheckListChildAdapter() }

    private val childrenRef by lazy { Firebase.database.getReference("children") }
    private val checkListRef by lazy { Firebase.database.getReference("check_list") }

    private var date = Date()
    private var year = LocalDate.now().year
    private var month = LocalDate.now().monthValue - 1
    private var day = LocalDate.now().dayOfMonth
    private var formatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    private val currentDate get() = formatter.format(date)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCheckListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCurrentDate.text = currentDate
        binding.rvChildren.adapter = adapter
        adapter.onCheckboxChangeListener = { child, b ->
            checkListRef.child(currentDate).child(child.first).setValue(if (b) "+" else "-")
        }
        loadChildrenList()
        binding.ivNextDay.setOnClickListener {
            val tomorrow = LocalDate.of(year, month + 1, day).plusDays(1)
            year = tomorrow.year
            month = tomorrow.monthValue - 1
            day = tomorrow.dayOfMonth
            date = GregorianCalendar(year, month, day).time
            binding.tvCurrentDate.text = formatter.format(date)
            loadChildrenList()
        }
        binding.ivPreviousDay.setOnClickListener {
            val tomorrow = LocalDate.of(year, month + 1, day).minusDays(1)
            year = tomorrow.year
            month = tomorrow.monthValue - 1
            day = tomorrow.dayOfMonth
            date = GregorianCalendar(year, month, day).time
            binding.tvCurrentDate.text = formatter.format(date)
            loadChildrenList()
        }
        binding.tvCurrentDate.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(), { _, year, month, dayOfMonth ->
                    date = GregorianCalendar(year, month, dayOfMonth).time
                    this.year = year
                    this.month = month
                    this.day = dayOfMonth
                    binding.tvCurrentDate.text = formatter.format(date)
                    loadChildrenList()
                },
                year, month, day
            )
            dialog.show()
        }
    }

    private fun loadChildrenList() {
        binding.rvChildren.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE

        checkListRef.child(currentDate).get().addOnSuccessListener { snapshot ->

            val checkList = mutableListOf<Pair<String, Boolean>>()
            for (child in snapshot.children) {
                child.getValue(String::class.java)?.let { checked ->
                    checkList.add(child.key.toString() to checked.contains("+"))
                }
            }
            if (checkList.isEmpty()) {
                childrenRef.get().addOnSuccessListener { children ->
                    for (child in children.children) {
                        child.getValue(Child::class.java)?.let {
                            checkListRef.child(currentDate).child(it.fullName!!).setValue("-")
                        }
                    }
                    loadChildrenList()
                }
            } else {
                adapter.setData(checkList)
                try {
                    binding.rvChildren.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                } catch (_: Exception) { }
            }
        }.addOnFailureListener {
            binding.rvChildren.visibility = View.GONE
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}