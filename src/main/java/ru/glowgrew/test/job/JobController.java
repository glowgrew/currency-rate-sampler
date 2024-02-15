package ru.glowgrew.test.job;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
class JobController {

    private final JobService testService;

    public JobController(JobService testService) {
        this.testService = testService;
    }

    @PostMapping
    public ResponseEntity<String> submitJob(@RequestBody SubmitJobRequest request) {
        var scheduledJob = testService.submitJob(request);
        return ResponseEntity.ok(scheduledJob.id());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResult> getStatus(@PathVariable String id) {
        return testService.findResult(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
