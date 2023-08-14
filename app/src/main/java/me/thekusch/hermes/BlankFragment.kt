package me.thekusch.hermes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.thekusch.hermes.databinding.FragmentBlankBinding
import me.thekusch.messager.WiFiScanner

class BlankFragment : Fragment() {

    lateinit var scanner: WiFiScanner
    lateinit var binding: FragmentBlankBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBlankBinding.inflate(inflater, container, false)
        return binding.root
        //return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scanner = WiFiScanner(requireActivity())
        scanner.checkBluetoothPermissions()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        @JvmStatic
        fun newInstance() = BlankFragment()
    }
}