package uz.olimjon_rustamov.dmtt_123

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import uz.olimjon_rustamov.dmtt_123.add_child.AddChildFragment
import uz.olimjon_rustamov.dmtt_123.check_list.CheckListFragment
import uz.olimjon_rustamov.dmtt_123.children_list.ChildrenListFragment
import uz.olimjon_rustamov.dmtt_123.databinding.ActivityMainBinding
import uz.olimjon_rustamov.dmtt_123.invoice_generator.InvoiceGeneratorFragment

private const val TAG_CHECK_LIST = "checkList"
private const val TAG_CHILDREN_LIST = "childrenList"
private const val TAG_ADD_CHILD = "addChild"
private const val TAG_INVOICE_GENERATOR = "invoiceGenerator"

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!

    private var currentFragment = TAG_CHILDREN_LIST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, CheckListFragment(), TAG_CHECK_LIST)
            .commit()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.checkListMenu -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, CheckListFragment(), TAG_CHECK_LIST)
                        .commit()
                }

                R.id.childrenListMenu -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ChildrenListFragment(), TAG_CHILDREN_LIST)
                        .commit()
                }

                R.id.addChildMenu -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, AddChildFragment(), TAG_ADD_CHILD)
                        .commit()
                }

                R.id.invoiceGeneratorMenu -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, InvoiceGeneratorFragment())
                        .commit()
                }
            }
            true
        }

        supportFragmentManager.addFragmentOnAttachListener { _, fragment ->
            when (fragment) {
                is CheckListFragment -> {
                    if (currentFragment != TAG_CHECK_LIST) {
                        currentFragment = TAG_CHECK_LIST
                        binding.bottomNavigationView.selectedItemId = R.id.checkListMenu
                    }
                }
                is ChildrenListFragment -> {
                    if (currentFragment != TAG_CHILDREN_LIST) {
                        currentFragment = TAG_CHILDREN_LIST
                        binding.bottomNavigationView.selectedItemId = R.id.childrenListMenu
                    }
                }

                is AddChildFragment -> {
                    if (currentFragment != TAG_ADD_CHILD) {
                        currentFragment = TAG_ADD_CHILD
                        binding.bottomNavigationView.selectedItemId = R.id.addChildMenu
                    }
                }

                is InvoiceGeneratorFragment -> {
                    if (currentFragment != TAG_INVOICE_GENERATOR) {
                        currentFragment = TAG_INVOICE_GENERATOR
                        binding.bottomNavigationView.selectedItemId = R.id.invoiceGeneratorMenu
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}