package jjhhyb.deepvalley.place;

import org.springframework.core.convert.converter.Converter;

public class StringToPlaceSortTypeConverter implements Converter<String, PlaceSortType> {

    @Override
    public PlaceSortType convert(String source) {
        return PlaceSortType.valueOf(source.toUpperCase());
    }
}
