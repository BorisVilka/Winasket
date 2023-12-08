package win.winbasket.ball.winline.game

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import kotlinx.coroutines.launch
import win.winbasket.ball.winline.game.databinding.FragmentGameBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentGame.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentGame : Fragment() {
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
    
    private lateinit var binding: FragmentGameBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGameBinding.inflate(inflater,container,false)
        binding.back2.setOnClickListener {
            val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
            navController.popBackStack()
        }
        val prefs = requireActivity().getSharedPreferences("prefs",Context.MODE_PRIVATE)
        val bombs = arrayOf(binding.b1,binding.b2,binding.b3,)
        binding.textView.setOnTouchListener { view, motionEvent ->
            binding.textView.visibility = View.INVISIBLE
            binding.imageView10.visibility = View.INVISIBLE
            binding.imageView6.visibility = View.INVISIBLE
            binding.game.paused = false
            return@setOnTouchListener true
        }
        binding.game.setEndListener(object : GameView.Companion.EndListener {
            override fun end() {
                lifecycleScope.launch {
                    val set = prefs.getStringSet("score",HashSet<String>())!!
                    val set1 = HashSet<String>()
                    set1.addAll(set)
                    val s = (binding.game.gold+binding.game.balls).toString()
                    if(!set1.contains(s)) set1.add(s)
                    prefs.edit().putStringSet("score",set1).apply()

                    val dialog = EndDialog(binding.game.balls,binding.game.gold,binding.game.health)
                    dialog.isCancelable = false
                    dialog.show(requireActivity().supportFragmentManager,"END")
                }
            }

            override fun score(score: Int) {
               lifecycleScope.launch {
                   val tmp = 60-binding.game.millis/50
                   val s = "${String.format("%02d",tmp/60)}:${String.format("%02d",tmp%60)}"
                   binding.time.text = s
                   binding.balls.text = binding.game.balls.toString()
                   binding.gold.text = binding.game.gold.toString()
                   for(i in bombs.indices) {
                       if(i>=binding.game.health) {
                           bombs[i].visibility = View.INVISIBLE
                       }
                   }
               }
            }

        })
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentGame.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentGame().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}