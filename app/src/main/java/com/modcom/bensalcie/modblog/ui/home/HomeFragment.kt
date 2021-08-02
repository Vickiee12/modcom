package com.modcom.bensalcie.modblog.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import com.modcom.bensalcie.modblog.R
import com.modcom.bensalcie.modblog.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private lateinit var recyclerpost :RecyclerView
    private lateinit var mAdapter: PostsAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerpost = root.findViewById(R.id.recyclerpost)
        loadBlogPost()

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner, Observer {
//            textView.text = it
//        })
        return root
    }

    private fun loadBlogPost() {
        val query = FirebaseDatabase.getInstance("https://mod-blog-default-rtdb.firebaseio.com/").reference
            .child("MODCOMBLOG").child("BLOGS")
        val options = FirebaseRecyclerOptions.Builder<Post>().setQuery(query,Post::class.java).build()
        mAdapter = PostsAdapter(options)
        recyclerpost.apply { layoutManager = GridLayoutManager(context,2)
        adapter = mAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        mAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        if (mAdapter!=null){
            mAdapter.stopListening()
        }
    }
}