package org.eval.moretech.linestatistics.feign;

import org.eval.moretech.linestatistics.entity.BranchScheduleDto;
import org.eval.moretech.linestatistics.entity.FindNearestRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "VtbApiBranch", url = "${places.api.url}")
public interface BranchFeignClient {

    @GetMapping("/places/branches/schedules")
    List<BranchScheduleDto> getAllBranches();

    @PostMapping("/places/branches/schedules/filtered")
    List<BranchScheduleDto> getFilteredBranches(FindNearestRequest request);
}
