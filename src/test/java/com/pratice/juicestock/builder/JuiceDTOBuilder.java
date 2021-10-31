package com.pratice.juicestock.builder;

import com.pratice.juicestock.dto.JuiceDTO;
import com.pratice.juicestock.enums.JuiceType;
import lombok.Builder;

@Builder
public class JuiceDTOBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String name = "Apple juice";

    @Builder.Default
    private String brand = "Natural";

    @Builder.Default
    private int max = 50;

    @Builder.Default
    private int quantity = 10;

    @Builder.Default
    private JuiceType type = JuiceType.APPLE;

    public JuiceDTO toJuiceDTO() {
        return new JuiceDTO(id,
                name,
                brand,
                max,
                quantity,
                type);
    }
}
