package net.squanchy.schedule.tracksfilter

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import net.squanchy.R
import net.squanchy.schedule.domain.view.Track
import net.squanchy.schedule.tracksfilter.widget.FilterChipView

internal class TracksFilterAdapter(
    context: Context,
    private val trackStateChangeListener: OnTrackSelectedChangeListener
) : ListAdapter<CheckableTrack, TrackViewHolder>(DiffCallback()) {

    init {
        setHasStableIds(true)
    }

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = layoutInflater.inflate(R.layout.track_filters_item, parent, false) as FilterChipView
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position), trackStateChangeListener)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).track().numericId
    }

    class DiffCallback : DiffUtil.ItemCallback<CheckableTrack>() {
        override fun areItemsTheSame(oldItem: CheckableTrack?, newItem: CheckableTrack?): Boolean {
            return oldItem?.track()?.id == newItem?.track()?.id
        }

        override fun areContentsTheSame(oldItem: CheckableTrack?, newItem: CheckableTrack?): Boolean {
            return oldItem == newItem
        }
    }
}

internal class TrackViewHolder(val item: FilterChipView) : RecyclerView.ViewHolder(item) {

    fun bind(checkableTrack: CheckableTrack, listener: OnTrackSelectedChangeListener) {
        val (track, selected) = checkableTrack

        item.apply {
            text = track.name

            color = when {
                track.accentColor.isPresent -> Color.parseColor(track.accentColor.get())
                else -> ContextCompat.getColor(context, R.color.chip_default_background_tint)
            }

            onCheckedChangeListener = { _, checked -> listener.invoke(track, checked) }
            isChecked = selected
        }
    }
}

internal typealias OnTrackSelectedChangeListener = (track: Track, selected: Boolean) -> Unit
