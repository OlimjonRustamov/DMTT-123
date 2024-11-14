package uz.olimjon_rustamov.dmtt_123.invoice_generator

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.olimjon_rustamov.dmtt_123.R
import uz.olimjon_rustamov.dmtt_123.databinding.FragmentInvoiceGeneratorBinding
import uz.olimjon_rustamov.dmtt_123.invoice_generator.SelectChildDF.Companion.showSelectChildDF
import uz.olimjon_rustamov.dmtt_123.model.Child


class InvoiceGeneratorFragment : Fragment() {

    private var _binding: FragmentInvoiceGeneratorBinding? = null
    private val binding: FragmentInvoiceGeneratorBinding get() = _binding!!
    private var bitmap: Bitmap? = null
    private var child: Child? = null
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInvoiceGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.childNameTv.setOnClickListener {
            showSelectChildDF {
                child = it
                binding.childNameTv.text = it.fullName
                binding.invoiceIv.setImageBitmap(generateImage())
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            binding.yearEt.addTextChangedListener {
                onTextChanged()
            }
            binding.monthEt.addTextChangedListener {
                onTextChanged()
            }
            binding.sumEt.addTextChangedListener {
                onTextChanged()
            }
        }
        binding.shareBtn.setOnClickListener {
            if (bitmap == null) {
                Toast.makeText(requireContext(), getString(R.string.empty_data), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //share image
            val path = MediaStore.Images.Media.insertImage(requireActivity().contentResolver, bitmap,"Invoice Student name", null);
            val bitmapUri = Uri.parse(path);
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, bitmapUri)
            }
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }
    }

    private fun generateImage(): Bitmap {
        val templateBitmap = BitmapFactory.decodeResource(resources, R.drawable.invoice_template)
        val mutableBitmap = templateBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val paintNameID = Paint().apply {
            color = Color.BLACK
            textSize = templateBitmap.height * 0.066f
        }
        val paintDetails = Paint().apply {
            color = Color.BLACK
            textSize = templateBitmap.height * 0.048f
        }

        val xName = templateBitmap.width * 0.367f
        val yName = templateBitmap.height * 0.59f
        val yDetails = templateBitmap.height * 0.72f

        // Draw student name and ID on the canvas
        child?.let {
            canvas.drawText(it.fullName ?: "", xName, yName, paintNameID)
            canvas.drawText(it.id ?: "", templateBitmap.width * 0.72f, yName, paintNameID)
            canvas.drawText(binding.monthEt.text.toString(), xName, yDetails, paintDetails)
            canvas.drawText(binding.yearEt.text.toString(), templateBitmap.width * 0.5f, yDetails, paintDetails)
            canvas.drawText(binding.sumEt.text.toString(), templateBitmap.width * 0.84f, yDetails, paintDetails)
        }
        bitmap = mutableBitmap
        return mutableBitmap
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun onTextChanged() {
        job?.cancel()
        job = viewLifecycleOwner.lifecycleScope.launch {
            delay(300)
            binding.invoiceIv.setImageBitmap(generateImage())
        }
    }

}