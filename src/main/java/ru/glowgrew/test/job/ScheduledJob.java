package ru.glowgrew.test.job;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public record ScheduledJob(String id, long deadline, String currencySymbol, CompletableFuture<Double> result,
                    List<Double> samples) {

}
