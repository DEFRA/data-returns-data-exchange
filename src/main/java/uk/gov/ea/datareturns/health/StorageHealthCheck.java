package uk.gov.ea.datareturns.health;

import com.codahale.metrics.health.HealthCheck;

import uk.gov.ea.datareturns.storage.StorageProvider;

/**
 * Created by graham on 18/04/16.
 */
public class StorageHealthCheck extends HealthCheck {

    private StorageProvider storageProvider;

    public StorageHealthCheck(StorageProvider storageProvider) {
        this.storageProvider = storageProvider;
    }

    @Override
    protected Result check() throws Exception {
//        File tempFile = null;
//        FileOutputStream fs = null;
//        byte[] randomBytes;
//
//        // Create a 1Kb temporary file in on the server to be written to storage
//        try {
//            // Neither this method nor any of its variants will return the same abstract pathname
//            // again in the current invocation of the virtual machine.
//            tempFile = File.createTempFile("healthcheck_", null);
//            tempFile.setWritable(true);
//            fs = new FileOutputStream(tempFile);
//            randomBytes = RandomUtils.nextBytes(1024);
//            fs.write(randomBytes);
//            fs.close();
//        } catch (IOException e) {
//            return Result.unhealthy("Cannot write temporary file on server: " + e.getLocalizedMessage());
//        }
//
//        // Try writing and retrieving from s3 (or local) storage
//
//
//        if (tempFile.exists()) {
//            tempFile.delete();
//        }
//
        return Result.healthy();
    }
}
