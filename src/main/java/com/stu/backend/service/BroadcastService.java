package com.stu.backend.service;

import com.stu.backend.domain.Broadcast;
import com.stu.backend.domain.User;
import com.stu.backend.dto.BroadcastDTO;
import com.stu.backend.dto.PageRequestDTO;
import com.stu.backend.dto.PageResultDTO;
import com.stu.backend.repository.BroadcastRepository;
import com.stu.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BroadcastService {

    private final BroadcastRepository broadcastRepository;
    private final UserRepository userRepository;

    public PageResultDTO<BroadcastDTO, Broadcast> findBroadCastList(PageRequestDTO pageRequestDTO) {
        Pageable pageable = pageRequestDTO.getPageable(Sort.by("endDateTime").descending());
        Page<Broadcast> result = broadcastRepository.findAll(pageable);

        Function<Broadcast, BroadcastDTO> fn = (this::entityToDto);

        return new PageResultDTO<>(result, fn);
    }

    public Long registerBroadcast(BroadcastDTO broadcastDTO) {
        Broadcast broadcast = dtoToEntity(broadcastDTO);

        broadcastRepository.save(broadcast);
        return null;
//        return broadcast.getId();
    }

    private Broadcast dtoToEntity(BroadcastDTO broadcastDTO) {
        return Broadcast.builder()
                .name(broadcastDTO.getName())
                .description(broadcastDTO.getDescription())
                .build();
    }

    private BroadcastDTO entityToDto(Broadcast broadcast) {
        return BroadcastDTO.builder()
                .id(broadcast.getId())
                .name(broadcast.getName())
                .description(broadcast.getDescription())
                .capacity(broadcast.getCapacity())
//                .username(broadcast.getUser().getEmail())
                .imageUrl(broadcast.getImageUrl())
                .build();
    }
}
