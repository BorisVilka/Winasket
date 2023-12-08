package win.winbasket.ball.winline.game

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.setPadding
import androidx.navigation.Navigation
import win.winbasket.ball.winline.game.databinding.FragmentShopBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentShop.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentShop : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var binding: FragmentShopBinding
    val array = arrayOf(
        arrayOf(R.drawable.ball1,R.drawable.ball2,R.drawable.ball3,R.drawable.ball4,R.drawable.ball5,R.drawable.ball6,R.drawable.ball7,R.drawable.ball8,R.drawable.ball9,),
        arrayOf(R.drawable.basket1,R.drawable.basket2,R.drawable.basket3,R.drawable.basket4,R.drawable.basket5,R.drawable.basket6,R.drawable.basket7,R.drawable.basket8,R.drawable.basket9,)
    )
    var ind = 0
    var old = -1

    lateinit var views: Array<ImageView>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentShopBinding.inflate(inflater,container,false)
        binding.back.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.popBackStack()
        }
        views = arrayOf(binding.t1,binding.t2,binding.t3,binding.t4,binding.t5,binding.t6,binding.t7,binding.t8,binding.t9,)
        for(i in views.indices) {
            views[i].setOnClickListener {
                views[i].setBackgroundResource(R.drawable.corner2)
                views[i].setPadding(10)
                if(old!=-1) {
                    views[old].setPadding(0)
                    views[old].setBackgroundResource(android.R.color.transparent)
                }
                old = i
                requireActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE).edit()
                    .putInt(if(ind==0) "ball" else "basket",array[ind][i]).apply()
            }
        }
        binding.ballShop.setOnClickListener {
            ind = 0
            inval()
        }
        binding.basketShop.setOnClickListener {
            ind = 1
            inval()
        }
        inval()
        return binding.root
    }

    fun inval() {
        if(old!=-1) {
            views[old].setPadding(0)
            views[old].setBackgroundResource(android.R.color.transparent)
        }
        val tmp = requireActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE).getInt(if(ind==0) "ball" else "basket",if(ind==0) R.drawable.ball1 else R.drawable.basket1)
        for(i in array[ind].indices) {
            if(array[ind][i]==tmp) {
                old = i
                break
            }
        }
        views[old].setBackgroundResource(R.drawable.corner2)
        views[old].setPadding(10)
        binding.ballShop.setBackgroundResource(if(ind==0) R.drawable.shop1 else R.drawable.shop2)
        binding.basketShop.setBackgroundResource(if(ind==1) R.drawable.shop1 else R.drawable.shop2)
        for(i in views.indices) {
            views[i].setImageResource(array[ind][i])
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentShop.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentShop().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}