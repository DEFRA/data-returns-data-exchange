package uk.gov.ea.datareturns.domain.dto.impl;

/**
 * Created by graham on 16/08/16.
 */
public class DisplayHeaderDto {
    private final String field;
    private final String header;

    public DisplayHeaderDto(String field, String header) {
        this.field = field;
        this.header = header;
    }

    public String getField() {
        return field;
    }

    public String getHeader() {
        return header;
    }
}
