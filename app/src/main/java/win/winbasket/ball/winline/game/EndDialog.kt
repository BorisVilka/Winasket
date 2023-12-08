package win.winbasket.ball.winline.game

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import win.winbasket.ball.winline.game.databinding.DialogBinding

class EndDialog(val balls: Int, val money: Int, val health: Int): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var adb = AlertDialog.Builder(requireContext())
        val binding = DialogBinding.inflate(layoutInflater,null,false)
        binding.label.text = if(health>0) "YOU WIN!" else "GAME OVER!"
        binding.ballsD.text = balls.toString()
        binding.moneyD.text = money.toString()
        binding.bombsD.text = "${3-health}/3"
        val navController = Navigation.findNavController(requireActivity(),R.id.fragmentContainerView)
        binding.homeD.setOnClickListener {
            SoundsManager.getInstance().startClickSound()
            dismiss()
            navController.popBackStack()
        }
        binding.repeatD.setOnClickListener {
            SoundsManager.getInstance().startClickSound()
            dismiss()
            navController.navigate(R.id.action_fragmentGame_self)
        }
        binding.setD.setOnClickListener {
            SoundsManager.getInstance().startClickSound()
            dismiss()
            navController.navigate(R.id.action_fragmentGame_to_settingsFragment)
        }
        adb = adb.setView(binding.root)
        val dialog = adb.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}