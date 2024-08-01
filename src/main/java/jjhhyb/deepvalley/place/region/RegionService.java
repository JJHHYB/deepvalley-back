package jjhhyb.deepvalley.place.region;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public List<String> getRegionNameList() {
        List<Region> regions = regionRepository.findAll();
        return regions.stream().map(Region::getName).toList();
    }
}
