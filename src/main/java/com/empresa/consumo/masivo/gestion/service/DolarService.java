package com.empresa.consumo.masivo.gestion.service;

import com.empresa.consumo.masivo.gestion.DTO.DolarTodayDTO;
import com.empresa.consumo.masivo.gestion.data.entity.Empresa;
import com.empresa.consumo.masivo.gestion.data.repository.EmpresaRepository;
import com.empresa.consumo.masivo.gestion.exception.BusinessServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class DolarService {

    private static final Logger log = LoggerFactory.getLogger(DolarService.class);

    @Autowired
    private DolarFacade dolarFacade;
    @Autowired
    private EmpresaRepository empresaRepository;

    @Scheduled(fixedRate = 1000000)
    public void getDollar() throws BusinessServiceException {

        String dolarTodayDTOString = dolarFacade.getDolar();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());
        DolarTodayDTO dolarTodayDTO = null;
        try {
            dolarTodayDTO = mapper.readValue(dolarTodayDTOString, DolarTodayDTO.class);
        } catch (IOException e) {
            throw new BusinessServiceException("Invalid dolar",e );
        }

        final Double dolarTodayDolar = dolarTodayDTO.getUsd().getDolartoday();
        List<Empresa> empresaList = empresaRepository.findByEnabledAndActualizarDolarAuto(Boolean.TRUE, Boolean.TRUE);

        empresaList.forEach(empresa -> empresa.setPrecioDolar(dolarTodayDolar));

        empresaRepository.saveAll(empresaList);

    }
}
