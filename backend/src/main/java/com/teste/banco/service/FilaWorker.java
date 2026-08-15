package com.teste.banco.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FilaWorker {

    private final FilaService filaService;

    public FilaWorker(FilaService filaService) {
        this.filaService = filaService;
    }

    @Scheduled(fixedDelay = 15000)
    public void processar() {
        filaService.processarProxima();
    }
}
