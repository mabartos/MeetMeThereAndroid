package org.mabartos.meetmethere.util

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import org.mabartos.meetmethere.data.event.EventsListItem

class ClusterEventItem(
    lat: Double,
    lng: Double,
    private val title: String,
    private val snippet: String,
    val event: EventsListItem
) : ClusterItem {

    private val position: LatLng = LatLng(lat, lng)

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

}