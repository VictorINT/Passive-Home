package com.passivehouse.PassiveHouse.SSE;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@RestController
//@CrossOrigin(origins = "*")
public class PirSSEController {
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/sse")
    public SseEmitter streamEvents() {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    public void sendAlarmEvent() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("ALARM").data("Motion detected!"));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }
}
