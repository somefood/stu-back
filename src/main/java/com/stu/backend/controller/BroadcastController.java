package com.stu.backend.controller;

import com.stu.backend.domain.Broadcast;
import com.stu.backend.dto.BroadcastDTO;
import com.stu.backend.dto.PageRequestDTO;
import com.stu.backend.dto.PageResultDTO;
import com.stu.backend.service.BroadcastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/broadcasts")
public class BroadcastController {

    private final BroadcastService broadcastService;

    @GetMapping
    public PageResultDTO<BroadcastDTO, Broadcast> getBroadcastList(PageRequestDTO pageRequestDTO) {
        log.info("getBroadcastList {}", pageRequestDTO);
        return broadcastService.findBroadCastList(pageRequestDTO);
    }

    @GetMapping("/{broadcastId}")
    public BroadcastDTO getBroadcast(@PathVariable Long broadcastId) {
        return null;
    }

    @PostMapping
    public Long createBroadcast(@RequestBody BroadcastDTO broadcastDTO) {
        return broadcastService.registerBroadcast(broadcastDTO);
    }

    @PutMapping("/{broadcastId}")
    public BroadcastDTO updateBroadcast(@PathVariable Long broadcastId) {
        return null;
    }

    @DeleteMapping("/{broadcastId}")
    public void closeBroadcast(@PathVariable Long broadcastId) {
    }
}
