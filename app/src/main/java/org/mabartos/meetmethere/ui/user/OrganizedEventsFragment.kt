package org.mabartos.meetmethere.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.mabartos.meetmethere.R
import org.mabartos.meetmethere.data.user.User
import org.mabartos.meetmethere.databinding.FragmentOrganizedEventsBinding
import org.mabartos.meetmethere.service.event.EventService
import org.mabartos.meetmethere.service.event.EventServiceUtil
import org.mabartos.meetmethere.ui.list.EventsListAdapter

class OrganizedEventsFragment(
    private val eventService: EventService = EventServiceUtil.createService()
) : Fragment() {

    private lateinit var binding: FragmentOrganizedEventsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrganizedEventsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.organizedEventsToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.organizedEventsToolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.organizedEventsToolbar.title = resources.getString(R.string.my_organized_events)
        binding.organizedEventsEmpty.text = resources.getString(R.string.no_organized_events)

        val user: User = OrganizedEventsFragmentArgs.fromBundle(requireArguments()).user
        val organizedEventsId = user.organizedEvents

        if (organizedEventsId.isNotEmpty()) {
            binding.organizedEventsEmpty.visibility = View.GONE
            binding.organizedEventsList.visibility = View.VISIBLE

            val adapter = EventsListAdapter(
                onItemClick = { event ->
                    findNavController()
                        .navigate(
                            OrganizedEventsFragmentDirections.actionUserOrganizedEventsFragmentToDetailFragment(
                                event
                            )
                        )
                },
            )

            binding.organizedEventsList.layoutManager = LinearLayoutManager(requireContext())
            binding.organizedEventsList.adapter = adapter

            organizedEventsId.stream()
                .forEach { id ->
                    eventService.getEvent(
                        id,
                        onSuccess = { item -> adapter.addItem(item) },
                        onFailure = {})
                }
        } else {
            binding.organizedEventsList.visibility = View.GONE
            binding.organizedEventsEmpty.visibility = View.VISIBLE
        }
    }

}