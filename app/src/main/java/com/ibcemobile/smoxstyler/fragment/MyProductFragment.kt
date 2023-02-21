package com.ibcemobile.smoxstyler.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.ibcemobile.smoxstyler.R
import com.ibcemobile.smoxstyler.activities.AddProductActivity
import com.ibcemobile.smoxstyler.activities.EditProductActivity
import com.ibcemobile.smoxstyler.adapter.ProductEditAdapter
import com.ibcemobile.smoxstyler.databinding.MyProductFragmentBinding
import com.ibcemobile.smoxstyler.responses.ProductResponse
import com.ibcemobile.smoxstyler.utils.shortToast
import com.ibcemobile.smoxstyler.viewmodels.ProductViewModel


class MyProductFragment : BaseFragment() {
    private lateinit var binding: MyProductFragmentBinding
    lateinit var productViewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = MyProductFragmentBinding.inflate(inflater, container, false)

        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        val adapter = ProductEditAdapter()
        adapter.setItemClickListener(object: ProductEditAdapter.ItemClickListener{
            override fun onItemClick(view: View, product: ProductResponse.ProductItem) {
                if(view.id == R.id.btnDelete){
                    showConfirmDeleteProduct(product.id)
                }else{
                    openProductEditPage(product)
                }

            }
        })

        binding.rvProducts.layoutManager= GridLayoutManager(requireActivity(),2)

        binding.rvProducts.adapter = adapter
        productViewModel.productRes.observe(viewLifecycleOwner, Observer { products ->
            if (products != null) {
                if(products.result.isNotEmpty()){
                    adapter.submitList(products.result)
                    binding.txtBtNoProduct.visibility = View.GONE
                    binding.rvProducts.visibility = View.VISIBLE
                } else {
                    //Toast.makeText(context, resources.getString(R.string.styler_product_no_found), Toast.LENGTH_LONG).show()
                    binding.txtBtNoProduct.visibility = View.VISIBLE
                    binding.rvProducts.visibility = View.GONE
                }
            }
        })


        binding.fabAddProduct.setOnClickListener {

            //showDialogForUSPSKEY(requireActivity())

            if (sessionManager.sellProducts) {
                val intent = Intent(requireActivity(), AddProductActivity::class.java)
                startActivity(intent)
            }else {
                shortToast("Please enable selling feature from Profile->Update Address")
            }
        }

        return binding.root
    }


    private fun showDialogForUSPSKEY(
        context: Context
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.add_usps_key_dailog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val txtWarning = dialog.findViewById<TextView>(R.id.txtWarning)
        val etUSPSId = dialog.findViewById<AppCompatEditText>(R.id.etUSPSId)
        val txtApprove = dialog.findViewById<TextView>(R.id.txtApprove)
        val imgClose = dialog.findViewById<ImageView>(R.id.imgClose)


        txtWarning.setOnClickListener {
            dialog.dismiss()
            val bundle = Bundle()
            bundle.putString("url", "https://registration.shippingapis.com/")

            NavHostFragment.findNavController(this@MyProductFragment)
                .navigate(R.id.to_webViewFragment, bundle)
        }
        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        txtApprove.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(context, AddProductActivity::class.java)
            context.startActivity(intent)

        }
        dialog.show()
    }
    private fun openProductEditPage(product: ProductResponse.ProductItem) {
        val intent = Intent(requireActivity(), EditProductActivity::class.java)
        intent.putExtra("product", product)
        intent.putExtra("is_new_product", false)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
    }
    private fun showConfirmDeleteProduct(productId:Int){
        val builder = AlertDialog.Builder(requireActivity(),R.style.MyAlertDialogStyle)
        builder.setTitle("DELETE PRODUCT")
        builder.setMessage("Are you sure you want to delete the selected product?")
        builder.setPositiveButton("DELETE") { _, _ ->
            productViewModel.deleteProduct(requireActivity(),progressHUD, productId, app.currentUser.id.toString())

        }
        builder.setNegativeButton("CANCEL", null)
        builder.show()
    }

    override fun onResume() {
        super.onResume()
        productViewModel.getMyProducts(requireActivity(), progressHUD)
    }

}