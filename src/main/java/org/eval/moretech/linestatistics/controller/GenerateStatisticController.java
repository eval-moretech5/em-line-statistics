package org.eval.moretech.linestatistics.controller;

import lombok.RequiredArgsConstructor;
import org.eval.moretech.linestatistics.entity.GenerateStatisticRequest;
import org.eval.moretech.linestatistics.service.LineStatGenerationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/line")
@RequiredArgsConstructor
public class GenerateStatisticController {

    private final LineStatGenerationService generationService;

    @PostMapping("/filtered-init")
    public void initFiltered(@RequestBody GenerateStatisticRequest request) {
        generationService.generate(request);
    }
}
