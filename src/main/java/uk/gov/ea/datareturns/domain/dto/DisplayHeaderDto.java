package uk.gov.ea.datareturns.domain.dto;

/**
 * Created by graham on 16/08/16.
 */
public class DisplayHeaderDto {
    private String field;
    private String header;

    public DisplayHeaderDto(String field, String header) {
        this.field = field;
        this.header = header;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
