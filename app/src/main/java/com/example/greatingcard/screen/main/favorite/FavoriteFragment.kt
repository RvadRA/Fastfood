package com.example.greatingcard.screen.main.favorite
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greatingcard.R
import com.example.greatingcard.adapter.FavoriteAdapter
import com.example.greatingcard.model.MainViewModel
import com.example.greatingcard.model.ProductModel

class FavoriteFragment : Fragment(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var emptyFavoriteTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        recyclerView = view.findViewById(R.id.favoriteRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        emptyFavoriteTextView = view.findViewById(R.id.emptyFavoriteTextView)

        favoriteAdapter = FavoriteAdapter(requireContext(), emptyList(), object : FavoriteAdapter.FavoriteItemListener {
            override fun onRemoveClicked(product: ProductModel) {
                viewModel.removeFromFavorite(product)
            }

            override fun onAddToCartClicked(product: ProductModel) {
                viewModel.addToCart(product)
            }
            override fun onRemoveFromCartClicked(product: ProductModel) {
                viewModel.removeFromCart(product)
            }
        })

        recyclerView.adapter = favoriteAdapter
        viewModel.favoriteItems.observe(viewLifecycleOwner, Observer { favoriteItems ->
            favoriteAdapter.updateFavoriteItems(favoriteItems)
            updateEmptyFavoriteMessage(favoriteItems)
        })

        return view
    }

    private fun updateEmptyFavoriteMessage(favoriteItems: List<ProductModel>) {
        emptyFavoriteTextView.visibility = if (favoriteItems.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (favoriteItems.isEmpty()) View.GONE else View.VISIBLE
    }
}
