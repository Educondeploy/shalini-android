package com.tt.skolarrs.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tt.skolarrs.R
import com.tt.skolarrs.databinding.CardReportHistoryBinding
import com.tt.skolarrs.model.response.IndividualReportData

class ReportHistoryAdapter(
    var context: Context,
    var list: List<IndividualReportData>,
    var itemSelectListener: ReportItemSelectListener
) : RecyclerView.Adapter<ReportHistoryAdapter.ViewHolder>() {

    lateinit var subMenuListAdapter: SubMenuAdapter

    var count: Int = 1;
    var isPlay: Boolean = false;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            CardReportHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list[0].followups.size
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        count += 1
//        holder.binding.count.text= count.toString()
//        if (list[0].followups.isNotEmpty()) {
//           // var list1 = list[0].followups[position];
//            // Access elements from list1
//        } else {
//            // Handle the case when the followups list is empty
//        }
//
//        if (list[0].recordings.isNotEmpty()) {
////            var recordingList = list[0].recordings[position];
//            // Access elements from recordingList
//        } else {
//            // Handle the case when the recordings list is empty
//        }
//
//        Log.d("TAG", "onBindViewHolder: " + list[0].reports[position])
//
//        if (list[0].reports[position].report.isNotEmpty()  ) {
//
//            var reportList = list[0].reports[position].report[0];
//            if (reportList.currentStatus.isNotEmpty()) {
//                holder.binding.status.text = reportList.currentStatus
//            } else {
//                holder.binding.status.text = "-"
//            }
//
//            if (reportList.notes.isNotEmpty()) {
//                holder.binding.message.text = reportList.notes
//            } else {
//                holder.binding.message.text = "-"
//            }
//
//            if (reportList.callHistory.isNotEmpty()) {
//                holder.binding.nextFollowUpDate.text = reportList.callHistory
//            } else {
//                holder.binding.nextFollowUpDate.text = "-"
//            }
//            if (reportList.followUpDate.isNotEmpty()) {
//                holder.binding.createdDate.text = reportList.followUpDate
//            } else {
//                holder.binding.createdDate.text = "-"
//            }
//
//        } else {
//            // Handle the case when the reports list is empty or when the specified position is out of bounds
//        }
//
//        if (list.isNotEmpty() && position >= 0 && position < list.size) {
//            if (list[position].recordings[position].recordingURL.isEmpty()) {
//                holder.binding.playPauseButton.visibility = View.GONE;
//            } else {
//                holder.binding.playPauseButton.visibility = View.VISIBLE;
//            }
//        } else {
//            // Handle the case where the list is empty or the position is out of bounds.
//            // You can display an error message or take appropriate action.
//        }
//
//        if (!isPlay) {
//            holder.binding.playPauseButton.setImageResource(R.drawable.ic_pause)
//
//        }  else {
//            holder.binding.playPauseButton.setImageResource(R.drawable.ic_play)
//
//
//        }
//
//        holder.binding.playPauseButton.setOnClickListener(View.OnClickListener{
//            if(!isPlay) {
//                if (list[0].recordings.isNotEmpty() ) {
//                    isPlay = true;
//                    itemSelectListener.playAudion(list[0].recordings[position].recordingURL)
//                }
//            }
//            else {
////                if (list[0].recordings.isNotEmpty() ) {
////                    isPlay = false;
////                    itemSelectListener.pauseAudion(list[0].recordings[position].recordingURL)
////                }
//            }
//
//
//        })
//
//
//
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        count += 1
        holder.binding.count.text = count.toString()

        val item = list.getOrNull(0) // Get the first item in the list, or null if the list is empty
        item?.let { it ->
            if (it.followups.isNotEmpty() && position < it.followups.size) {
                // Access elements from it.followups[position]
            } else {
                // Handle the case when the followups list is empty or position is out of bounds
            }

            if (it.recordings.isNotEmpty() && position < it.recordings.size) {
                // Access elements from it.recordings[position]
            } else {
                // Handle the case when the recordings list is empty or position is out of bounds
            }

            Log.d("TAG", "onBindViewHolder: " + it.reports.getOrNull(position)?.report)

            it.reports.getOrNull(position)?.report?.getOrNull(0)?.let { reportList ->
                holder.binding.status.text =
                    reportList.currentStatus.takeIf { it.isNotEmpty() } ?: "-"
                holder.binding.message.text = reportList.notes.takeIf { it.isNotEmpty() } ?: "-"
                holder.binding.nextFollowUpDate.text =
                    reportList.callHistory.takeIf { it.isNotEmpty() } ?: "-"
                holder.binding.createdDate.text =
                    reportList.followUpDate.takeIf { it.isNotEmpty() } ?: "-"
            }

            if (it.recordings.isNotEmpty() && position < it.recordings.size) {
                if (it.recordings[position].recordingURL.isEmpty()) {
                    holder.binding.playPauseButton.visibility = View.GONE

                    holder.binding.audioSeekBar.visibility = View.GONE
                } else {
                    holder.binding.playPauseButton.visibility = View.VISIBLE

                    holder.binding.audioSeekBar.visibility = View.VISIBLE
                }
            } else {
                holder.binding.playPauseButton.visibility = View.GONE
                holder.binding.audioSeekBar.visibility = View.GONE
            }

            holder.binding.playPauseButton.setOnClickListener {
                if (!isPlay) {
                    if (list[0].recordings.isNotEmpty() && position < list[0].recordings.size) {
                        isPlay = true
                              if (isPlay) {
                        holder.binding.playPauseButton.setImageResource(R.drawable.ic_pause)

                    } else {
                        holder.binding.playPauseButton.setImageResource(R.drawable.ic_play)


                    }
                    itemSelectListener.playAudion(list[0].recordings[position].recordingURL)
                }
            } else {
            // Handle the case when isPlay is true
        }
        }
    }
}


class ViewHolder(itemView: CardReportHistoryBinding) : RecyclerView.ViewHolder(itemView.root) {
    var binding: CardReportHistoryBinding

    init {
        binding = itemView
    }
}

interface ReportItemSelectListener {
    fun onSelect(position: Int)
    fun unSelect(position: Int)

    fun playAudion(url: String)

    fun pauseAudion(url: String)

}
}




