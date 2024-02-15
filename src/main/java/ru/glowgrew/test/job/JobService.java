package ru.glowgrew.test.job;

import ru.glowgrew.test.coindesk.CoindeskApi;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Service
public class JobService {

    private static final long SAMPLE_RATE = TimeUnit.MINUTES.toMillis(5);

    private final CoindeskApi coindeskApi;
    private final List<ScheduledJob> jobs = new CopyOnWriteArrayList<>();

    public JobService(CoindeskApi coindeskApi) {
        this.coindeskApi = coindeskApi;
    }

    public ScheduledJob submitJob(SubmitJobRequest request) {
        var job = new ScheduledJob(UUID.randomUUID().toString(),
                System.currentTimeMillis() + SAMPLE_RATE,
                request.currencySymbol(),
                new CompletableFuture<>(),
                new CopyOnWriteArrayList<>());
        jobs.add(job);
        return job;
    }

    @Scheduled(fixedRate = 1_000)
    public void cleanUpJobs() {
        jobs.removeIf(job -> System.currentTimeMillis() >= job.deadline() + TimeUnit.MINUTES.toMillis(30));
    }

    @Scheduled(fixedRate = 30_000)
    public void updateJobs() {
        var millis = System.currentTimeMillis();
        for (var job : jobs) {
            if (millis < job.deadline()) {
                var currentPrice = coindeskApi.getCurrentPrice();
                var symbol = job.currencySymbol().toLowerCase();
                var currency = currentPrice.bpi().get(symbol.toUpperCase());
                job.samples().add(currency.currencyRate());
            } else {
                if (!job.result().isDone()) {
                    computeAvgRate(job.samples()).ifPresentOrElse(value -> job.result().complete(value),
                            () -> job.result().completeExceptionally(new IllegalStateException()));
                }
            }
        }
    }

    public Optional<JobResult> findResult(String id) {
        return jobs.stream()
                .filter(job -> id.equalsIgnoreCase(job.id()))
                .map(job -> {
                    var result = job.result();
                    if (result.isCompletedExceptionally()) {
                        return new JobResult(JobStatus.FAILURE, -1);
                    }
                    if (result.isDone()) {
                        return new JobResult(JobStatus.SUCCESS, result.join());
                    }
                    return new JobResult(JobStatus.IN_PROGRESS, -1);
                }).findFirst();
    }

    private OptionalDouble computeAvgRate(List<Double> samples) {
        return samples.stream().mapToDouble(value -> value).average();
    }

}
