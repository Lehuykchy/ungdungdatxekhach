package com.example.ungdungdatxekhach.user.fragment

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ungdungdatxekhach.R
import com.example.ungdungdatxekhach.databinding.FragmentRouteDefaultBuyticketStep3Binding
import com.example.ungdungdatxekhach.user.model.Ticket
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class RouteDefaultBuyTicketStep3 : Fragment() {
    private lateinit var binding : FragmentRouteDefaultBuyticketStep3Binding
    private var countdownTimer: CountDownTimer? = null
    private val COUNTDOWN_TIME = TimeUnit.MINUTES.toMillis(15)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRouteDefaultBuyticketStep3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imgBuyTicketStep3NameBack.setOnClickListener {
            val navController = activity?.findNavController(R.id.framelayout)
            navController?.popBackStack()
        }
        startCountdownTimer()
    }

    private fun startCountdownTimer() {
        countdownTimer = object : CountDownTimer(COUNTDOWN_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Thời gian còn lại
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                val formattedTime = String.format("%02d:%02d", minutes, seconds)

                // Hiển thị thời gian còn lại trong TextView
                binding.tvBuyTicketStep3NameCountTime.text = formattedTime
            }

            override fun onFinish() {
                // Đồng hồ đếm ngược kết thúc, xử lý tại đây (ví dụ: hủy vé)
                binding.tvBuyTicketStep3NameCountTime.text = "00:00"
                // Xử lý khi hết thời gian, ví dụ: hủy vé
                cancelTicket()
            }
        }

        countdownTimer?.start()
    }

    private fun cancelTicket() {
        // Thực hiện hủy vé ở đây
    }

    override fun onDestroy() {
        super.onDestroy()
        // Đảm bảo dừng đồng hồ đếm ngược khi hoạt động bị hủy
        countdownTimer?.cancel()
    }
}