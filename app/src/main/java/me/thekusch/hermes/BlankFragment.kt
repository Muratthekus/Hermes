package me.thekusch.hermes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import me.thekusch.hermes.databinding.FragmentBlankBinding
import me.thekusch.messager.WiFiScanner
import me.thekusch.messager.wifi.model.Status

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
        scanner.startReceiver(requireActivity())

        scanner.onP2pStatusChange = { status ->
            when (status) {
                is Status.DiscoverProcess -> {
                    Log.d("WIFISCANNER DISCOVERING", status.result.toString())
                }

                is Status.PeersDiscovered -> {
                    Log.d("WIFISCANNER DISCOVERED", status.wifiDevice.toString())
                }

                is Status.ConnectionProcess -> {
                    Log.d("WIFISCANNER CONNECTION", status.result.toString())

                }

                else -> {

                }
            }
        }
        binding.button2.setOnClickListener {
            scanner.discoverOthers()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        scanner.stopReceiver(requireActivity())
    }

    companion object {
        @JvmStatic
        fun newInstance() = BlankFragment()
    }
}