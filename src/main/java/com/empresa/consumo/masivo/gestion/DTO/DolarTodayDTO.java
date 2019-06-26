package com.empresa.consumo.masivo.gestion.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DolarTodayDTO  {
    @JsonAlias({ "USD"})
    private USD usd;
}


