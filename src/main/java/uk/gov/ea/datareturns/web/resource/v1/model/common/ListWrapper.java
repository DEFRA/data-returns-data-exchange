package uk.gov.ea.datareturns.web.resource.v1.model.common;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

/**
 * List wrapper utility class.
 *
 * @author Sam Gardner-Dell
 */
@JacksonXmlRootElement(localName = "list")
public class ListWrapper<T> {
    @JacksonXmlProperty(localName = "item")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<T> items;

    public ListWrapper() {
        this.items = new ArrayList<>();
    }

    public ListWrapper(List<T> items) {
        this.items = items;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
