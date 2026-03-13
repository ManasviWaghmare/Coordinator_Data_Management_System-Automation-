package com.ticclub.service;

import com.ticclub.dto.TeamDTO;
import com.ticclub.model.Student;
import com.ticclub.model.Team;
import com.ticclub.repository.StudentRepository;
import com.ticclub.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TeamDTO getTeamById(Long id) {
        return teamRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public TeamDTO createTeam(TeamDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());

        if (teamDTO.getMemberIds() != null) {
            List<Student> members = studentRepository.findAllById(teamDTO.getMemberIds());
            team.setMembers(new HashSet<>(members));
        }

        return convertToDTO(teamRepository.save(team));
    }

    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        return teamRepository.findById(id).map(team -> {
            team.setName(teamDTO.getName());
            team.setDescription(teamDTO.getDescription());
            if (teamDTO.getMemberIds() != null) {
                List<Student> members = studentRepository.findAllById(teamDTO.getMemberIds());
                team.setMembers(new HashSet<>(members));
            }
            return convertToDTO(teamRepository.save(team));
        }).orElse(null);
    }

    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    private TeamDTO convertToDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setDescription(team.getDescription());
        if (team.getCreatedBy() != null) {
            dto.setCreatedById(team.getCreatedBy().getId());
            dto.setCreatedByUsername(team.getCreatedBy().getUsername());
        }
        if (team.getMembers() != null) {
            dto.setMemberIds(team.getMembers().stream()
                    .map(Student::getId)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
