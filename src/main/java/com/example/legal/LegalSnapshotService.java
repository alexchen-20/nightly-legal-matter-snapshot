package com.example.legal;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public final class LegalSnapshotService {
    public record Matter(String id, String client, LocalDate deadline, boolean signedDocumentDelivered) {}

    /** Selects matters that need follow-up before writing the nightly snapshot. */
    public List<Matter> followUps(List<Matter> matters, LocalDate today) {
        return matters.stream()
                .filter(m -> !m.signedDocumentDelivered() && !m.deadline().isBefore(today))
                .collect(Collectors.toList());
    }

    public String snapshotJson(List<Matter> matters, LocalDate today) {
        return "{\"snapshot_date\":\"" + today + "\",\"follow_up_count\":" + followUps(matters, today).size() + "}";
    }

    public void runNightly(List<Matter> matters, LocalDate today, InfraiStorageClient storage, String bucket) throws Exception {
        storage.createBucket(bucket);
        storage.put(bucket, "legal/" + today + ".json", snapshotJson(matters, today));
    }
}
