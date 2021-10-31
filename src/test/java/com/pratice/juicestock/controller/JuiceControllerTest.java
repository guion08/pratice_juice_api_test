package com.pratice.juicestock.controller;

import com.pratice.juicestock.builder.JuiceDTOBuilder;
import com.pratice.juicestock.dto.JuiceDTO;
import com.pratice.juicestock.dto.QuantityDTO;
import com.pratice.juicestock.exception.JuiceNotFoundException;
import com.pratice.juicestock.service.JuiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static com.pratice.juicestock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class JuiceControllerTest {

    private static final String JUICE_API_URL_PATH = "api/v1/juices";
    private static final long VALID_JUICE_ID = 1L;
    private static final long INVALID_JUICE_ID = 2L;
    private static final String JUICE_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String JUICE_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;
    
    @Mock
    private JuiceService juiceService;
    
    @InjectMocks
    private JuiceController juiceController;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(juiceController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }
    
    @Test
    void whenPOSTIsCalledThenAJuiceIsCreated() throws Exception {
        // given
        JuiceDTO juiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        
        // when
        when(juiceService.createJuice(juiceDTO)).thenReturn(juiceDTO);
        
        // then
        mockMvc.perform(post(JUICE_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(juiceDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(juiceDTO.getName())))
                .andExpect(jsonPath("$.brand", is(juiceDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(juiceDTO.getType().toString())));
    }
    
    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        JuiceDTO juiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        juiceDTO.setBrand(null);
        
        // then
        mockMvc.perform(post(JUICE_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(juiceDTO)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        JuiceDTO juiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        
        // when
        when(juiceService.findByName(juiceDTO.getName())).thenReturn(juiceDTO);
        
        // then
        mockMvc.perform(MockMvcRequestBuilders.get(JUICE_API_URL_PATH + "/" + juiceDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(juiceDTO.getName())))
                .andExpect(jsonPath("$.brand", is(juiceDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(juiceDTO.getType().toString())));
    }
    
    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        JuiceDTO juiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        
        // when
        when(juiceService.findByName(juiceDTO.getName())).thenThrow(JuiceNotFoundException.class);
        
        // then
        mockMvc.perform(MockMvcRequestBuilders.get(JUICE_API_URL_PATH + "/" + juiceDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void whenGETListWithJuicesIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        JuiceDTO juiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        
        // when
        when(juiceService.listAll()).thenReturn(Collections.singletonList(juiceDTO));
        
        // then
        mockMvc.perform(MockMvcRequestBuilders.get(JUICE_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].name", is(juiceDTO.getName())))
                .andExpect(jsonPath("[0].brand", is(juiceDTO.getBrand())))
                .andExpect(jsonPath("[0].type", is(juiceDTO.getType().toString())));
    }
    
    @Test
    void whenGETListWithoutJuicesIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        JuiceDTO juiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        
        // when
        when(juiceService.listAll()).thenReturn(Collections.singletonList(juiceDTO));
        
        // then
        mockMvc.perform(MockMvcRequestBuilders.get(JUICE_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    
    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        JuiceDTO juiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        
        // when
        doNothing().when(juiceService).deleteById(juiceDTO.getId());
        
        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(JUICE_API_URL_PATH + "/" + juiceDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        // when
        doThrow(JuiceNotFoundException.class).when(juiceService).deleteById(INVALID_JUICE_ID);
        
        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(JUICE_API_URL_PATH + "/" + INVALID_JUICE_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOkStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();
        
        JuiceDTO juiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
        juiceDTO.setQuantity(juiceDTO.getQuantity() + quantityDTO.getQuantity());
        
        when(juiceService.increment(VALID_JUICE_ID, quantityDTO.getQuantity())).thenReturn(juiceDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(JUICE_API_URL_PATH + "/" + VALID_JUICE_ID + JUICE_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(juiceDTO.getName())))
                .andExpect(jsonPath("$.brand", is(juiceDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(juiceDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(juiceDTO.getQuantity())));
    }
    
    // Configurações opcionais

//    @Test
//    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(30)
//                .build();
//
//        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
//        JuiceDTO.setQuantity(JuiceDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(JuiceService.increment(VALID_Juice_ID, quantityDTO.getQuantity())).thenThrow(JuiceStockExceededException.class);
//
//        mockMvc.perform(patch(Juice_API_URL_PATH + "/" + VALID_Juice_ID + Juice_API_SUBPATH_INCREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .con(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
//    }

//    @Test
//    void whenPATCHIsCalledWithInvalidJuiceIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(30)
//                .build();
//
//        when(JuiceService.increment(INVALID_Juice_ID, quantityDTO.getQuantity())).thenThrow(JuiceNotFoundException.class);
//        mockMvc.perform(patch(Juice_API_URL_PATH + "/" + INVALID_Juice_ID + Juice_API_SUBPATH_INCREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(5)
//                .build();
//
//        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
//        JuiceDTO.setQuantity(JuiceDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(JuiceService.decrement(VALID_Juice_ID, quantityDTO.getQuantity())).thenReturn(JuiceDTO);
//
//        mockMvc.perform(patch(Juice_API_URL_PATH + "/" + VALID_Juice_ID + Juice_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
//                .andExpect(jsonPath("$.name", is(JuiceDTO.getName())))
//                .andExpect(jsonPath("$.brand", is(JuiceDTO.getBrand())))
//                .andExpect(jsonPath("$.type", is(JuiceDTO.getType().toString())))
//                .andExpect(jsonPath("$.quantity", is(JuiceDTO.getQuantity())));
//    }
//
//    @Test
//    void whenPATCHIsCalledToDEcrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(60)
//                .build();
//
//        JuiceDTO JuiceDTO = JuiceDTOBuilder.builder().build().toJuiceDTO();
//        JuiceDTO.setQuantity(JuiceDTO.getQuantity() + quantityDTO.getQuantity());
//
//        when(JuiceService.decrement(VALID_Juice_ID, quantityDTO.getQuantity())).thenThrow(JuiceStockExceededException.class);
//
//        mockMvc.perform(patch(Juice_API_URL_PATH + "/" + VALID_Juice_ID + Juice_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void whenPATCHIsCalledWithInvalidJuiceIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
//        QuantityDTO quantityDTO = QuantityDTO.builder()
//                .quantity(5)
//                .build();
//
//        when(JuiceService.decrement(INVALID_Juice_ID, quantityDTO.getQuantity())).thenThrow(JuiceNotFoundException.class);
//        mockMvc.perform(patch(Juice_API_URL_PATH + "/" + INVALID_Juice_ID + Juice_API_SUBPATH_DECREMENT_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(asJsonString(quantityDTO)))
//                .andExpect(status().isNotFound());
//    }
}
