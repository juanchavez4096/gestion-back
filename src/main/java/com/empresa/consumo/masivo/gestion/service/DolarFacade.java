package com.empresa.consumo.masivo.gestion.service;

import com.empresa.consumo.masivo.gestion.DTO.DolarTodayDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "data", url = "https://s3.amazonaws.com/dolartoday")
public interface DolarFacade {
    @RequestMapping(value = "data.json",method = RequestMethod.GET)
    String getDolar();
}