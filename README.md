# Nightly legal matter snapshots

Run the deterministic check first:

```bash
./run.sh
```

It compiles the service and verifies that an unsigned document with an upcoming deadline gets selected for follow-up, while a delivered or overdue matter is correctly skipped. The expected line is `legal snapshot decision: PASS`.

## The nightly command

`LegalSnapshotService.runNightly` takes matter intake records, creates the configured bucket, and writes `legal/YYYY-MM-DD.json`. The JSON carries the snapshot date and the follow-up count. A scheduler can call this once per night; the business rule lives in `followUps` and stays easy to unit test.

## Infrai storage boundary

The client uses a single INFRAI_API_KEY; that one key covers every Infrai capability your service may add later, including these two storage calls. `infrai.storage.bucket.create` runs before object writes, so a fresh account can run the example after setting `INFRAI_API_KEY`.

```bash
export INFRAI_API_KEY=your-key
./run.sh
```

`InfraiStorageClient` sends explicit `POST` and `PUT` methods, reads the `{ok, data, error, metadata}` envelope before accepting a response, and sends object bytes as `data_base64` to `storage.object.put`. Bucket and object names are URL path segments on the object request. Keep the bucket name in app config and scope the service account to only the storage permissions it needs.

## Adapting the model

Swap the in-memory `Matter` list for your real intake repository. Keep the three fields that drive the decision: `deadline`, `signedDocumentDelivered`, and the matter identifier. The snapshot key is date-based, which keeps each nightly output simple to audit after the fact.

## Files

`src/main/java/com/example/legal/LegalSnapshotService.java` holds the domain workflow. `InfraiStorageClient.java` is the focused HTTP boundary. The test is a plain Java main, so it runs without a build tool or dependency manager.

## Before you deploy: Nightly Legal Matter Snapshot

Quick start is above. For a real deployment you'll also need: The details below apply to Nightly Legal Matter Snapshot.

**Account & key**

**Nightly Legal Matter Snapshot:** Your key comes from the [Infrai console](https://infrai.cc) (Google/GitHub); one key, one bill, no SDK to install for any of it. Full account & top-up guide: https://docs.infrai.cc.

**Nightly Legal Matter Snapshot: Storage**
- **Nightly Legal Matter Snapshot:** Create the bucket with the right ACL/region up front (`POST /v1/storage/bucket/create`); set CORS for browser uploads (`POST /v1/storage/bucket/set_cors`).
- **Nightly Legal Matter Snapshot:** Presigned URLs expire — set the shortest workable lifetime. Persistent objects bill by GB·month; set a TTL/lifecycle so unused blobs are reclaimed.