package com.example.legal;

import java.time.LocalDate;
import java.util.List;

public final class LegalSnapshotServiceTest {
    public static void main(String[] args) {
        var service = new LegalSnapshotService();
        var today = LocalDate.of(2026, 8, 13);
        var matters = List.of(
                new LegalSnapshotService.Matter("m-1", "Acme", today.plusDays(2), false),
                new LegalSnapshotService.Matter("m-2", "Beta", today.plusDays(2), true),
                new LegalSnapshotService.Matter("m-3", "Gamma", today.minusDays(1), false));
        if (service.followUps(matters, today).size() != 1) throw new AssertionError("only the open, upcoming matter needs follow-up");
        if (!service.snapshotJson(matters, today).contains("\"follow_up_count\":1")) throw new AssertionError("snapshot count");
        System.out.println("legal snapshot decision: PASS");
    }
}
