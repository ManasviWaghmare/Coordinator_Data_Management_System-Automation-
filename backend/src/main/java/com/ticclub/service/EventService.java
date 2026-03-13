package com.ticclub.service;

import com.ticclub.dto.EventDTO;
import com.ticclub.model.Event;
import com.ticclub.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {
    @Autowired
    private EventRepository eventRepository;

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public EventDTO getEventById(Long id) {
        return eventRepository.findById(id)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public EventDTO createEvent(EventDTO dto) {
        Event event = new Event();
        mapToEntity(dto, event);
        return mapToDTO(eventRepository.save(event));
    }

    public EventDTO updateEvent(Long id, EventDTO dto) {
        return eventRepository.findById(id).map(event -> {
            mapToEntity(dto, event);
            return mapToDTO(eventRepository.save(event));
        }).orElse(null);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    private void mapToEntity(EventDTO dto, Event event) {
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setEventDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
    }

    private EventDTO mapToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        if (event.getCreatedBy() != null) {
            dto.setCreatedByUsername(event.getCreatedBy().getUsername());
        }
        return dto;
    }
}
